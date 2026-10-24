package com.devv.banking.cuentas.infrastructure.controllers;

import com.devv.banking.cuentas.application.dto.MovimientoCreateRequest;
import com.devv.banking.cuentas.application.dto.MovimientoResponse;
import com.devv.banking.cuentas.application.dto.MovimientoUpdateRequest;
import com.devv.banking.cuentas.application.usecases.FechaParser;
import com.devv.banking.cuentas.domain.model.Movimiento;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase.ActualizarMovimientoCommand;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase.RegistrarMovimientoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/movimientos", "/movimientos"})
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Registro transaccional y consulta de movimientos contables")
public class MovimientoController {

    private final MovimientoUseCase movimientoUseCase;

    @PostMapping
    @Operation(summary = "Registrar un movimiento (depósito positivo o retiro negativo) validando saldo disponible")
    public ResponseEntity<MovimientoResponse> registrar(@Valid @RequestBody MovimientoCreateRequest request) {
        Movimiento movimiento = movimientoUseCase.registrar(new RegistrarMovimientoCommand(
                request.numeroCuenta(),
                request.tipoMovimiento(),
                request.valor(),
                FechaParser.parsearFechaHoraOpcional(request.fecha())
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimientoResponse.fromDomain(movimiento));
    }

    @GetMapping
    @Operation(summary = "Listar movimientos (con filtro opcional por número de cuenta)")
    public ResponseEntity<List<MovimientoResponse>> listar(
            @RequestParam(required = false) String numeroCuenta
    ) {
        List<MovimientoResponse> movimientos = movimientoUseCase.listar(numeroCuenta).stream()
                .map(MovimientoResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un movimiento por ID")
    public ResponseEntity<MovimientoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(MovimientoResponse.fromDomain(movimientoUseCase.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rectificar el último movimiento vigente de una cuenta (CRU)")
    public ResponseEntity<MovimientoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MovimientoUpdateRequest request
    ) {
        Movimiento actualizado = movimientoUseCase.actualizar(id, new ActualizarMovimientoCommand(
                request.tipoMovimiento(),
                request.valor(),
                FechaParser.parsearFechaHoraOpcional(request.fecha())
        ));
        return ResponseEntity.ok(MovimientoResponse.fromDomain(actualizado));
    }
}
