package com.devv.banking.clientes.infrastructure.adapters;

import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.infrastructure.entities.ClienteEntity;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct que traduce entre el modelo puro de dominio ({@link Cliente})
 * y la entidad de persistencia JPA ({@link ClienteEntity}), manteniendo ambas capas desacopladas.
 */
@Mapper(componentModel = "spring")
public interface ClientePersistenceMapper {

    ClienteEntity toEntity(Cliente domain);

    default Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Cliente(
                entity.getId(),
                entity.getNombre(),
                entity.getGenero(),
                entity.getEdad(),
                entity.getIdentificacion(),
                entity.getDireccion(),
                entity.getTelefono(),
                entity.getClienteId(),
                entity.getContrasena(),
                entity.isEstado(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
