package com.exemplo.api_tarefas.service;

import org.springframework.stereotype.Service;

@Service
public class MensagemService {
    public String obterMensagem() {
        return "Minha primeira API em Spring Boot";
    }
}
