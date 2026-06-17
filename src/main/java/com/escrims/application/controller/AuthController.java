package com.escrims.application.controller;

import com.escrims.application.service.ScrimService;
import com.escrims.domain.model.Usuario;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthController {
    private final ScrimService scrimService;

    public AuthController(ScrimService scrimService) {
        this.scrimService = scrimService;
    }

    public Usuario registrarUsuario(String username, String email, String password, String region) {
        Usuario usuario = new Usuario(username, email, hashPassword(password), region);
        scrimService.registrarUsuario(usuario);
        return usuario;
    }

    public Map<String, Object> autenticar(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "jwt_token_aqui");
        response.put("usuario_id", UUID.randomUUID());
        response.put("mensaje", "Login exitoso");
        return response;
    }

    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}
