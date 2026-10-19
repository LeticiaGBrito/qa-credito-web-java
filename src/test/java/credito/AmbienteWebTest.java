package credito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmbienteWebTest {

    @Test
    @DisplayName("Deve abrir o Chrome e validar uma página de teste")
    void deveValidarAmbienteWeb() {
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

        WebDriver navegador = new ChromeDriver(opcoes);

        try {
            navegador.manage().timeouts()
                    .pageLoadTimeout(Duration.ofSeconds(30));

            if (!headless) {
                navegador.manage().window().maximize();
            }

            String html = """
                    <!DOCTYPE html>
                    <html lang="pt-BR">
                    <head>
                        <meta charset="UTF-8">
                        <title>QA Crédito Web — Java</title>
                    </head>
                    <body>
                        <h1 id="titulo">Ambiente Selenium funcionando</h1>
                    </body>
                    </html>
                    """;

            String pagina = Base64.getEncoder().encodeToString(
                    html.getBytes(StandardCharsets.UTF_8)
            );

            navegador.get("data:text/html;base64," + pagina);

            assertEquals(
                    "QA Crédito Web — Java",
                    navegador.getTitle()
            );

            assertEquals(
                    "Ambiente Selenium funcionando",
                    navegador.findElement(By.id("titulo")).getText()
            );
        } finally {
            navegador.quit();
        }
    }
}