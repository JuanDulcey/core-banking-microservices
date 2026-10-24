package com.devv.banking.cuentas.infrastructure.repositories;

import com.devv.banking.cuentas.infrastructure.entities.ClienteSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para consultar y persistir la proyección asíncrona {@link ClienteSnapshotEntity}.
 */
public interface ClienteSnapshotJpaRepository extends JpaRepository<ClienteSnapshotEntity, UUID> {

    Optional<ClienteSnapshotEntity> findByClienteIdIgnoreCase(String clienteId);

    Optional<ClienteSnapshotEntity> findByIdentificacion(String identificacion);

    Optional<ClienteSnapshotEntity> findFirstByNombreIgnoreCase(String nombre);
}
