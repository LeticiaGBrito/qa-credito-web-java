package credito;

import credito.support.BaseWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica a rejeição de entradas inválidas no formulário.
 */
class DadosInvalidosTest extends BaseWebTest {

    private static final String ERRO_IDADE =
            "Informe uma idade inteira entre 0 e 120.";

    private static final String ERRO_FORMATO_MONETARIO =
            "Informe um valor numérico com no máximo duas casas decimais.";

    private static final String ERRO_VALOR_POSITIVO =
            "Informe um valor maior que zero e dentro do limite numérico.";

    static Stream<Arguments> dadosInvalidos() {
        return Stream.of(
                Arguments.of(
                        "Idade negativa",
                        "-1", "3000,00", "10000,00",
                        "idade", ERRO_IDADE
                ),
                Arguments.of(
                        "Idade acima de 120",
                        "121", "3000,00", "10000,00",
                        "idade", ERRO_IDADE
                ),
                Arguments.of(
                        "Idade fracionária",
                        "18.5", "3000,00", "10000,00",
                        "idade", ERRO_IDADE
                ),
                Arguments.of(
                        "Idade com letras",
                        "abc", "3000,00", "10000,00",
                        "idade", ERRO_IDADE
                ),
                Arguments.of(
                        "Renda igual a zero",
                        "30", "0", "10000,00",
                        "rendaMensal", ERRO_VALOR_POSITIVO
                ),
                Arguments.of(
                        "Renda negativa",
                        "30", "-100,00", "10000,00",
                        "rendaMensal", ERRO_FORMATO_MONETARIO
                ),
                Arguments.of(
                        "Renda com três casas decimais",
                        "30", "3000,001", "10000,00",
                        "rendaMensal", ERRO_FORMATO_MONETARIO
                ),
                Arguments.of(
                        "Valor solicitado igual a zero",
                        "30", "3000,00", "0",
                        "valorSolicitado", ERRO_VALOR_POSITIVO
                ),
                Arguments.of(
                        "Valor solicitado negativo",
                        "30", "3000,00", "-100,00",
                        "valorSolicitado", ERRO_FORMATO_MONETARIO
                ),
                Arguments.of(
                        "Valor solicitado com três casas decimais",
                        "30", "3000,00", "10000,001",
                        "valorSolicitado", ERRO_FORMATO_MONETARIO
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dadosInvalidos")
    @DisplayName("Deve rejeitar dados inválidos na interface")
    void deveRejeitarDadosInvalidos(
            String cenario,
            String idade,
            String renda,
            String valor,
            String campoInvalido,
            String mensagemEsperada
    ) {
        creditoPage.preencherSolicitacao(idade, renda, valor);
        creditoPage.analisar();

        String mensagem = creditoPage.obterErroCampo(campoInvalido);

        assertAll(
                () -> assertEquals(
                        mensagemEsperada,
                        mensagem
                ),
                () -> assertTrue(
                        creditoPage.campoEstaInvalido(campoInvalido),
                        "O campo deve estar marcado como inválido."
                ),
                () -> assertFalse(
                        creditoPage.resultadoEstaVisivel(),
                        "Não deve apresentar resultado com dados inválidos."
                )
        );
    }
}