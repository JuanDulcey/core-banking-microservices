package com.devv.banking.clientes.infrastructure.repositories;

import com.devv.banking.clientes.infrastructure.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    Optional<ClienteEntity> findByClienteIdIgnoreCase(String clienteId);

    Optional<ClienteEntity> findByIdentificacion(String identificacion);

    List<ClienteEntity> findByEstado(boolean estado);

    boolean existsByClienteIdIgnoreCase(String clienteId);

    boolean existsByIdentificacion(String identificacion);
}
