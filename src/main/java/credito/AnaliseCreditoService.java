package credito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Valida os dados e aplica as regras fictícias de análise de crédito.
 * Não depende de servidor HTTP ou navegador.
 */
public class AnaliseCreditoService {

    private static final BigDecimal RENDA_MINIMA =
            new BigDecimal("2000.00");

    private static final BigDecimal MULTIPLICADOR_LIMITE =
            new BigDecimal("5");

    /**
     * Analisa uma solicitação válida e retorna todos os motivos
     * de reprovação encontrados.
     *
     * @throws IllegalArgumentException quando os dados são inválidos
     */
    public ResultadoAnalise analisar(
            Integer idade,
            BigDecimal rendaMensal,
            BigDecimal valorSolicitado
    ) {
        validarDados(idade, rendaMensal, valorSolicitado);

        List<String> motivos = new ArrayList<>();

        if (idade < 18) {
            motivos.add(
                    "Solicitante deve ter pelo menos 18 anos."
            );
        }

        if (rendaMensal.compareTo(RENDA_MINIMA) < 0) {
            motivos.add(
                    "Renda mensal inferior a R$ 2.000,00."
            );
        }

        BigDecimal limite = rendaMensal.multiply(
                MULTIPLICADOR_LIMITE
        );

        if (valorSolicitado.compareTo(limite) > 0) {
            motivos.add(
                    "Valor solicitado superior a cinco vezes a renda mensal."
            );
        }

        String status = motivos.isEmpty()
                ? "APROVADO"
                : "REPROVADO";

        return new ResultadoAnalise(status, motivos);
    }

    /**
     * Verifica campos obrigatórios e intervalos permitidos
     * antes de aplicar os critérios de aprovação.
     */
    private void validarDados(
            Integer idade,
            BigDecimal rendaMensal,
            BigDecimal valorSolicitado
    ) {
        if (idade == null
                || rendaMensal == null
                || valorSolicitado == null) {
            throw new IllegalArgumentException(
                    "Idade, renda mensal e valor solicitado são obrigatórios."
            );
        }

        if (idade < 0 || idade > 120) {
            throw new IllegalArgumentException(
                    "Idade deve ser um número inteiro entre 0 e 120."
            );
        }

        validarValorMonetario(rendaMensal, "Renda mensal");
        validarValorMonetario(valorSolicitado, "Valor solicitado");
    }

    /**
     * Exige valores positivos com no máximo duas casas decimais.
     */
    private void validarValorMonetario(
            BigDecimal valor,
            String nome
    ) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    nome + " deve ser maior que zero."
            );
        }

        if (valor.scale() > 2) {
            throw new IllegalArgumentException(
                    nome + " deve ter no máximo duas casas decimais."
            );
        }
    }
}