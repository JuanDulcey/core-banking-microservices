package com.devv.banking.cuentas.infrastructure.adapters;

import com.devv.banking.cuentas.domain.model.Movimiento;
import com.devv.banking.cuentas.domain.ports.out.MovimientoRepositoryPort;
import com.devv.banking.cuentas.infrastructure.repositories.MovimientoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador Secundario de Persistencia (Driven Adapter) que implementa {@link MovimientoRepositoryPort}.
 */
@Component
@RequiredArgsConstructor
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;
    private final MovimientoPersistenceMapper mapper;

    @Override
    public Movimiento save(Movimiento movimiento) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(movimiento)));
    }

    @Override
    public Optional<Movimiento> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Movimiento> findLatestByCuentaId(UUID cuentaId) {
        return jpaRepository.findFirstByCuentaIdOrderByFechaDesc(cuentaId).map(mapper::toDomain);
    }

    @Override
    public List<Movimiento> findAll() {
        return jpaRepository.findAllByOrderByFechaDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Movimiento> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Movimiento> findByCuentaIdsAndFechaBetween(List<UUID> cuentaIds, LocalDateTime desde, LocalDateTime hasta) {
        return jpaRepository.findByCuentaIdInAndFechaBetweenOrderByFechaDesc(cuentaIds, desde, hasta).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
