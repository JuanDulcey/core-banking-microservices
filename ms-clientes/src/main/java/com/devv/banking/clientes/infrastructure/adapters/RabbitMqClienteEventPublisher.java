package com.devv.banking.clientes.infrastructure.adapters;

import com.devv.banking.clientes.application.dto.ClienteIntegrationEvent;
import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.model.TipoEventoCliente;
import com.devv.banking.clientes.domain.ports.out.ClienteEventPublisherPort;
import com.devv.banking.clientes.infrastructure.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 * Adaptador de salida para mensajería asíncrona.
 * Desacopla la transacción SQL del envío AMQP mediante @TransactionalEventListener(AFTER_COMMIT),
 * evitando publicar eventos fantasma si la transacción de base de datos hace rollback.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqClienteEventPublisher implements ClienteEventPublisherPort {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Cliente cliente, TipoEventoCliente tipoEvento) {
        ClienteIntegrationEvent event = new ClienteIntegrationEvent(
                UUID.randomUUID(),
                tipoEvento.name(),
                cliente.getId(),
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getIdentificacion(),
                cliente.isEstado(),
                Instant.now()
        );
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onClienteEventAfterCommit(ClienteIntegrationEvent event) {
        String routingKey = RabbitMqConfig.ROUTING_KEY_PREFIX + event.eventType().toLowerCase(Locale.ROOT);
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.CLIENTES_EXCHANGE, routingKey, event);
            log.info("Evento asíncrono publicado en RabbitMQ [type={}, clienteId={}, id={}]",
                    event.eventType(), event.clienteId(), event.id());
        } catch (Exception ex) {
            log.error("Error publicando evento asíncrono en RabbitMQ [eventId={}, clienteId={}]: {}",
                    event.eventId(), event.clienteId(), ex.getMessage(), ex);
        }
    }
}
