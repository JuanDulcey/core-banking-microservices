package com.devv.banking.cuentas.infrastructure.adapters;

import com.devv.banking.cuentas.domain.model.Cuenta;
import com.devv.banking.cuentas.domain.ports.out.CuentaRepositoryPort;
import com.devv.banking.cuentas.infrastructure.repositories.CuentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador Secundario de Persistencia (Driven Adapter) que implementa {@link CuentaRepositoryPort}.
 * Utiliza {@code saveAndFlush} para materializar inmediatamente incrementos de {@code @Version} dentro de la transacción.
 */
@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    private final CuentaPersistenceMapper mapper;

    @Override
    public Cuenta save(Cuenta cuenta) {
        return mapper.toDomain(jpaRepository.saveAndFlush(mapper.toEntity(cuenta)));
    }

    @Override
    public Optional<Cuenta> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuentaForUpdate(String numeroCuenta) {
        return jpaRepository.findByNumeroCuentaForUpdate(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public List<Cuenta> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cuenta> findByClienteId(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cuenta> findByEstado(boolean estado) {
        return jpaRepository.findByEstado(estado).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }
}
