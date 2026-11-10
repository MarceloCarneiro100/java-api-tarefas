package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.config.SecurityConfig;
import com.exemplo.api_tarefas.dto.UsuarioResponse;
import com.exemplo.api_tarefas.entity.Perfil;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.UsuarioNaoEncontradoException;
import com.exemplo.api_tarefas.service.JwtService;
import com.exemplo.api_tarefas.service.MensagemService;
import com.exemplo.api_tarefas.service.UsuarioService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(UsuarioController.class)
@ImportAutoConfiguration(SecurityAutoConfiguration.class)
@Import(SecurityConfig.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MensagemService mensagemService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setEmail("usuario@email.com");

        when(usuarioService.salvarUsuario(any(Usuario.class)))
                .thenAnswer(invocacao -> {
                    Usuario usuario = invocacao.getArgument(0);
                    ReflectionTestUtils.setField(usuario, "id", 1L);
                    return usuario;
                });

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "email": "usuario@email.com",
                                   "senha": "12345678"
                                }
                                """))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem")
                        .value("Usuário cadastrado com sucesso"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("usuario@email.com"));

        verify(usuarioService).salvarUsuario(any(Usuario.class));
    }

    @Test
    void deveRejeitarCadastroComEmailInvalido() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "email-invalido",
                                    "senha": "12345678"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never())
                .salvarUsuario(any(Usuario.class));

    }

    @Test
    void deveRejeitarCadastroComSenhaMuitoCurta() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "email": "usuario@email.com",
                                   "senha": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never())
                .salvarUsuario(any(Usuario.class));
    }

    @Test
    void deveExigirAutenticacaoParaListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveListarUsuariosComSucesso() throws Exception {
        UsuarioResponse usuario = new UsuarioResponse(
                1L,
                "usuario@email.com",
                Perfil.USER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(usuarioService.listarUsuarios())
                .thenReturn(List.of(usuario));

        when(jwtService.extrairEmail("token-admin"))
                .thenReturn("admin@email.com");

        when(jwtService.extrairPerfil("token-admin"))
                .thenReturn("ADMIN");

        when(jwtService.extrairId("token-admin"))
                .thenReturn(1L);

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer token-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("usuario@email.com"))
                .andExpect(jsonPath("$[0].perfil").value("USER"));

        verify(usuarioService).listarUsuarios();
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        UsuarioResponse usuario = new UsuarioResponse(
                1L,
                "usuario@email.com",
                Perfil.USER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(usuarioService.buscarUsuarioPorId(1L))
                .thenReturn(usuario);

        when(jwtService.extrairEmail("token-admin"))
                .thenReturn("admin@email.com");

        when(jwtService.extrairPerfil("token-admin"))
                .thenReturn("ADMIN");

        when(jwtService.extrairId("token-admin"))
                .thenReturn(1L);

        mockMvc.perform(get("/api/usuarios/1")
                        .header("Authorization", "Bearer token-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("usuario@email.com"))
                .andExpect(jsonPath("$.perfil").value("USER"));


        verify(usuarioService).buscarUsuarioPorId(1L);
    }

    @Test
    void deveRetornar400AoBuscarUsuarioInexistente() throws Exception {
        when(usuarioService.buscarUsuarioPorId(999L))
                .thenThrow(new UsuarioNaoEncontradoException(999L));

        when(jwtService.extrairEmail("token-admin"))
                .thenReturn("admin@email.com");

        when(jwtService.extrairPerfil("token-admin"))
                .thenReturn("ADMIN");

        when(jwtService.extrairId("token-admin"))
                .thenReturn(1L);

        mockMvc.perform(get("/api/usuarios/999")
                        .header("Authorization", "Bearer token-admin"))
                .andExpect(status().isNotFound());

        verify(usuarioService).buscarUsuarioPorId(999L);
    }
}
