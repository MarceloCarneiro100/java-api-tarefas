package com.exemplo.api_tarefas.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String codificarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean verificarSenha(String senhaInformada, String senhaCodificada) {
        return passwordEncoder.matches(senhaInformada, senhaCodificada);
    }
}
