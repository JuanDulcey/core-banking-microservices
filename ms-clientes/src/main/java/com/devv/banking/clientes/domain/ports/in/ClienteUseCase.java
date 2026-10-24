package com.devv.banking.clientes.domain.ports.in;

import com.devv.banking.clientes.domain.model.Cliente;

import java.util.List;

/**
 * Puerto de Entrada (Driving Port): define el contrato de casos de uso disponibles
 * para la gestión integral (CRUD) de {@link Cliente}.
 */
public interface ClienteUseCase {

    Cliente crear(CrearClienteCommand command);

    Cliente obtenerPorIdentificador(String identificador);

    List<Cliente> listarTodos(Boolean estado);

    Cliente actualizar(String identificador, ActualizarClienteCommand command);

    Cliente actualizarParcial(String identificador, ActualizarParcialClienteCommand command);

    void eliminar(String identificador);

    record CrearClienteCommand(
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            String clienteId,
            String contrasena,
            Boolean estado
    ) {}

    record ActualizarClienteCommand(
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            String contrasena,
            Boolean estado
    ) {}

    record ActualizarParcialClienteCommand(
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            String contrasena,
            Boolean estado
    ) {}
}
