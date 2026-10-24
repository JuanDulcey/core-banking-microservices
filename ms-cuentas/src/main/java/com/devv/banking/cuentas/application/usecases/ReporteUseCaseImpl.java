package com.devv.banking.cuentas.application.usecases;

import com.devv.banking.cuentas.application.usecases.FechaParser.RangoFechas;
import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.ResourceNotFoundException;
import com.devv.banking.cuentas.domain.model.ClienteSnapshot;
import com.devv.banking.cuentas.domain.model.Cuenta;
import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte;
import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte.DetalleMovimientoReporte;
import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte.ResumenCuenta;
import com.devv.banking.cuentas.domain.model.Movimiento;
import com.devv.banking.cuentas.domain.ports.in.ReporteUseCase;
import com.devv.banking.cuentas.domain.ports.out.ClienteSnapshotRepositoryPort;
import com.devv.banking.cuentas.domain.ports.out.CuentaRepositoryPort;
import com.devv.banking.cuentas.domain.ports.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReporteUseCaseImpl implements ReporteUseCase {

    private final ClienteSnapshotRepositoryPort clienteSnapshotRepository;
    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Override
    public EstadoCuentaReporte generarEstadoCuenta(
            String rangoFechas,
            String fechaInicio,
            String fechaFin,
            String clienteReferencia
    ) {
        if (clienteReferencia == null || clienteReferencia.isBlank()) {
            throw new BusinessValidationException("El parámetro 'cliente' es obligatorio para generar el estado de cuenta");
        }

        RangoFechas rango = FechaParser.parsearRango(rangoFechas, fechaInicio, fechaFin);

        ClienteSnapshot cliente = clienteSnapshotRepository.findByAnyIdentifier(clienteReferencia)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteReferencia));

        List<Cuenta> cuentasCliente = cuentaRepository.findByClienteId(cliente.getId());
        Map<UUID, Cuenta> cuentasPorId = cuentasCliente.stream()
                .collect(Collectors.toMap(Cuenta::getId, Function.identity()));

        LocalDateTime desde = rango.inicio().atStartOfDay();
        LocalDateTime hasta = rango.fin().atTime(LocalTime.MAX);

        List<UUID> cuentaIds = cuentasCliente.stream().map(Cuenta::getId).toList();
        List<Movimiento> movimientos = cuentaIds.isEmpty()
                ? List.of()
                : movimientoRepository.findByCuentaIdsAndFechaBetween(cuentaIds, desde, hasta);

        Map<UUID, Long> conteoPorCuenta = movimientos.stream()
                .collect(Collectors.groupingBy(Movimiento::getCuentaId, Collectors.counting()));

        List<ResumenCuenta> resumenCuentas = cuentasCliente.stream()
                .sorted(Comparator.comparing(Cuenta::getNumeroCuenta))
                .map(c -> new ResumenCuenta(
                        c.getNumeroCuenta(),
                        c.getTipoCuenta().getEtiqueta(),
                        c.getSaldoInicial(),
                        c.getSaldoDisponible(),
                        c.isEstado(),
                        conteoPorCuenta.getOrDefault(c.getId(), 0L).intValue()
                ))
                .toList();

        List<DetalleMovimientoReporte> detallesMovimientos = movimientos.stream()
                .sorted(Comparator.comparing(Movimiento::getFecha).reversed())
                .map(m -> {
                    Cuenta cuentaAsociada = cuentasPorId.get(m.getCuentaId());
                    String tipoCuenta = cuentaAsociada != null ? cuentaAsociada.getTipoCuenta().getEtiqueta() : "";
                    boolean estadoCuenta = cuentaAsociada == null || cuentaAsociada.isEstado();
                    return new DetalleMovimientoReporte(
                            FechaParser.formatearFechaReporte(m.getFecha()),
                            cliente.getNombre(),
                            m.getNumeroCuenta(),
                            tipoCuenta,
                            m.getSaldoInicial(),
                            estadoCuenta,
                            m.getValor(),
                            m.getSaldo()
                    );
                })
                .toList();

        BigDecimal totalCreditos = movimientos.stream()
                .map(Movimiento::getValor)
                .filter(v -> v.signum() > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalDebitos = movimientos.stream()
                .map(Movimiento::getValor)
                .filter(v -> v.signum() < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new EstadoCuentaReporte(
                cliente,
                rango.inicio(),
                rango.fin(),
                totalCreditos,
                totalDebitos,
                resumenCuentas,
                detallesMovimientos
        );
    }
}
