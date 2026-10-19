package credito;

import credito.support.BaseWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica as validações de preenchimento da interface.
 */
class ValidacaoFormularioTest extends BaseWebTest {

    static Stream<Arguments> camposObrigatorios() {
        return Stream.of(
                Arguments.of(
                        "Idade vazia",
                        "",
                        "3000,00",
                        "10000,00",
                        List.of("idade")
                ),
                Arguments.of(
                        "Renda vazia",
                        "30",
                        "",
                        "10000,00",
                        List.of("rendaMensal")
                ),
                Arguments.of(
                        "Valor solicitado vazio",
                        "30",
                        "3000,00",
                        "",
                        List.of("valorSolicitado")
                ),
                Arguments.of(
                        "Todos os campos vazios",
                        "",
                        "",
                        "",
                        List.of(
                                "idade",
                                "rendaMensal",
                                "valorSolicitado"
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("camposObrigatorios")
    @DisplayName("Deve exigir os campos obrigatórios")
    void deveValidarCamposObrigatorios(
            String cenario,
            String idade,
            String renda,
            String valor,
            List<String> camposInvalidos
    ) {
        creditoPage.preencherSolicitacao(idade, renda, valor);
        creditoPage.analisar();

        for (String campo : camposInvalidos) {
            String mensagem = creditoPage.obterErroCampo(campo);

            assertAll(
                    "Validação do campo " + campo,
                    () -> assertEquals(
                            "Este campo é obrigatório.",
                            mensagem
                    ),
                    () -> assertTrue(
                            creditoPage.campoEstaInvalido(campo),
                            "O campo deve estar marcado como inválido."
                    )
            );
        }

        assertFalse(
                creditoPage.resultadoEstaVisivel(),
                "Não deve apresentar resultado com campos obrigatórios vazios."
        );
    }
}