package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.CadastroTarefaRequest;
import com.exemplo.api_tarefas.dto.TarefaResponse;
import com.exemplo.api_tarefas.dto.AtualizarTarefaRequest;
import com.exemplo.api_tarefas.entity.Categoria;
import com.exemplo.api_tarefas.entity.StatusTarefa;
import com.exemplo.api_tarefas.service.TarefaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {
    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    public TarefaResponse criar(
            @Valid @RequestBody CadastroTarefaRequest request) {

        return tarefaService.criarTarefa(request);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<TarefaResponse> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) StatusTarefa status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(
                    size = 10,
                    sort = "criadoEm",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return tarefaService.listarTarefas(
                titulo,
                categoria,
                status,
                dataInicio,
                dataFim,
                pageable);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public TarefaResponse buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarTarefaPorId(id);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public TarefaResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarTarefaRequest request
    ) {
        return tarefaService.atualizarTarefa(id, request);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        tarefaService.excluirTarefa(id);
    }
}
