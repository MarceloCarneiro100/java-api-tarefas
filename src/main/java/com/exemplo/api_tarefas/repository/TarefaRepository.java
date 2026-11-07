package com.exemplo.api_tarefas.repository;

import com.exemplo.api_tarefas.entity.Categoria;
import com.exemplo.api_tarefas.entity.StatusTarefa;
import com.exemplo.api_tarefas.entity.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Optional<Tarefa> findByIdAndUsuario_Id(Long id, Long usuarioId);
    boolean existsByUsuario_Id(Long usuarioId);

    @Query("""
            SELECT t FROM Tarefa t
            WHERE t.usuario.id = :usuarioId
            AND (:categoria is NULL OR t.categoria = :categoria)
            AND (:status IS NULL OR t.status = :status)
            AND (:dataInicio IS NULL OR t.criadoEm >= :dataInicio)
            AND (:dataFim IS NULL OR t.criadoEm < :dataFim)
            """)
    Page<Tarefa> buscarComFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("categoria") Categoria categoria,
            @Param("status") StatusTarefa status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );
}
