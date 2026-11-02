package com.exemplo.api_tarefas.dto;

import com.exemplo.api_tarefas.entity.Perfil;
import jakarta.validation.constraints.NotNull;

public record AtualizarPerfilRequest(
        @NotNull(message = "O perfil é obrigatório")
        Perfil perfil
) {
}
