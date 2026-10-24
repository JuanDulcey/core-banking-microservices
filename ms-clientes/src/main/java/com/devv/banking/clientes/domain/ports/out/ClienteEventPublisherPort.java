package com.devv.banking.clientes.domain.ports.out;

import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.model.TipoEventoCliente;

public interface ClienteEventPublisherPort {

    void publish(Cliente cliente, TipoEventoCliente tipoEvento);
}
