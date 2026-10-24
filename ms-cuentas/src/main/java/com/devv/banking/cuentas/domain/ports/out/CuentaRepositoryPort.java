package com.devv.banking.cuentas.domain.ports.out;

import com.devv.banking.cuentas.domain.model.Cuenta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuentaRepositoryPort {

    Cuenta save(Cuenta cuenta);

    Optional<Cuenta> findById(UUID id);

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    Optional<Cuenta> findByNumeroCuentaForUpdate(String numeroCuenta);

    Optional<Cuenta> findByIdForUpdate(UUID id);

    List<Cuenta> findAll();

    List<Cuenta> findByClienteId(UUID clienteId);

    List<Cuenta> findByEstado(boolean estado);

    boolean existsByNumeroCuenta(String numeroCuenta);
}
