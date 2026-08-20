package com.jeffry.agendamento.service;

import com.jeffry.agendamento.dto.AuthDtos.*;
import com.jeffry.agendamento.config.JwtUtil;
import com.jeffry.agendamento.model.Usuario;
import com.jeffry.agendamento.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public TokenResponse registrar(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        usuario.setSenhaHash(passwordEncoder.encode(req.senha()));
        usuario.setTelefone(req.telefone());
        usuario.setPerfil(Usuario.Perfil.CLIENTE);

        usuarioRepository.save(usuario);

        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getPerfil().name());
        return new TokenResponse(token, usuario.getNome(), usuario.getPerfil().name());
    }

    public TokenResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.email())
            .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!passwordEncoder.matches(req.senha(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getPerfil().name());
        return new TokenResponse(token, usuario.getNome(), usuario.getPerfil().name());
    }
}
