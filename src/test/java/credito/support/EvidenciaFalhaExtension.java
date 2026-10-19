package credito.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Captura a tela em falhas do teste ou de sua preparação.
 * Preserva a exceção original para que o teste continue falhando.
 */
public class EvidenciaFalhaExtension implements
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private final Supplier<WebDriver> fornecedorDriver;

    public EvidenciaFalhaExtension(
            Supplier<WebDriver> fornecedorDriver
    ) {
        this.fornecedorDriver = fornecedorDriver;
    }

    @Override
    public void handleTestExecutionException(
            ExtensionContext contexto,
            Throwable erro
    ) throws Throwable {
        capturarTela(contexto, "execucao", erro);
        throw erro;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(
            ExtensionContext contexto,
            Throwable erro
    ) throws Throwable {
        capturarTela(contexto, "preparacao", erro);
        throw erro;
    }

    private void capturarTela(
            ExtensionContext contexto,
            String etapa,
            Throwable erroOriginal
    ) {
        try {
            WebDriver navegador = fornecedorDriver.get();

            // A inicialização pode falhar antes de criar o navegador.
            if (!(navegador instanceof TakesScreenshot captura)) {
                return;
            }

            byte[] imagem = captura.getScreenshotAs(OutputType.BYTES);

            Path pasta = Path.of(
                    "build",
                    "evidencias",
                    "screenshots"
            );

            Files.createDirectories(pasta);

            String classe = contexto.getRequiredTestClass()
                    .getSimpleName();

            String metodo = contexto.getRequiredTestMethod()
                    .getName();

            String nome = classe
                    + "-"
                    + metodo
                    + "-"
                    + etapa
                    + "-"
                    + UUID.randomUUID()
                    + ".png";

            Path arquivo = pasta.resolve(nome);

            Files.write(arquivo, imagem);

            System.out.println(
                    "Captura de falha: " + arquivo.toAbsolutePath()
            );

        } catch (Exception erroCaptura) {
            // Não substitui a falha original por um erro da evidência.
            erroOriginal.addSuppressed(erroCaptura);

            System.err.println(
                    "Não foi possível salvar a captura: "
                            + erroCaptura.getMessage()
            );
        }
    }
}