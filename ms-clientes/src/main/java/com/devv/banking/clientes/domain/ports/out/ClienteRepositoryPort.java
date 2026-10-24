package com.devv.banking.clientes.domain.ports.out;

import com.devv.banking.clientes.domain.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(UUID id);

    Optional<Cliente> findByClienteId(String clienteId);

    Optional<Cliente> findByIdentificacion(String identificacion);

    List<Cliente> findAll();

    List<Cliente> findByEstado(boolean estado);

    boolean existsByClienteId(String clienteId);

    boolean existsByIdentificacion(String identificacion);

    void deleteById(UUID id);
}
