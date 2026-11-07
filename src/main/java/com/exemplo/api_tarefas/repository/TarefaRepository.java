package com.exemplo.api_tarefas.repository;

import com.exemplo.api_tarefas.entity.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Optional<Tarefa> findByIdAndUsuario_Id(Long id, Long usuarioId);
    List<Tarefa> findAllByUsuario_Id(Long usuarioId);
    Page<Tarefa> findAllByUsuario_Id(Long usuarioId, Pageable pageable);
    boolean existsByUsuario_Id(Long usuarioId);
}
