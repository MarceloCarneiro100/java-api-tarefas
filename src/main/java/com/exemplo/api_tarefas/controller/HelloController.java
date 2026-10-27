package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.CadastroUsuarioRequest;
import com.exemplo.api_tarefas.dto.CadastroUsuarioResponse;
import com.exemplo.api_tarefas.dto.MensagemResponse;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.service.MensagemService;
import com.exemplo.api_tarefas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    private final MensagemService mensagemService;
    private final UsuarioService usuarioService;

    public HelloController(
            MensagemService mensagemService,
            UsuarioService usuarioService
    ) {
        this.mensagemService = mensagemService;
        this.usuarioService = usuarioService;
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

    // Endpoint provisório para teste (inicialmente sem senha com hash)
    @PostMapping("/api/teste-persistencia")
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroUsuarioResponse testarPersistencia(
            @Valid @RequestBody CadastroUsuarioRequest request
    ) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);

        return new CadastroUsuarioResponse(
                "Usuário salvo no banco de dados de teste",
                usuarioSalvo.getEmail()
        );
    }
}
