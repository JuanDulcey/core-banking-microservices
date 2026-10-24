package com.devv.banking.cuentas.application.usecases;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.ResourceNotFoundException;
import com.devv.banking.cuentas.domain.model.Cuenta;
import com.devv.banking.cuentas.domain.model.Movimiento;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase;
import com.devv.banking.cuentas.domain.ports.out.CuentaRepositoryPort;
import com.devv.banking.cuentas.domain.ports.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MovimientoUseCaseImpl implements MovimientoUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Override
    public Movimiento registrar(RegistrarMovimientoCommand command) {
        String numeroCuenta = command.numeroCuenta() != null ? command.numeroCuenta().trim() : "";
        // Bloqueo pesimista sobre la fila de la cuenta para garantizar consistencia transaccional concurrente
        Cuenta cuenta = cuentaRepository.findByNumeroCuentaForUpdate(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));

        Movimiento nuevoMovimiento = cuenta.registrarMovimiento(
                command.valor(),
                command.tipoMovimiento(),
                command.fecha()
        );

        cuentaRepository.save(cuenta);
        return movimientoRepository.save(nuevoMovimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Movimiento obtenerPorId(UUID id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listar(String numeroCuenta) {
        if (numeroCuenta != null && !numeroCuenta.isBlank()) {
            return movimientoRepository.findByNumeroCuenta(numeroCuenta.trim());
        }
        return movimientoRepository.findAll();
    }

    @Override
    public Movimiento actualizar(UUID id, ActualizarMovimientoCommand command) {
        Movimiento existente = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        Cuenta cuenta = cuentaRepository.findByIdForUpdate(existente.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta asociada no encontrada: " + existente.getNumeroCuenta()));

        Movimiento ultimo = movimientoRepository.findLatestByCuentaId(cuenta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron movimientos para la cuenta"));

        if (!ultimo.getId().equals(existente.getId())) {
            throw new BusinessValidationException(
                    "Por integridad contable, solo se permite rectificar el último movimiento vigente de la cuenta"
            );
        }

        cuenta.recalcularUltimoMovimiento(existente, command.valor(), command.tipoMovimiento(), command.fecha());
        cuentaRepository.save(cuenta);
        return movimientoRepository.save(existente);
    }
}
