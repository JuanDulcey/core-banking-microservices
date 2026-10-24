package com.devv.banking.cuentas.domain.ports.out;

import com.devv.banking.cuentas.domain.model.Movimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovimientoRepositoryPort {

    Movimiento save(Movimiento movimiento);

    Optional<Movimiento> findById(UUID id);

    Optional<Movimiento> findLatestByCuentaId(UUID cuentaId);

    List<Movimiento> findAll();

    List<Movimiento> findByNumeroCuenta(String numeroCuenta);

    List<Movimiento> findByCuentaIdsAndFechaBetween(List<UUID> cuentaIds, LocalDateTime desde, LocalDateTime hasta);
}
