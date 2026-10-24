package com.devv.banking.cuentas.infrastructure.controllers;

import com.devv.banking.cuentas.application.dto.CuentaCreateRequest;
import com.devv.banking.cuentas.application.dto.CuentaPatchRequest;
import com.devv.banking.cuentas.application.dto.CuentaResponse;
import com.devv.banking.cuentas.application.dto.CuentaUpdateRequest;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase.ActualizarCuentaCommand;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase.CrearCuentaCommand;
import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase.CuentaDetalle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/cuentas", "/cuentas"})
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Operaciones CRU de cuentas bancarias")
public class CuentaController {

    private final CuentaUseCase cuentaUseCase;

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta bancaria")
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaCreateRequest request) {
        CuentaDetalle detalle = cuentaUseCase.crear(new CrearCuentaCommand(
                request.numeroCuenta(),
                request.tipoCuenta(),
                request.saldoInicial(),
                request.estado(),
                request.clienteId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(CuentaResponse.fromDetalle(detalle));
    }

    @GetMapping
    @Operation(summary = "Listar cuentas (filtrable opcionalmente por cliente y/o estado)")
    public ResponseEntity<List<CuentaResponse>> listar(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) Boolean estado
    ) {
        List<CuentaResponse> cuentas = cuentaUseCase.listar(cliente, estado).stream()
                .map(CuentaResponse::fromDetalle)
                .toList();
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por número de cuenta o UUID")
    public ResponseEntity<CuentaResponse> obtenerPorId(@PathVariable("id") String identificador) {
        CuentaDetalle detalle = cuentaUseCase.obtenerPorIdentificador(identificador);
        return ResponseEntity.ok(CuentaResponse.fromDetalle(detalle));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar completamente una cuenta (CRU)")
    public ResponseEntity<CuentaResponse> actualizar(
            @PathVariable("id") String identificador,
            @Valid @RequestBody CuentaUpdateRequest request
    ) {
        CuentaDetalle detalle = cuentaUseCase.actualizar(
                identificador,
                new ActualizarCuentaCommand(request.tipoCuenta(), request.estado())
        );
        return ResponseEntity.ok(CuentaResponse.fromDetalle(detalle));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente una cuenta (ej. activar/desactivar)")
    public ResponseEntity<CuentaResponse> actualizarParcial(
            @PathVariable("id") String identificador,
            @Valid @RequestBody CuentaPatchRequest request
    ) {
        CuentaDetalle detalle = cuentaUseCase.actualizarParcial(
                identificador,
                new ActualizarCuentaCommand(request.tipoCuenta(), request.estado())
        );
        return ResponseEntity.ok(CuentaResponse.fromDetalle(detalle));
    }
}
