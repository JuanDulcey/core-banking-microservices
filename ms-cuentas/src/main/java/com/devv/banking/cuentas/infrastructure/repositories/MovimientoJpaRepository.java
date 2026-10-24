package com.devv.banking.cuentas.infrastructure.repositories;

import com.devv.banking.cuentas.infrastructure.entities.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para consultas cronológicas y por rango de fechas sobre {@link MovimientoEntity}.
 */
public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, UUID> {

    List<MovimientoEntity> findAllByOrderByFechaDesc();

    List<MovimientoEntity> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    Optional<MovimientoEntity> findFirstByCuentaIdOrderByFechaDesc(UUID cuentaId);

    List<MovimientoEntity> findByCuentaIdInAndFechaBetweenOrderByFechaDesc(
            List<UUID> cuentaIds,
            LocalDateTime desde,
            LocalDateTime hasta
    );
}
