package com.exemplo.api_tarefas.dto;

import com.exemplo.api_tarefas.entity.StatusTarefa;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusTarefaRequest(
        @NotNull(message = "O status é obrigatório")
        StatusTarefa status
) {
}
