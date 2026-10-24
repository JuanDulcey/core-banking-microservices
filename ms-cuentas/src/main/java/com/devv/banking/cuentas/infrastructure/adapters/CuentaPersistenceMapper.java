package com.devv.banking.cuentas.infrastructure.adapters;

import com.devv.banking.cuentas.domain.model.Cuenta;
import com.devv.banking.cuentas.infrastructure.entities.CuentaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuentaPersistenceMapper {

    CuentaEntity toEntity(Cuenta domain);

    default Cuenta toDomain(CuentaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Cuenta(
                entity.getId(),
                entity.getNumeroCuenta(),
                entity.getTipoCuenta(),
                entity.getSaldoInicial(),
                entity.getSaldoDisponible(),
                entity.isEstado(),
                entity.getClienteId(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
