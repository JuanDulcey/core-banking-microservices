package com.devv.banking.clientes;

import com.devv.banking.clientes.infrastructure.entities.ClienteEntity;
import com.devv.banking.clientes.infrastructure.repositories.ClienteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * F6: Prueba de integración en ms-clientes verificando API REST, herencia JPA JOINED, BCrypt y eventos AMQP.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Debe persistir Persona + Cliente en BD con contraseña cifrada BCrypt y emitir evento RabbitMQ")
    void debeCrearClienteIntegradoConPersistenciaYEvento() throws Exception {
        String requestJson = """
                {
                  "nombre": "Jose Lema Integracion",
                  "genero": "Masculino",
                  "edad": 34,
                  "identificacion": "1799887766",
                  "direccion": "Otavalo sn y principal",
                  "telefono": "098254785",
                  "clienteId": "CLI-INT-001",
                  "contrasena": "1234",
                  "estado": true
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value("CLI-INT-001"))
                .andExpect(jsonPath("$.nombre").value("Jose Lema Integracion"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        ClienteEntity persistido = clienteJpaRepository.findByClienteIdIgnoreCase("CLI-INT-001").orElseThrow();
        assertThat(persistido.getContrasena()).isNotEqualTo("1234");
        assertThat(passwordEncoder.matches("1234", persistido.getContrasena())).isTrue();

        verify(rabbitTemplate, atLeastOnce()).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}
