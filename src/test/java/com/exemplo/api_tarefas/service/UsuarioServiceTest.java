package com.exemplo.api_tarefas.service;

import com.exemplo.api_tarefas.dto.UsuarioResponse;
import com.exemplo.api_tarefas.entity.Perfil;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.EmailJaCadastradoException;
import com.exemplo.api_tarefas.exception.UsuarioNaoEncontradoException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

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

    @Test
    void deveListarUsuariosComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setPerfil(Perfil.USER);

        when(usuarioRepository.findAll())
                .thenReturn(List.of(usuario));

        List<UsuarioResponse> resultado = usuarioService.listarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("teste@email.com", resultado.get(0).email());
        assertEquals(Perfil.USER, resultado.get(0).perfil());

        verify(usuarioRepository).findAll();
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);
        usuario.setEmail("teste@email.com");
        usuario.setPerfil(Perfil.USER);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResponse resultado = usuarioService.buscarUsuarioPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals("teste@email.com", resultado.email());
        assertEquals(Perfil.USER, resultado.perfil());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarUsuarioPorId(99L)
        );

        verify(usuarioRepository).findById(99L);
    }
}
