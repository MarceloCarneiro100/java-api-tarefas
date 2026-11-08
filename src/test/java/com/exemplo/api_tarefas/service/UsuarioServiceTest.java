package com.exemplo.api_tarefas.service;

import com.exemplo.api_tarefas.entity.Perfil;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.EmailJaCadastradoException;
import com.exemplo.api_tarefas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveSalvarUsuarioComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("12345678");

        when(usuarioRepository.existsByEmail("teste@email.com"))
                .thenReturn(false);

        when(passwordService.codificarSenha("12345678"))
                .thenReturn("senha-codificada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        Usuario resultado = usuarioService.salvarUsuario(usuario);

        assertEquals("senha-codificada", resultado.getSenha());
        assertEquals(Perfil.USER, resultado.getPerfil());

        verify(usuarioRepository).existsByEmail("teste@email.com");
        verify(passwordService).codificarSenha("12345678");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRejeitarCadastroComEmailJaCadastrado() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("12345678");

        when(usuarioRepository.existsByEmail("teste@email.com"))
                .thenReturn(true);

        assertThrows(
                EmailJaCadastradoException.class,
                () -> usuarioService.salvarUsuario(usuario)
        );

        verify(usuarioRepository).existsByEmail("teste@email.com");
        verify(passwordService, never()).codificarSenha("12345678");
    }
}
