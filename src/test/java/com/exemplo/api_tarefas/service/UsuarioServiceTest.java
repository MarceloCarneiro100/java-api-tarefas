package com.exemplo.api_tarefas.service;

import com.exemplo.api_tarefas.dto.AtualizarUsuarioRequest;
import com.exemplo.api_tarefas.dto.LoginRequest;
import com.exemplo.api_tarefas.dto.UsuarioResponse;
import com.exemplo.api_tarefas.entity.Perfil;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.AutoExclusaoAdministradorException;
import com.exemplo.api_tarefas.exception.CredenciaisInvalidasException;
import com.exemplo.api_tarefas.exception.EmailJaCadastradoException;
import com.exemplo.api_tarefas.exception.UsuarioNaoEncontradoException;
import com.exemplo.api_tarefas.repository.UsuarioRepository;
import com.exemplo.api_tarefas.security.UsuarioAutenticado;
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

import java.nio.file.AccessDeniedException;
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

    @Test
    void deveAtualizarUsuarioComSucesso() throws AccessDeniedException {
        Usuario usuario = new Usuario();
        usuario.setEmail("antigo@email.com");
        usuario.setSenha("senha-antiga");
        usuario.setPerfil(Perfil.USER);

        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "novo@email.com",
                "12345678"
        );

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "antigo@email.com",
                Perfil.USER
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmail("novo@email.com"))
                .thenReturn(false);

        when(passwordService.codificarSenha("12345678"))
                .thenReturn("nova-senha-codificada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        UsuarioResponse resultado = usuarioService.atualizarUsuario(
                1L, request, autenticado
        );

        assertEquals("novo@email.com", resultado.email());
        assertEquals(Perfil.USER, resultado.perfil());
        assertEquals("nova-senha-codificada", usuario.getSenha());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).existsByEmail("novo@email.com");
        verify(passwordService).codificarSenha("12345678");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRejeitarAtualizacaoComEmailCadastrado() {
        Usuario usuario = new Usuario();
        usuario.setEmail("antigo@email.com");
        usuario.setSenha("senha-antiga");
        usuario.setPerfil(Perfil.USER);

        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "outro@email.com",
                "12345678"
        );

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "antigo@email.com",
                Perfil.USER
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmail("outro@email.com"))
                .thenReturn(true);

        assertThrows(
                EmailJaCadastradoException.class,
                () -> usuarioService.atualizarUsuario(1L, request, autenticado)
        );

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).existsByEmail("outro@email.com");
        verify(passwordService, never())
                .codificarSenha("12345678");

        verify(usuarioRepository, never())
                .save(any(Usuario.class));
    }

    @Test
    void devePermitirAtualizacaoMantendoMesmoEmail() throws AccessDeniedException {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha-antiga");
        usuario.setPerfil(Perfil.USER);

        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "teste@email.com",
                "12345678"
        );

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "teste@email.com",
                Perfil.USER
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmail("teste@email.com"))
                .thenReturn(true);

        when(passwordService.codificarSenha("12345678"))
                .thenReturn("nova-senha-codificada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        UsuarioResponse resultado = usuarioService.atualizarUsuario(1L, request, autenticado);

        assertEquals("teste@email.com", resultado.email());
        assertEquals("nova-senha-codificada", usuario.getSenha());

        verify(usuarioRepository).existsByEmail("teste@email.com");
        verify(passwordService).codificarSenha("12345678");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveImpedirUsuarioComumDeAtualizarOutroUsuario() {
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "novo@email.com",
                "12345678"
        );

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "usuario@email.com",
                Perfil.USER
        );

        assertThrows(
                AccessDeniedException.class,
                () -> usuarioService.atualizarUsuario(2L, request, autenticado)
        );

        verify(usuarioRepository, never()).findById(2L);
        verify(usuarioRepository, never()).existsByEmail("novo@email.com");
        verify(passwordService, never()).codificarSenha("12345678");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void devePermitirAdministradorAtualizarOutroUsuario() throws AccessDeniedException {
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@email.com");
        usuario.setSenha("senha-antiga");
        usuario.setPerfil(Perfil.USER);

        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "novo@email.com",
                "12345678"
        );

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "admin@email.com",
                Perfil.ADMIN
        );

        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmail("novo@email.com"))
                .thenReturn(false);

        when(passwordService.codificarSenha("12345678"))
                .thenReturn("nova-senha-codificada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        UsuarioResponse resultado = usuarioService.atualizarUsuario(2L, request, autenticado);

        assertEquals("novo@email.com", resultado.email());
        assertEquals(Perfil.USER, resultado.perfil());
        assertEquals("nova-senha-codificada", usuario.getSenha());

        verify(usuarioRepository).findById(2L);
        verify(usuarioRepository).existsByEmail("novo@email.com");
        verify(passwordService).codificarSenha("12345678");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
                "novo@email.com",
                "12345678"
        );

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "admin@email.com",
                Perfil.ADMIN
        );

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.atualizarUsuario(99L, request, autenticado)
        );

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).existsByEmail("novo@email.com");
        verify(passwordService, never()).codificarSenha("12345678");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveExcluirUsuarioComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@email.com");
        usuario.setPerfil(Perfil.USER);

        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "admin@email.com",
                Perfil.ADMIN
        );

        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(usuario));

        usuarioService.excluirUsuario(2L, autenticado);

        verify(usuarioRepository).findById(2L);
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deveImpedirAdministradorDeExcluirPropriaConta() {
        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "admin@email.com",
                Perfil.ADMIN
        );

        assertThrows(
                AutoExclusaoAdministradorException.class,
                () -> usuarioService.excluirUsuario(1L, autenticado)
        );

        verify(usuarioRepository, never()).findById(1L);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        UsuarioAutenticado autenticado = new UsuarioAutenticado(
                1L,
                "admin@email.com",
                Perfil.ADMIN
        );

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.excluirUsuario(99L, autenticado)
        );

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void deveAutenticarUsuarioComSucesso() {
        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);
        usuario.setEmail("usuario@email.com");
        usuario.setSenha("senha-codificada");
        usuario.setPerfil(Perfil.USER);

        LoginRequest request = new LoginRequest(
                "usuario@email.com",
                "12345678"
        );

        when(usuarioRepository.findByEmail("usuario@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordService.verificarSenha(
                "12345678",
                "senha-codificada"
        ))
                .thenReturn(true);

        UsuarioResponse resposta = usuarioService.autenticar(request);

        assertEquals(1L, resposta.id());
        assertEquals("usuario@email.com", resposta.email());
        assertEquals(Perfil.USER, resposta.perfil());

        verify(usuarioRepository).findByEmail("usuario@email.com");
        verify(passwordService).verificarSenha(
                "12345678",
                "senha-codificada"
        );
    }

    @Test
    void deveRejeitarAutenticacaoComEmailInexistente() {
        LoginRequest request = new LoginRequest(
                "inexistente@email.com",
                "12345678"
        );

        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> usuarioService.autenticar(request)
        );

        verify(usuarioRepository).findByEmail("inexistente@email.com");
        verify(passwordService, never()).verificarSenha(anyString(), anyString());
    }

    @Test
    void deveRejeitarAutenticacaoComSenhaIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@email.com");
        usuario.setSenha("senha-codificada");
        usuario.setPerfil(Perfil.USER);

        LoginRequest request = new LoginRequest(
                "usuario@email.com",
                "senha-errada"
        );

        when(usuarioRepository.findByEmail("usuario@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordService
                .verificarSenha("senha-errada",
                        "senha-codificada"))
                .thenReturn(false);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> usuarioService.autenticar(request)
        );

        verify(usuarioRepository).findByEmail("usuario@email.com");
        verify(passwordService).verificarSenha(
                "senha-errada",
                "senha-codificada"
        );
    }
}
