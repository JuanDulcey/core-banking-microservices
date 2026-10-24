package com.devv.banking.cuentas.application.usecases;

import com.devv.banking.cuentas.domain.model.ClienteSnapshot;
import com.devv.banking.cuentas.domain.ports.in.ClienteProjectionUseCase;
import com.devv.banking.cuentas.domain.ports.out.ClienteSnapshotRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClienteProjectionUseCaseImpl implements ClienteProjectionUseCase {

    private final ClienteSnapshotRepositoryPort snapshotRepository;

    @Override
    public void sincronizarEventoCliente(SincronizarClienteCommand command) {
        Instant eventTime = command.occurredAt() != null ? command.occurredAt() : Instant.now();
        Optional<ClienteSnapshot> existenteOpt = snapshotRepository.findById(command.id());

        if (existenteOpt.isPresent()) {
            ClienteSnapshot existente = existenteOpt.get();
            // Protección de idempotencia ante mensajes fuera de orden
            if (existente.getUpdatedAt() != null && eventTime.isBefore(existente.getUpdatedAt())) {
                log.info("Ignorando evento desactualizado para clienteId={} (eventTime={} < localUpdatedAt={})",
                        command.clienteId(), eventTime, existente.getUpdatedAt());
                return;
            }
            if ("DELETED".equalsIgnoreCase(command.eventType())) {
                existente.marcarInactivo(eventTime);
            } else {
                existente.actualizarDesdeEvento(
                        command.clienteId(),
                        command.nombre(),
                        command.identificacion(),
                        command.estado(),
                        eventTime
                );
            }
            snapshotRepository.save(existente);
            log.info("Proyección local de cliente actualizada [eventType={}, clienteId={}, estado={}]",
                    command.eventType(), existente.getClienteId(), existente.isEstado());
            return;
        }

        boolean estadoInicial = !"DELETED".equalsIgnoreCase(command.eventType().toUpperCase(Locale.ROOT)) && command.estado();
        ClienteSnapshot nuevoSnapshot = new ClienteSnapshot(
                command.id(),
                command.clienteId(),
                command.nombre(),
                command.identificacion(),
                estadoInicial,
                eventTime
        );
        snapshotRepository.save(nuevoSnapshot);
        log.info("Nueva proyección local de cliente registrada [clienteId={}, nombre={}]",
                nuevoSnapshot.getClienteId(), nuevoSnapshot.getNombre());
    }
}
