package com.exemplo.api_tarefas.dto;

import com.exemplo.api_tarefas.entity.Perfil;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String email,
        Perfil perfil,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
