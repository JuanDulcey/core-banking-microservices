package com.devv.banking.cuentas.karate;

import com.devv.banking.cuentas.infrastructure.messaging.ClienteEventListener;
import com.devv.banking.cuentas.infrastructure.messaging.ClienteIntegrationEventPayload;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Suite Karate DSL integrada a JUnit 5 y Maven Surefire para ms-cuentas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CuentasKarateTest {

    private static final UUID JOSE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MARIANELA_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JUAN_UUID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ClienteEventListener clienteEventListener;

    @BeforeEach
    void sincronizarClientesBase() {
        Instant now = Instant.now();
        clienteEventListener.onClienteEvent(new ClienteIntegrationEventPayload(
                UUID.randomUUID(), "CREATED", JOSE_UUID, "CLI-JOSE", "Jose Lema", "1710000001", true, now
        ));
        clienteEventListener.onClienteEvent(new ClienteIntegrationEventPayload(
                UUID.randomUUID(), "CREATED", MARIANELA_UUID, "CLI-MARIANELA", "Marianela Montalvo", "1710000002", true, now
        ));
        clienteEventListener.onClienteEvent(new ClienteIntegrationEventPayload(
                UUID.randomUUID(), "CREATED", JUAN_UUID, "CLI-JUAN", "Juan Osorio", "1710000003", true, now
        ));
    }

    @Test
    @DisplayName("Karate DSL: Casos de Uso 2, 3, 4, F3 ('Saldo no disponible') y Caso de Uso 5 (/reportes)")
    void debeEjecutarEscenariosKarateCuentasMovimientosYReportes() {
        Results results = Runner.path("classpath:karate/cuentas_movimientos_reportes.feature")
                .systemProperty("karate.port", String.valueOf(serverPort))
                .parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
