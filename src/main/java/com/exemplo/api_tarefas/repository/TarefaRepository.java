package com.exemplo.api_tarefas.repository;

import com.exemplo.api_tarefas.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Optional<Tarefa> findByIdAndUsuario(Long id, Long usuarioId);
    List<Tarefa> findAllByUsuarioId(Long usuarioId);
}
