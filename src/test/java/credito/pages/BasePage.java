package credito.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Comportamentos compartilhados pelos Page Objects.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    protected WebElement aguardarVisivel(By localizador) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(localizador)
        );
    }

    protected void preencher(By localizador, String valor) {
        WebElement campo = aguardarVisivel(localizador);
        campo.clear();
        campo.sendKeys(valor);
    }

    protected void clicar(By localizador) {
        wait.until(
                ExpectedConditions.elementToBeClickable(localizador)
        ).click();
    }
}