package com.exemplo.api_tarefas.exception;

public class AutoExclusaoAdministradorException extends RuntimeException {
    public AutoExclusaoAdministradorException() {
        super("O administrador não pode excluir a própria conta");
    }
}
