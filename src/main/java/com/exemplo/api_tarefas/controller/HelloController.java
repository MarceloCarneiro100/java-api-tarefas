package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.CadastroUsuarioRequest;
import com.exemplo.api_tarefas.dto.CadastroUsuarioResponse;
import com.exemplo.api_tarefas.dto.MensagemResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public MensagemResponse hello() {
        return new MensagemResponse(
                "Minha primeira API Spring Boot",
                "funcionando"
        );
    }

    @PostMapping("/api/teste-usuario")
    public CadastroUsuarioResponse testarUsuario(
            @Valid @RequestBody CadastroUsuarioRequest request
    ) {
        return new CadastroUsuarioResponse(
                "Dados recebidos com sucesso",
                request.email()
        );
    }
}
