package com.exemplo.api_tarefas.dto;

import com.exemplo.api_tarefas.entity.Categoria;
import com.exemplo.api_tarefas.entity.StatusTarefa;

import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        Categoria categoria,
        StatusTarefa status,
        Long usuarioId,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
