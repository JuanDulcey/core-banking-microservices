package com.devv.banking.clientes.domain.ports.out;

import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.model.TipoEventoCliente;

/**
 * Puerto de Salida (Driven Port): contrato para notificar asíncronamente a otros microservicios
 * los cambios en el ciclo de vida de un {@link Cliente}.
 */
public interface ClienteEventPublisherPort {

    void publish(Cliente cliente, TipoEventoCliente tipoEvento);
}
