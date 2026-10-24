package com.devv.banking.cuentas;

import com.devv.banking.cuentas.domain.exception.SaldoNoDisponibleException;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase.CrearCuentaCommand;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase.RegistrarMovimientoCommand;
import com.devv.banking.cuentas.infrastructure.messaging.ClienteEventListener;
import com.devv.banking.cuentas.infrastructure.messaging.ClienteIntegrationEventPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * F6: Prueba de integración avanzada verificando sincronización asíncrona e integridad bajo concurrencia real.
 */
@SpringBootTest
@ActiveProfiles("test")
class CuentasConcurrencyAndSyncIntegrationTest {

    @Autowired
    private ClienteEventListener clienteEventListener;

    @Autowired
    private CuentaUseCase cuentaUseCase;

    @Autowired
    private MovimientoUseCase movimientoUseCase;

    @Test
    @DisplayName("Debe evitar condiciones de carrera (race conditions) ante retiros concurrentes sobre la misma cuenta")
    void debeGarantizarConsistenciaTransaccionalEnRetirosSimultaneos() throws Exception {
        UUID personaId = UUID.randomUUID();
        clienteEventListener.onClienteEvent(new ClienteIntegrationEventPayload(
                UUID.randomUUID(),
                "CREATED",
                personaId,
                "CLI-CONC-01",
                "Cliente Concurrencia",
                "1700998877",
                true,
                Instant.now()
        ));

        cuentaUseCase.crear(new CrearCuentaCommand(
                "999001",
                "Ahorros",
                new BigDecimal("2000.00"),
                true,
                "CLI-CONC-01"
        ));

        // Lanzamos 2 hilos simultáneos intentando retirar 1500.00 cada uno sobre un saldo de 2000.00
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger exitosos = new AtomicInteger(0);
        AtomicInteger rechazadosPorSaldo = new AtomicInteger(0);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Void>> tareas = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                tareas.add(() -> {
                    readyLatch.countDown();
                    startLatch.await();
                    try {
                        movimientoUseCase.registrar(new RegistrarMovimientoCommand(
                                "999001", "Retiro", new BigDecimal("-1500.00"), null
                        ));
                        exitosos.incrementAndGet();
                    } catch (SaldoNoDisponibleException ex) {
                        if ("Saldo no disponible".equals(ex.getMessage())) {
                            rechazadosPorSaldo.incrementAndGet();
                        }
                    }
                    return null;
                });
            }

            List<Future<Void>> futures = tareas.stream().map(executor::submit).toList();
            readyLatch.await();
            startLatch.countDown();
            for (Future<Void> f : futures) {
                f.get();
            }
        }

        assertThat(exitosos.get()).isEqualTo(1);
        assertThat(rechazadosPorSaldo.get()).isEqualTo(1);

        BigDecimal saldoFinal = cuentaUseCase.obtenerPorIdentificador("999001").cuenta().getSaldoDisponible();
        assertThat(saldoFinal).isEqualByComparingTo("500.00");
    }
}
