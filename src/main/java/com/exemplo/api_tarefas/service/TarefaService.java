package com.exemplo.api_tarefas.service;

import com.exemplo.api_tarefas.dto.AtualizarTarefaRequest;
import com.exemplo.api_tarefas.dto.CadastroTarefaRequest;
import com.exemplo.api_tarefas.dto.TarefaResponse;
import com.exemplo.api_tarefas.entity.Categoria;
import com.exemplo.api_tarefas.entity.Tarefa;
import com.exemplo.api_tarefas.entity.Usuario;
import com.exemplo.api_tarefas.exception.TarefaNaoEncontradaException;
import com.exemplo.api_tarefas.exception.UsuarioNaoEncontradoException;
import com.exemplo.api_tarefas.mapper.TarefaMapper;
import com.exemplo.api_tarefas.repository.TarefaRepository;
import com.exemplo.api_tarefas.repository.UsuarioRepository;
import com.exemplo.api_tarefas.security.UsuarioAutenticado;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarefaMapper tarefaMapper;

    public TarefaService(
            TarefaRepository tarefaRepository,
            UsuarioRepository usuarioRepository,
            TarefaMapper tarefaMapper
    ) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tarefaMapper = tarefaMapper;
    }

    public TarefaResponse criarTarefa(CadastroTarefaRequest request) {
        Usuario usuario = buscarUsuarioAutenticado();

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setUsuario(usuario);

        if (request.categoria() != null) {
            tarefa.setCategoria(request.categoria());
        }

        Tarefa tarefaSalva = tarefaRepository.save(tarefa);

        return tarefaMapper.toResponse(tarefaSalva);
    }

    public List<TarefaResponse> listarTarefas() {
        Usuario usuario = buscarUsuarioAutenticado();

        return tarefaRepository.findAllByUsuario_Id(usuario.getId())
                .stream()
                .map(tarefaMapper::toResponse)
                .toList();
    }

    public TarefaResponse buscarTarefaPorId(Long id) {
        Usuario usuario = buscarUsuarioAutenticado();

        Tarefa tarefa = tarefaRepository.findByIdAndUsuario_Id(id, usuario.getId())
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));

        return tarefaMapper.toResponse(tarefa);
    }

    public TarefaResponse atualizarTarefa(Long id, AtualizarTarefaRequest request) {
        Usuario usuario = buscarUsuarioAutenticado();

        Tarefa tarefa = tarefaRepository.findByIdAndUsuario_Id(id, usuario.getId())
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));

        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());

        if (request.categoria() != null) {
            tarefa.setCategoria(request.categoria());
        } else {
            tarefa.setCategoria(Categoria.TRABALHO);
        }

        tarefa.setStatus(request.status());

        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);

        return tarefaMapper.toResponse(tarefaAtualizada);
    }

    public void excluirTarefa(Long id) {
        Usuario usuario = buscarUsuarioAutenticado();

        Tarefa tarefa = tarefaRepository.findByIdAndUsuario_Id(id, usuario.getId())
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));

        tarefaRepository.delete(tarefa);
    }

    private UsuarioAutenticado obterUsuarioAutenticado() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null ||
                !(autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        return usuario;
    }

    private Usuario buscarUsuarioAutenticado() {
        UsuarioAutenticado autenticado = obterUsuarioAutenticado();
        return usuarioRepository.findById(autenticado.id())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(autenticado.id()));
    }
}
