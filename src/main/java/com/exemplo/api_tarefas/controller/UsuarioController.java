package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.*;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.service.JwtService;
import com.exemplo.api_tarefas.service.MensagemService;
import com.exemplo.api_tarefas.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {

    private final MensagemService mensagemService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public UsuarioController(
            MensagemService mensagemService,
            UsuarioService usuarioService,
            JwtService jwtService
    ) {
        this.mensagemService = mensagemService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/hello")
    public MensagemResponse hello() {
        String mensagem = mensagemService.obterMensagem();

        return new MensagemResponse(
                mensagem,
                "funcionando"
        );
    }

    @SecurityRequirement(name = "bearerAuth")
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

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/usuarios")
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/usuarios/{id}")
    public UsuarioResponse buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/usuarios/{id}")
    public UsuarioResponse atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return usuarioService.atualizarUsuario(id, request);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirUsuario(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
    }

    @PostMapping("/api/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UsuarioResponse usuario = usuarioService.autenticar(request);

        String token = jwtService.gerarToken(
                usuario.id(),
                usuario.email(),
                usuario.perfil().name()
        );

        return new LoginResponse(
                "Login realizado com sucesso",
                token,
                usuario.id(),
                usuario.email(),
                usuario.perfil().name()
        );
    }
}
