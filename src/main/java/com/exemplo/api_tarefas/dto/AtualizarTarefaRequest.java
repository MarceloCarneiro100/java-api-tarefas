package com.exemplo.api_tarefas.dto;

import com.exemplo.api_tarefas.entity.Categoria;
import com.exemplo.api_tarefas.entity.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarTarefaRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String titulo,

        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String descricao,

        Categoria categoria,

        @NotNull(message = "O status é obrigatório")
        StatusTarefa status
) {
}
