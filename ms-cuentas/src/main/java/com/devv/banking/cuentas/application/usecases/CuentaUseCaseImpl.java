package com.devv.banking.cuentas.application.usecases;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.DuplicateResourceException;
import com.devv.banking.cuentas.domain.exception.ResourceNotFoundException;
import com.devv.banking.cuentas.domain.model.ClienteSnapshot;
import com.devv.banking.cuentas.domain.model.Cuenta;
import com.devv.banking.cuentas.domain.model.TipoCuenta;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase;
import com.devv.banking.cuentas.domain.ports.out.ClienteSnapshotRepositoryPort;
import com.devv.banking.cuentas.domain.ports.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CuentaUseCaseImpl implements CuentaUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final ClienteSnapshotRepositoryPort clienteSnapshotRepository;

    @Override
    public CuentaDetalle crear(CrearCuentaCommand command) {
        String numeroLimpio = command.numeroCuenta() != null ? command.numeroCuenta().trim() : "";
        if (cuentaRepository.existsByNumeroCuenta(numeroLimpio)) {
            throw new DuplicateResourceException("Ya existe una cuenta con el número: " + numeroLimpio);
        }

        ClienteSnapshot cliente = clienteSnapshotRepository.findByAnyIdentifier(command.clienteReferencia())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado o pendiente de sincronización asíncrona: " + command.clienteReferencia()
                ));

        if (!cliente.isEstado()) {
            throw new BusinessValidationException("No se puede abrir una cuenta para el cliente inactivo: " + cliente.getNombre());
        }

        TipoCuenta tipoCuenta = TipoCuenta.fromText(command.tipoCuenta());
        Cuenta nuevaCuenta = Cuenta.abrirNueva(
                numeroLimpio,
                tipoCuenta,
                command.saldoInicial(),
                command.estado(),
                cliente.getId()
        );

        Cuenta guardada = cuentaRepository.save(nuevaCuenta);
        return new CuentaDetalle(guardada, cliente.getClienteId(), cliente.getNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDetalle obtenerPorIdentificador(String identificador) {
        Cuenta cuenta = resolverCuenta(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + identificador));
        return enriquecerConCliente(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDetalle> listar(String clienteFiltro, Boolean estado) {
        List<Cuenta> cuentas;
        if (clienteFiltro != null && !clienteFiltro.isBlank()) {
            ClienteSnapshot cliente = clienteSnapshotRepository.findByAnyIdentifier(clienteFiltro)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteFiltro));
            cuentas = cuentaRepository.findByClienteId(cliente.getId()).stream()
                    .filter(c -> estado == null || c.isEstado() == estado)
                    .toList();
        } else if (estado != null) {
            cuentas = cuentaRepository.findByEstado(estado);
        } else {
            cuentas = cuentaRepository.findAll();
        }

        Map<UUID, ClienteSnapshot> snapshotsPorId = clienteSnapshotRepository.findAll().stream()
                .collect(Collectors.toMap(ClienteSnapshot::getId, Function.identity(), (a, b) -> a));

        return cuentas.stream()
                .map(c -> {
                    ClienteSnapshot snap = snapshotsPorId.get(c.getClienteId());
                    String codigo = snap != null ? snap.getClienteId() : c.getClienteId().toString();
                    String nombre = snap != null ? snap.getNombre() : "Desconocido";
                    return new CuentaDetalle(c, codigo, nombre);
                })
                .toList();
    }

    @Override
    public CuentaDetalle actualizar(String identificador, ActualizarCuentaCommand command) {
        Cuenta cuenta = resolverCuenta(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + identificador));

        TipoCuenta nuevoTipo = TipoCuenta.fromText(command.tipoCuenta());
        cuenta.actualizarDatosCuenta(nuevoTipo, command.estado());

        Cuenta guardada = cuentaRepository.save(cuenta);
        return enriquecerConCliente(guardada);
    }

    @Override
    public CuentaDetalle actualizarParcial(String identificador, ActualizarCuentaCommand command) {
        Cuenta cuenta = resolverCuenta(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + identificador));

        TipoCuenta nuevoTipo = (command.tipoCuenta() != null && !command.tipoCuenta().isBlank())
                ? TipoCuenta.fromText(command.tipoCuenta())
                : null;
        cuenta.actualizarDatosCuenta(nuevoTipo, command.estado());

        Cuenta guardada = cuentaRepository.save(cuenta);
        return enriquecerConCliente(guardada);
    }

    private Optional<Cuenta> resolverCuenta(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new BusinessValidationException("El identificador de la cuenta no puede ser vacío");
        }
        String trimmed = identificador.trim();
        Optional<Cuenta> porNumero = cuentaRepository.findByNumeroCuenta(trimmed);
        if (porNumero.isPresent()) {
            return porNumero;
        }
        try {
            return cuentaRepository.findById(UUID.fromString(trimmed));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private CuentaDetalle enriquecerConCliente(Cuenta cuenta) {
        return clienteSnapshotRepository.findById(cuenta.getClienteId())
                .map(snap -> new CuentaDetalle(cuenta, snap.getClienteId(), snap.getNombre()))
                .orElseGet(() -> new CuentaDetalle(cuenta, cuenta.getClienteId().toString(), "Desconocido"));
    }
}
