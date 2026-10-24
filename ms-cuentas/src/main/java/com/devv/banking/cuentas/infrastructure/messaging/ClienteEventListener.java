package com.devv.banking.cuentas.infrastructure.messaging;

import com.devv.banking.cuentas.domain.ports.in.ClienteProjectionUseCase;
import com.devv.banking.cuentas.domain.ports.in.ClienteProjectionUseCase.SincronizarClienteCommand;
import com.devv.banking.cuentas.infrastructure.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador Primario de Mensajería AMQP (Driving Adapter): escucha asíncronamente los eventos
 * de {@code banking.clientes.exchange} y desvía mensajes inválidos a la Dead Letter Queue ({@code DLQ}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventListener {

    private final ClienteProjectionUseCase clienteProjectionUseCase;

    @RabbitListener(queues = RabbitMqConfig.CLIENTES_SYNC_QUEUE)
    public void onClienteEvent(ClienteIntegrationEventPayload payload) {
        if (payload == null || payload.id() == null || payload.clienteId() == null) {
            log.error("Evento de cliente inválido recibido en cola AMQP; enviando a DLQ: {}", payload);
            throw new AmqpRejectAndDontRequeueException("Evento de cliente incompleto");
        }
        try {
            clienteProjectionUseCase.sincronizarEventoCliente(new SincronizarClienteCommand(
                    payload.eventType(),
                    payload.id(),
                    payload.clienteId(),
                    payload.nombre(),
                    payload.identificacion(),
                    payload.estado(),
                    payload.occurredAt()
            ));
        } catch (Exception ex) {
            log.error("Fallo al sincronizar proyección de cliente [eventId={}, clienteId={}]: {}",
                    payload.eventId(), payload.clienteId(), ex.getMessage(), ex);
            throw new AmqpRejectAndDontRequeueException("Error procesando evento de cliente", ex);
        }
    }
}
