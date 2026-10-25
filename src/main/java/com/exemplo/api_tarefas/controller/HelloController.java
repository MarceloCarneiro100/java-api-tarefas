package com.exemplo.api_tarefas.controller;

import com.exemplo.api_tarefas.dto.MensagemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public MensagemResponse hello() {
        return new MensagemResponse(
                "Minha primeira API Spring Boot",
                "funcionando"
        );
    }
}
