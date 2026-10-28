package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.*;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.service.MensagemService;
import com.exemplo.api_tarefas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {

    private final MensagemService mensagemService;
    private final UsuarioService usuarioService;

    public UsuarioController(
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
                null,
                request.email()
        );
    }

    @PostMapping("/api/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroUsuarioResponse cadastrarUsuario(
            @Valid @RequestBody CadastroUsuarioRequest request
    ) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);

        return new CadastroUsuarioResponse(
                "Usuário cadastrado com sucesso",
                usuarioSalvo.getId(),
                usuarioSalvo.getEmail()
        );
    }

    @GetMapping("/api/usuarios")
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/api/usuarios/{id}")
    public UsuarioResponse buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id);
    }

    @PutMapping("/api/usuarios/{id}")
    public UsuarioResponse atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return usuarioService.atualizarUsuario(id, request);
    }
}
