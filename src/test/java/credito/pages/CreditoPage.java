package credito.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

/**
 * Elementos e ações do simulador de crédito.
 */
public class CreditoPage extends BasePage {

    private final By idade = By.id("idade");
    private final By rendaMensal = By.id("rendaMensal");
    private final By valorSolicitado = By.id("valorSolicitado");
    private final By analisar = By.id("analisar");
    private final By limpar = By.id("limpar");

    private final By resultado = By.id("resultado");
    private final By tituloResultado = By.id("resultado-titulo");

    private final By motivos = By.cssSelector(
            "#resultado-motivos li"
    );

    public CreditoPage(WebDriver driver) {
        super(driver, Duration.ofSeconds(20));
    }

    public void abrir(String enderecoBase) {
        driver.get(enderecoBase + "/simulador");
        aguardarVisivel(idade);
    }

    public void preencherSolicitacao(
            String idadeInformada,
            String rendaInformada,
            String valorInformado
    ) {
        preencher(idade, idadeInformada);
        preencher(rendaMensal, rendaInformada);
        preencher(valorSolicitado, valorInformado);
    }

    public void analisar() {
        clicar(analisar);
    }

    public void limparFormulario() {
        clicar(limpar);

        // Aguarda o reset dos campos realizado pelo navegador.
        wait.until(navegador ->
                List.of("idade", "rendaMensal", "valorSolicitado")
                        .stream()
                        .allMatch(nome ->
                                "".equals(
                                        navegador.findElement(By.id(nome))
                                                .getDomProperty("value")
                                )
                        )
        );
    }

    public String obterStatus() {
        return aguardarVisivel(resultado)
                .getDomAttribute("data-status");
    }

    public String obterTituloResultado() {
        return aguardarVisivel(tituloResultado).getText();
    }

    public List<String> obterMotivos() {
        aguardarVisivel(resultado);

        return driver.findElements(motivos)
                .stream()
                .map(elemento -> elemento.getText())
                .toList();
    }

    /**
     * Aguarda uma mensagem de validação preenchida.
     */
    public String obterErroCampo(String nomeCampo) {
        By erro = By.id(nomeCampo + "-erro");

        wait.until(navegador ->
                !navegador.findElement(erro).getText().isBlank()
        );

        return obterTextoErroCampo(nomeCampo);
    }

    /**
     * Permite verificar também se a mensagem foi removida.
     */
    public String obterTextoErroCampo(String nomeCampo) {
        return driver.findElement(
                By.id(nomeCampo + "-erro")
        ).getText();
    }

    public String obterValorCampo(String nomeCampo) {
        return driver.findElement(By.id(nomeCampo))
                .getDomProperty("value");
    }

    public boolean campoEstaInvalido(String nomeCampo) {
        return "true".equals(
                driver.findElement(By.id(nomeCampo))
                        .getDomAttribute("aria-invalid")
        );
    }

    public boolean resultadoEstaVisivel() {
        return driver.findElement(resultado).isDisplayed();
    }
}