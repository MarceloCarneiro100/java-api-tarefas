package com.exemplo.api_tarefas.exception;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa não encontrada com o ID: " + id);
    }
}
