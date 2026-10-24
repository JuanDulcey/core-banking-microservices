package com.devv.banking.clientes.karate;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Suite Karate DSL integrada a JUnit 5 y Maven Surefire para ms-clientes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientesKarateTest {

    @LocalServerPort
    private int serverPort;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Karate DSL: Flujo E2E completo de CRUD y validaciones en /api/clientes y /clientes")
    void debeEjecutarEscenariosKarateClientes() {
        Results results = Runner.path("classpath:karate/clientes.feature")
                .systemProperty("karate.port", String.valueOf(serverPort))
                .parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
