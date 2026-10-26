package br.com.ada.lms.rest.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Dados que o cliente envia no corpo do POST/PUT.
 * Repare que não tem "id": quem gera o id é o servidor.
 *
 * Se as validações falharem, o Quarkus responde 400 Bad Request.
 */
public record TransacaoRequest(
        @NotBlank(message = "descrição é obrigatória")
        String descricao,

        @NotBlank(message = "tipo é obrigatório")
        String tipo,

        @NotBlank(message = "categoria é obrigatória")
        String categoria,

        @NotNull(message = "valor é obrigatório")
        double valor,

        @NotNull(message = "data é obrigatória")
        LocalDate data) {
}
