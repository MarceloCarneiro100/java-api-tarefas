package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.service.JwtService;
import com.exemplo.api_tarefas.service.MensagemService;
import com.exemplo.api_tarefas.service.UsuarioService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioController.class)
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
}
