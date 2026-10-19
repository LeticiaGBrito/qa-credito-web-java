package credito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Servidor independente do simulador educacional.
 * Entrega a interface HTML e recebe solicitações de análise.
 */
public class CreditoWebApplication implements AutoCloseable {

    private static final int LIMITE_CORPO = 65_536;

    private final HttpServer servidor;
    private final byte[] paginaHtml;

    private final AnaliseCreditoService service =
            new AnaliseCreditoService();

    private final Gson gson = new GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .create();

    /**
     * Inicializa o servidor local em uma porta fixa ou disponível.
     * A porta zero permite ao sistema escolher uma porta livre.
     */
    public CreditoWebApplication(int porta) throws IOException {
        this("127.0.0.1", porta);
    }

    /**
     * Permite configurar host e porta para execução na hospedagem.
     */
    public CreditoWebApplication(
            String host,
            int porta
    ) throws IOException {
        // Carrega o HTML pelo classpath, funcionando também
        // quando a aplicação estiver empacotada.
        try (var arquivo = CreditoWebApplication.class
                .getResourceAsStream("/web/index.html")) {

            if (arquivo == null) {
                throw new IOException(
                        "Recurso /web/index.html não encontrado."
                );
            }

            paginaHtml = arquivo.readAllBytes();
        }

        servidor = HttpServer.create(
                new InetSocketAddress(host, porta),
                0
        );

        servidor.createContext("/", this::atenderRequisicao);
    }

    public void iniciar() {
        servidor.start();
    }

    /**
     * Endereço de acesso local, utilizado também pelos testes.
     */
    public String endereco() {
        return "http://127.0.0.1:"
                + servidor.getAddress().getPort();
    }

    /**
     * Direciona cada caminho para seu comportamento.
     */
    private void atenderRequisicao(
            HttpExchange requisicao
    ) throws IOException {
        try {
            String caminho = requisicao.getRequestURI().getPath();

            switch (caminho) {
                case "/simulador" -> {
                    if (validarMetodo(requisicao, "GET")) {
                        responder(
                                requisicao,
                                200,
                                "text/html; charset=UTF-8",
                                paginaHtml
                        );
                    }
                }

                case "/health" -> {
                    if (validarMetodo(requisicao, "GET")) {
                        responderJson(
                                requisicao,
                                200,
                                Map.of("status", "UP")
                        );
                    }
                }

                case "/analises-credito" -> {
                    if (validarMetodo(requisicao, "POST")) {
                        analisarCredito(requisicao);
                    }
                }

                default -> responderJson(
                        requisicao,
                        404,
                        Map.of("erro", "Endpoint não encontrado.")
                );
            }
        } finally {
            requisicao.close();
        }
    }

    /**
     * Retorna HTTP 405 quando o método não é aceito pela rota.
     */
    private boolean validarMetodo(
            HttpExchange requisicao,
            String metodo
    ) throws IOException {
        if (metodo.equals(requisicao.getRequestMethod())) {
            return true;
        }

        requisicao.getResponseHeaders().set("Allow", metodo);

        responderJson(
                requisicao,
                405,
                Map.of("erro", "Método HTTP não permitido.")
        );

        return false;
    }

    /**
     * Valida o JSON, converte os campos e executa o serviço.
     */
    private void analisarCredito(
            HttpExchange requisicao
    ) throws IOException {
        String contentType = requisicao.getRequestHeaders()
                .getFirst("Content-Type");

        if (contentType == null
                || !contentType.split(";", 2)[0].trim()
                .equalsIgnoreCase("application/json")) {

            responderJson(
                    requisicao,
                    415,
                    Map.of(
                            "erro",
                            "Utilize Content-Type application/json."
                    )
            );

            return;
        }

        byte[] bytes = requisicao.getRequestBody()
                .readNBytes(LIMITE_CORPO + 1);

        if (bytes.length > LIMITE_CORPO) {
            responderJson(
                    requisicao,
                    413,
                    Map.of(
                            "erro",
                            "O corpo da requisição excede o limite permitido."
                    )
            );

            return;
        }

        String corpo = new String(
                bytes,
                StandardCharsets.UTF_8
        );

        try {
            JsonElement json = gson.fromJson(
                    corpo,
                    JsonElement.class
            );

            if (json == null || !json.isJsonObject()) {
                throw new IllegalArgumentException(
                        "O corpo da requisição deve ser um objeto JSON."
                );
            }

            JsonObject dados = json.getAsJsonObject();

            BigDecimal idadeInformada = lerNumero(dados, "idade");
            Integer idade = null;

            if (idadeInformada != null) {
                try {
                    idade = idadeInformada.intValueExact();
                } catch (ArithmeticException erro) {
                    throw new IllegalArgumentException(
                            "Idade deve ser um número inteiro entre 0 e 120."
                    );
                }
            }

            BigDecimal renda = lerNumero(dados, "rendaMensal");
            BigDecimal valor = lerNumero(dados, "valorSolicitado");

            ResultadoAnalise resultado = service.analisar(
                    idade,
                    renda,
                    valor
            );

            responderJson(requisicao, 200, resultado);

        } catch (JsonParseException erro) {
            responderJson(
                    requisicao,
                    400,
                    Map.of("erro", "JSON inválido.")
            );

        } catch (IllegalArgumentException erro) {
            responderJson(
                    requisicao,
                    400,
                    Map.of("erro", erro.getMessage())
            );
        }
    }

    /**
     * Aceita apenas números JSON, rejeitando textos e booleanos.
     * Campos ausentes ou nulos são validados pelo serviço.
     */
    private BigDecimal lerNumero(
            JsonObject dados,
            String campo
    ) {
        JsonElement valor = dados.get(campo);

        if (valor == null || valor.isJsonNull()) {
            return null;
        }

        if (!valor.isJsonPrimitive()
                || !valor.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " deve ser numérico."
            );
        }

        return valor.getAsBigDecimal();
    }

    private void responderJson(
            HttpExchange requisicao,
            int status,
            Object conteudo
    ) throws IOException {
        byte[] resposta = gson.toJson(conteudo)
                .getBytes(StandardCharsets.UTF_8);

        responder(
                requisicao,
                status,
                "application/json; charset=UTF-8",
                resposta
        );
    }

    private void responder(
            HttpExchange requisicao,
            int status,
            String contentType,
            byte[] resposta
    ) throws IOException {
        requisicao.getResponseHeaders().set(
                "Content-Type",
                contentType
        );

        requisicao.sendResponseHeaders(status, resposta.length);

        try (var saida = requisicao.getResponseBody()) {
            saida.write(resposta);
        }
    }

    @Override
    public void close() {
        servidor.stop(0);
    }

    public static void main(String[] args) throws IOException {
        String host = System.getenv()
                .getOrDefault("HOST", "127.0.0.1");

        int porta = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        CreditoWebApplication aplicacao =
                new CreditoWebApplication(host, porta);

        Runtime.getRuntime().addShutdownHook(
                new Thread(aplicacao::close)
        );

        aplicacao.iniciar();

        System.out.println(
                "Servidor iniciado em " + host + ":" + porta
        );

        System.out.println(
                "Simulador local: "
                        + aplicacao.endereco()
                        + "/simulador"
        );

        System.out.println(
                "Para encerrar, pressione Ctrl + C."
        );
    }
}