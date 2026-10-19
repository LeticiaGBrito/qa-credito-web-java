package credito;

import java.util.List;

/**
 * Representa o resultado da análise e seus motivos de reprovação.
 */
public record ResultadoAnalise(
        String status,
        List<String> motivos
) {
    public ResultadoAnalise {
        // Mantém uma cópia imutável dos motivos.
        motivos = List.copyOf(motivos);
    }
}