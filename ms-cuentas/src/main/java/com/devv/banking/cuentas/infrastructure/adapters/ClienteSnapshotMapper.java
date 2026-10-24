package com.devv.banking.cuentas.infrastructure.adapters;

import com.devv.banking.cuentas.domain.model.ClienteSnapshot;
import com.devv.banking.cuentas.infrastructure.entities.ClienteSnapshotEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteSnapshotMapper {

    ClienteSnapshotEntity toEntity(ClienteSnapshot domain);

    default ClienteSnapshot toDomain(ClienteSnapshotEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ClienteSnapshot(
                entity.getId(),
                entity.getClienteId(),
                entity.getNombre(),
                entity.getIdentificacion(),
                entity.isEstado(),
                entity.getUpdatedAt()
        );
    }
}
