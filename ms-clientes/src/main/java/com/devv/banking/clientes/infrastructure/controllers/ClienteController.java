package com.devv.banking.clientes.infrastructure.controllers;

import com.devv.banking.clientes.application.dto.ClienteCreateRequest;
import com.devv.banking.clientes.application.dto.ClientePatchRequest;
import com.devv.banking.clientes.application.dto.ClienteResponse;
import com.devv.banking.clientes.application.dto.ClienteUpdateRequest;
import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.ports.in.ClienteUseCase;
import com.devv.banking.clientes.domain.ports.in.ClienteUseCase.ActualizarClienteCommand;
import com.devv.banking.clientes.domain.ports.in.ClienteUseCase.ActualizarParcialClienteCommand;
import com.devv.banking.clientes.domain.ports.in.ClienteUseCase.CrearClienteCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

/**
 * Adaptador Primario REST (Driving Adapter) para operaciones CRUD sobre {@link Cliente} (F1).
 * Soporta tanto el prefijo {@code /api/clientes} como {@code /clientes} para máxima compatibilidad.
 */
@RestController
@RequestMapping({"/api/clientes", "/clientes"})
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Operaciones CRUD de Personas y Clientes")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteCreateRequest request) {
        Cliente creado = clienteUseCase.crear(new CrearClienteCommand(
                request.nombre(),
                request.genero(),
                request.edad(),
                request.identificacion(),
                request.direccion(),
                request.telefono(),
                request.clienteId(),
                request.contrasena(),
                request.estado()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.fromDomain(creado));
    }

    @GetMapping
    @Operation(summary = "Listar clientes (con filtro opcional por estado)")
    public ResponseEntity<List<ClienteResponse>> listar(@RequestParam(required = false) Boolean estado) {
        List<ClienteResponse> clientes = clienteUseCase.listarTodos(estado).stream()
                .map(ClienteResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por UUID, clienteId o identificación")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable("id") String identificador) {
        Cliente cliente = clienteUseCase.obtenerPorIdentificador(identificador);
        return ResponseEntity.ok(ClienteResponse.fromDomain(cliente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar completamente un cliente")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable("id") String identificador,
            @Valid @RequestBody ClienteUpdateRequest request
    ) {
        Cliente actualizado = clienteUseCase.actualizar(identificador, new ActualizarClienteCommand(
                request.nombre(),
                request.genero(),
                request.edad(),
                request.identificacion(),
                request.direccion(),
                request.telefono(),
                request.contrasena(),
                request.estado()
        ));
        return ResponseEntity.ok(ClienteResponse.fromDomain(actualizado));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un cliente")
    public ResponseEntity<ClienteResponse> actualizarParcial(
            @PathVariable("id") String identificador,
            @Valid @RequestBody ClientePatchRequest request
    ) {
        Cliente actualizado = clienteUseCase.actualizarParcial(identificador, new ActualizarParcialClienteCommand(
                request.nombre(),
                request.genero(),
                request.edad(),
                request.identificacion(),
                request.direccion(),
                request.telefono(),
                request.contrasena(),
                request.estado()
        ));
        return ResponseEntity.ok(ClienteResponse.fromDomain(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String identificador) {
        clienteUseCase.eliminar(identificador);
        return ResponseEntity.noContent().build();
    }
}
