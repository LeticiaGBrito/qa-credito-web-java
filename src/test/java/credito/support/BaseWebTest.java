package credito.support;

import credito.CreditoWebApplication;
import credito.pages.CreditoPage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.Duration;

/**
 * Prepara a aplicação local e um navegador novo para cada teste.
 */
public abstract class BaseWebTest {

    private static CreditoWebApplication aplicacao;
    private static String enderecoBase;

    protected WebDriver driver;
    protected CreditoPage creditoPage;

    @RegisterExtension
    final EvidenciaFalhaExtension evidenciaFalha =
            new EvidenciaFalhaExtension(() -> driver);

    @BeforeAll
    static void iniciarAplicacao() throws IOException {
        aplicacao = new CreditoWebApplication(0);
        aplicacao.iniciar();
        enderecoBase = aplicacao.endereco();
    }

    @BeforeEach
    void prepararNavegador() {
        ChromeOptions opcoes = new ChromeOptions();

        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false")
        );

        if (headless) {
            opcoes.addArguments(
                    "--headless=new",
                    "--window-size=1440,1000"
            );
        }

        driver = new ChromeDriver(opcoes);

        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(30));

        if (!headless) {
            driver.manage().window().maximize();
        }

        creditoPage = new CreditoPage(driver);
        creditoPage.abrir(enderecoBase);
    }

    @AfterEach
    void encerrarNavegador() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }

    @AfterAll
    static void encerrarAplicacao() {
        if (aplicacao != null) {
            aplicacao.close();
            aplicacao = null;
        }
    }
}