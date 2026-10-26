package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.CadastroUsuarioRequest;
import com.exemplo.api_tarefas.dto.CadastroUsuarioResponse;
import com.exemplo.api_tarefas.dto.MensagemResponse;
import com.exemplo.api_tarefas.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final MensagemService mensagemService;

    public HelloController(MensagemService mensagemService) {
        this.mensagemService = mensagemService;
    }

    @GetMapping("/api/hello")
    public MensagemResponse hello() {
        String mensagem = mensagemService.obterMensagem();

        return new MensagemResponse(
                mensagem,
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
