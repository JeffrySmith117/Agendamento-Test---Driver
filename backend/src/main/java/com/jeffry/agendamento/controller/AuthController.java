package com.jeffry.agendamento.controller;

import com.jeffry.agendamento.dto.AuthDtos.*;
import com.jeffry.agendamento.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public TokenResponse registrar(@Valid @RequestBody RegisterRequest req) {
        return usuarioService.registrar(req);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return usuarioService.login(req);
    }
}
