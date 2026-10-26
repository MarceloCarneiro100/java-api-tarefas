package com.exemplo.api_tarefas.dto;

import java.util.List;

public record ErroResponse(
        int status,
        String erro,
        List<String> mensagens
) {}
