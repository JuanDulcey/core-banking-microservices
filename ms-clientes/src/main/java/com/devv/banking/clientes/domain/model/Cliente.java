package com.devv.banking.clientes.domain.model;

import com.devv.banking.clientes.domain.exception.BusinessValidationException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz Cliente que hereda de Persona, encapsulando sus invariantes de negocio.
 */
public class Cliente extends Persona {

    private String clienteId;
    private String contrasena;
    private boolean estado;
    private final Instant createdAt;
    private Instant updatedAt;

    public Cliente(
            UUID id,
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            String clienteId,
            String contrasena,
            boolean estado,
            Instant createdAt,
            Instant updatedAt
    ) {
        super(id, nombre, genero, edad, identificacion, direccion, telefono);
        setClienteId(clienteId);
        setContrasena(contrasena);
        this.estado = estado;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public static Cliente crearNuevo(
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            String clienteId,
            String contrasenaHash,
            Boolean estado
    ) {
        UUID nuevoId = UUID.randomUUID();
        String codigoCliente = (clienteId == null || clienteId.isBlank())
                ? generarCodigoCliente(nuevoId)
                : clienteId.trim();
        boolean estadoInicial = estado == null || estado;
        Instant ahora = Instant.now();

        return new Cliente(
                nuevoId,
                nombre,
                genero,
                edad,
                identificacion,
                direccion,
                telefono,
                codigoCliente,
                contrasenaHash,
                estadoInicial,
                ahora,
                ahora
        );
    }

    public void actualizarDatos(
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            boolean estado
    ) {
        setNombre(nombre);
        setGenero(genero);
        setEdad(edad);
        setIdentificacion(identificacion);
        setDireccion(direccion);
        setTelefono(telefono);
        this.estado = estado;
        this.updatedAt = Instant.now();
    }

    public void cambiarContrasena(String nuevaContrasenaHash) {
        setContrasena(nuevaContrasenaHash);
        this.updatedAt = Instant.now();
    }

    public void activar() {
        this.estado = true;
        this.updatedAt = Instant.now();
    }

    public void desactivar() {
        this.estado = false;
        this.updatedAt = Instant.now();
    }

    public final void setClienteId(String clienteId) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new BusinessValidationException("El clienteId es obligatorio");
        }
        this.clienteId = clienteId.trim();
    }

    public final void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new BusinessValidationException("La contraseña del cliente es obligatoria");
        }
        this.contrasena = contrasena;
    }

    private static String generarCodigoCliente(UUID id) {
        return "CLI-" + id.toString().substring(0, 8).toUpperCase();
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getContrasena() {
        return contrasena;
    }

    public boolean isEstado() {
        return estado;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(getId(), cliente.getId())
                && Objects.equals(clienteId, cliente.clienteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), clienteId);
    }
}
