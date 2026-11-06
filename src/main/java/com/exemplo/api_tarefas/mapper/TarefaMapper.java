package com.exemplo.api_tarefas.mapper;

import com.exemplo.api_tarefas.dto.TarefaResponse;
import com.exemplo.api_tarefas.entity.Tarefa;
import org.springframework.stereotype.Component;

@Component
public class TarefaMapper {
    public TarefaResponse toResponse(Tarefa tarefa) {

        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getCategoria(),
                tarefa.getStatus(),
                tarefa.getUsuario().getId(),
                tarefa.getCriadoEm(),
                tarefa.getAtualizadoEm()
        );
    }
}
