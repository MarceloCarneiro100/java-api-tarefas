package com.exemplo.api_tarefas.service;

import com.exemplo.api_tarefas.dto.UsuarioResponse;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.EmailJaCadastradoException;
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

        String senhaCodificada =
                passwordService.codificarSenha(usuario.getSenha());

        usuario.setSenha(senhaCodificada);
        return usuarioRepository.save(usuario);
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> new UsuarioResponse(
                        usuario.getId(),
                        usuario.getEmail()
                ))
                .toList();
    }
}
