package credito;

import credito.support.BaseWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica formatos monetários e o comportamento do botão Limpar.
 */
class ComportamentoFormularioTest extends BaseWebTest {

    private static final List<String> CAMPOS = List.of(
            "idade",
            "rendaMensal",
            "valorSolicitado"
    );

    @ParameterizedTest(name = "{0}")
    @DisplayName("Deve aceitar os formatos decimais permitidos")
    @CsvSource(
            delimiter = ';',
            value = {
                    "Vírgula decimal; 3000,50; 10000,25",
                    "Ponto decimal; 3000.50; 10000.25"
            }
    )
    void deveAceitarFormatoDecimal(
            String cenario,
            String renda,
            String valor
    ) {
        creditoPage.preencherSolicitacao("30", renda, valor);
        creditoPage.analisar();

        String status = creditoPage.obterStatus();

        assertAll(
                () -> assertEquals(
                        "APROVADO",
                        status
                ),
                () -> assertEquals(
                        "Solicitação aprovada",
                        creditoPage.obterTituloResultado()
                ),
                () -> assertEquals(
                        List.of(),
                        creditoPage.obterMotivos()
                )
        );
    }

    @Test
    @DisplayName("Deve limpar os campos e ocultar o resultado")
    void deveLimparCamposEResultado() {
        creditoPage.preencherSolicitacao(
                "30",
                "3000,00",
                "10000,00"
        );

        creditoPage.analisar();

        // Confirma o resultado antes de testar sua remoção.
        assertEquals("APROVADO", creditoPage.obterStatus());

        creditoPage.limparFormulario();

        for (String campo : CAMPOS) {
            assertEquals(
                    "",
                    creditoPage.obterValorCampo(campo),
                    "O campo " + campo + " deve ficar vazio."
            );
        }

        assertFalse(
                creditoPage.resultadoEstaVisivel(),
                "O resultado deve ficar oculto após a limpeza."
        );
    }

    @Test
    @DisplayName("Deve remover os erros e as marcações de campos inválidos")
    void deveLimparErrosDeValidacao() {
        creditoPage.analisar();

        // Confirma que os erros existem antes de limpar.
        for (String campo : CAMPOS) {
            assertEquals(
                    "Este campo é obrigatório.",
                    creditoPage.obterErroCampo(campo)
            );

            assertTrue(
                    creditoPage.campoEstaInvalido(campo)
            );
        }

        creditoPage.limparFormulario();

        for (String campo : CAMPOS) {
            assertAll(
                    "Limpeza do campo " + campo,
                    () -> assertEquals(
                            "",
                            creditoPage.obterValorCampo(campo)
                    ),
                    () -> assertEquals(
                            "",
                            creditoPage.obterTextoErroCampo(campo)
                    ),
                    () -> assertFalse(
                            creditoPage.campoEstaInvalido(campo)
                    )
            );
        }

        assertFalse(creditoPage.resultadoEstaVisivel());
    }
}