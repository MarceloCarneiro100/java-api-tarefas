package com.exemplo.api_tarefas.exception;

import com.exemplo.api_tarefas.dto.ErroResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroValidacao(MethodArgumentNotValidException exception) {
        List<String> mensagens = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getDefaultMessage())
                .toList();

        ErroResponse erroResponse = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                mensagens
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erroResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> tratarRotaNaoEncontrada(
            NoResourceFoundException exception
    ) {
        ErroResponse erroResponse = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                List.of("A rota solicitada não foi encontrada")
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erroResponse);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarEmailJaCadastrado(
            EmailJaCadastradoException exception
    ) {
        ErroResponse erroResponse = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Conflito",
                List.of(exception.getMessage())
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erroResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarViolacaoIntegridade(
            DataIntegrityViolationException exception
    ) {
        ErroResponse erroResponse = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Conflito",
                List.of("Os dados informados violam uma restrição do banco de dados.")
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erroResponse);
    }
}
