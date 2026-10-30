package com.exemplo.api_tarefas.service;

import com.exemplo.api_tarefas.dto.AtualizarUsuarioRequest;
import com.exemplo.api_tarefas.dto.LoginRequest;
import com.exemplo.api_tarefas.dto.UsuarioResponse;
import com.exemplo.api_tarefas.entity.Perfil;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.CredenciaisInvalidasException;
import com.exemplo.api_tarefas.exception.EmailJaCadastradoException;
import com.exemplo.api_tarefas.exception.UsuarioNaoEncontradoException;
import com.exemplo.api_tarefas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordService passwordService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        boolean emailExiste = usuarioRepository.existsByEmail(usuario.getEmail());

        if (emailExiste) {
            throw new EmailJaCadastradoException(usuario.getEmail());
        }

        String senhaCodificada = passwordService.codificarSenha(usuario.getSenha());

        usuario.setSenha(senhaCodificada);
        usuario.setPerfil(Perfil.USER);

        return usuarioRepository.save(usuario);
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository
                .findAll()
                .stream()
                .map(usuario -> new UsuarioResponse(
                        usuario.getId(), usuario.getEmail()))
                .toList();
    }

    public UsuarioResponse buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail()
        );
    }

    public UsuarioResponse atualizarUsuario(Long id, AtualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        boolean emailEmUso = usuarioRepository.existsByEmail(request.email());

        if (emailEmUso && !usuario.getEmail().equals(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        usuario.setEmail(request.email());

        String senhaCodificada = passwordService.codificarSenha(request.senha());
        usuario.setSenha(senhaCodificada);

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuarioAtualizado.getId(),
                usuarioAtualizado.getEmail()
        );
    }

    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        usuarioRepository.delete(usuario);
    }

    public UsuarioResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new CredenciaisInvalidasException());

        boolean senhaCorreta = passwordService
                .verificarSenha(request.senha(), usuario.getSenha());

        if (!senhaCorreta) {
            throw new CredenciaisInvalidasException();
        }

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail()
        );
    }
}
