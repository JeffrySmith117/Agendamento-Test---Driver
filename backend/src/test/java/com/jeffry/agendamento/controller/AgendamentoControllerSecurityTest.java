package com.jeffry.agendamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeffry.agendamento.config.JwtAuthFilter;
import com.jeffry.agendamento.config.SecurityConfig;
import com.jeffry.agendamento.repository.AgendamentoRepository;
import com.jeffry.agendamento.repository.UsuarioRepository;
import com.jeffry.agendamento.repository.VeiculoRepository;
import com.jeffry.agendamento.service.AgendamentoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendamentoController.class)
@Import(SecurityConfig.class)
class AgendamentoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendamentoService agendamentoService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private VeiculoRepository veiculoRepository;

    @MockBean
    private AgendamentoRepository agendamentoRepository;

    @BeforeEach
    void configurarFiltroFake() throws Exception {
        // o mock do filtro JWT precisa deixar a requisicao seguir adiante
        // na cadeia de filtros; sem isso, o Mockito nao chama chain.doFilter()
        // e a requisicao nunca chega nas checagens reais de autorizacao
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Nested
    @DisplayName("GET /api/agendamentos (listagem por periodo - dados de todos os clientes)")
    class ListarPorPeriodo {

        @Test
        @DisplayName("bloqueia requisicao sem autenticacao")
        void bloqueiaSemAutenticacao() throws Exception {
            mockMvc.perform(get("/api/agendamentos")
                    .param("inicio", "2026-01-01T00:00:00")
                    .param("fim", "2026-01-02T00:00:00"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "CLIENTE")
        @DisplayName("bloqueia cliente comum autenticado (nao pode ver dados de outros clientes)")
        void bloqueiaClienteComum() throws Exception {
            mockMvc.perform(get("/api/agendamentos")
                    .param("inicio", "2026-01-01T00:00:00")
                    .param("fim", "2026-01-02T00:00:00"))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("permite acesso para usuario com role ADMIN")
        void permiteAdmin() throws Exception {
            mockMvc.perform(get("/api/agendamentos")
                    .param("inicio", "2026-01-01T00:00:00")
                    .param("fim", "2026-01-02T00:00:00"))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/agendamentos/meus (dados do proprio cliente)")
    class MeusAgendamentos {

        @Test
        @DisplayName("bloqueia requisicao sem autenticacao")
        void bloqueiaSemAutenticacao() throws Exception {
            mockMvc.perform(get("/api/agendamentos/meus"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/agendamentos (criar agendamento)")
    class CriarAgendamento {

        @Test
        @DisplayName("bloqueia criacao de agendamento sem autenticacao")
        void bloqueiaSemAutenticacao() throws Exception {
            mockMvc.perform(post("/api/agendamentos")
                    .contentType("application/json")
                    .content("{}"))
                .andExpect(status().isUnauthorized());
        }
    }
}