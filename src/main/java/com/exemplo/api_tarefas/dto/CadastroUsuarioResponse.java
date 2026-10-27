package com.exemplo.api_tarefas.dto;

public record CadastroUsuarioResponse(
        String mensagem,
        Long id,
        String email
) {
}
