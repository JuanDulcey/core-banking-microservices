package com.devv.banking.cuentas.infrastructure.repositories;

import com.devv.banking.cuentas.infrastructure.entities.CuentaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA de cuentas bancarias.
 * Expone consultas {@code PESSIMISTIC_WRITE} ({@code SELECT ... FOR UPDATE}) para serializar operaciones concurrentes de saldo.
 */
public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, UUID> {

    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaEntity c WHERE c.numeroCuenta = :numeroCuenta")
    Optional<CuentaEntity> findByNumeroCuentaForUpdate(@Param("numeroCuenta") String numeroCuenta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaEntity c WHERE c.id = :id")
    Optional<CuentaEntity> findByIdForUpdate(@Param("id") UUID id);

    List<CuentaEntity> findByClienteId(UUID clienteId);

    List<CuentaEntity> findByEstado(boolean estado);

    boolean existsByNumeroCuenta(String numeroCuenta);
}
