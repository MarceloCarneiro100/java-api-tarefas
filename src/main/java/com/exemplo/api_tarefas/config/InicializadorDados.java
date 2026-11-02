package com.exemplo.api_tarefas.config;

import com.exemplo.api_tarefas.entity.Perfil;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.repository.UsuarioRepository;
import com.exemplo.api_tarefas.service.PasswordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InicializadorDados {
    @Bean
    public CommandLineRunner criarAdministrador(
            UsuarioRepository usuarioRepository,
            PasswordService passwordService
    ) {
        return args -> {
            String email = "admin@teste.com";

            if (!usuarioRepository.existsByEmail(email)) {
                Usuario admin = new Usuario();

                admin.setEmail(email);
                admin.setSenha(
                        passwordService.codificarSenha("Admin12345")
                );
                admin.setPerfil(Perfil.ADMIN);
                usuarioRepository.save(admin);
            }
        };
    }
}
