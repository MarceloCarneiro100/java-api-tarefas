package com.exemplo.api_tarefas.dto;

public record LoginResponse(
        String mensagem,
        String token,
        Long id,
        String email
) {
}
