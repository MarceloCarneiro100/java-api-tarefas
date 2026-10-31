package com.exemplo.api_tarefas.dto;

import com.exemplo.api_tarefas.entity.Perfil;

public record UsuarioResponse(
        Long id,
        String email,
        Perfil perfil
) {
}
