package com.exemplo.api_tarefas.security;

import com.exemplo.api_tarefas.entity.Perfil;

public record UsuarioAutenticado(
        Long id,
        String email,
        Perfil perfil
) {
}
