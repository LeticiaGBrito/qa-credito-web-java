package credito;

import credito.support.BaseWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes das regras de crédito pela interface web.
 */
class SolicitacaoCreditoTest extends BaseWebTest {

    @ParameterizedTest(name = "{0}")
    @DisplayName("Deve aprovar solicitações válidas")
    @CsvSource(
            delimiter = ';',
            value = {
                    "Dados válidos; 30; 3000,00; 10000,00",
                    "Limites exatos; 18; 2000,00; 10000,00"
            }
    )
    void deveAprovarSolicitacao(
            String cenario,
            String idade,
            String renda,
            String valor
    ) {
        creditoPage.preencherSolicitacao(idade, renda, valor);
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

    @ParameterizedTest(name = "{0}")
    @DisplayName("Deve reprovar por um critério de negócio")
    @CsvSource(
            delimiter = ';',
            value = {
                    "Menor de idade; 17; 3000,00; 10000,00;"
                            + " Solicitante deve ter pelo menos 18 anos.",

                    "Renda abaixo do mínimo; 30; 1999,99; 5000,00;"
                            + " Renda mensal inferior a R$ 2.000,00.",

                    "Valor acima do limite; 30; 3000,00; 15000,01;"
                            + " Valor solicitado superior a cinco vezes"
                            + " a renda mensal."
            }
    )
    void deveReprovarSolicitacao(
            String cenario,
            String idade,
            String renda,
            String valor,
            String motivoEsperado
    ) {
        creditoPage.preencherSolicitacao(idade, renda, valor);
        creditoPage.analisar();

        String status = creditoPage.obterStatus();

        assertAll(
                () -> assertEquals(
                        "REPROVADO",
                        status
                ),
                () -> assertEquals(
                        "Solicitação reprovada",
                        creditoPage.obterTituloResultado()
                ),
                () -> assertEquals(
                        List.of(motivoEsperado),
                        creditoPage.obterMotivos()
                )
        );
    }

    @Test
    @DisplayName("Deve apresentar todos os motivos de reprovação")
    void deveApresentarMultiplosMotivos() {
        creditoPage.preencherSolicitacao(
                "17",
                "1000,00",
                "6000,00"
        );

        creditoPage.analisar();

        String status = creditoPage.obterStatus();

        List<String> motivosEsperados = List.of(
                "Solicitante deve ter pelo menos 18 anos.",
                "Renda mensal inferior a R$ 2.000,00.",
                "Valor solicitado superior a cinco vezes a renda mensal."
        );

        assertAll(
                () -> assertEquals(
                        "REPROVADO",
                        status
                ),
                () -> assertEquals(
                        "Solicitação reprovada",
                        creditoPage.obterTituloResultado()
                ),
                () -> assertEquals(
                        motivosEsperados,
                        creditoPage.obterMotivos()
                )
        );
    }
}