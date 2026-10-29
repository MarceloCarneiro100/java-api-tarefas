package com.exemplo.api_tarefas.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey chave;

    public JwtService(@Value("${JWT_SECRET}") String segredo) {
        byte[] bytes = Decoders.BASE64.decode(segredo);
        this.chave = Keys.hmacShaKeyFor(bytes);
    }

    public String gerarToken(Long id, String email) {
        Date agora = new Date();

        return Jwts.builder()
                .subject(email)
                .claim("id", id)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + 3600000))
                .signWith(chave)
                .compact();
    }
}
