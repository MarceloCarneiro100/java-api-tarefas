package com.exemplo.api_tarefas.config;

import com.exemplo.api_tarefas.entity.*;
import com.exemplo.api_tarefas.repository.TarefaRepository;
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
            PasswordService passwordService,
            TarefaRepository tarefaRepository
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

            Usuario admin = usuarioRepository.findByEmail(email)
                    .orElseThrow();

            if (!tarefaRepository.existsByUsuario_Id(admin.getId())) {
                criarTarefa(
                        tarefaRepository,
                        admin,
                        "Estudar Spring Boot",
                        "Revisar os fundamentos do framework",
                        Categoria.ESTUDO,
                        StatusTarefa.PENDENTE
                );

                criarTarefa(
                        tarefaRepository,
                        admin,
                        "Organizar documentos",
                        "Separar documentos importantes",
                        Categoria.TRABALHO,
                        StatusTarefa.EM_ANDAMENTO
                );

                criarTarefa(
                        tarefaRepository,
                        admin,
                        "Revisar Java",
                        "Estudar orientação a objetos",
                        Categoria.ESTUDO,
                        StatusTarefa.CONCLUIDA
                );

                criarTarefa(
                        tarefaRepository,
                        admin,
                        "Fazer exercícios",
                        "Realizar atividade física",
                        Categoria.PESSOAL,
                        StatusTarefa.PENDENTE
                );

                criarTarefa(
                        tarefaRepository,
                        admin,
                        "Estudar JPA",
                        "Entender consultas e relacionamentos",
                        Categoria.ESTUDO,
                        StatusTarefa.EM_ANDAMENTO
                );
            }
        };
    }

    private void criarTarefa(
            TarefaRepository tarefaRepository,
            Usuario admin,
            String titulo,
            String descricao,
            Categoria categoria,
            StatusTarefa status
    ) {
        Tarefa tarefa = new Tarefa();

        tarefa.setTitulo(titulo);
        tarefa.setDescricao(descricao);
        tarefa.setCategoria(categoria);
        tarefa.setStatus(status);
        tarefa.setUsuario(admin);

        tarefaRepository.save(tarefa);
    }
}
