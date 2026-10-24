package com.devv.banking.cuentas.infrastructure.adapters;

import com.devv.banking.cuentas.domain.model.Movimiento;
import com.devv.banking.cuentas.infrastructure.entities.MovimientoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovimientoPersistenceMapper {

    MovimientoEntity toEntity(Movimiento domain);

    default Movimiento toDomain(MovimientoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Movimiento(
                entity.getId(),
                entity.getCuentaId(),
                entity.getNumeroCuenta(),
                entity.getFecha(),
                entity.getTipoMovimiento(),
                entity.getSaldoInicial(),
                entity.getValor(),
                entity.getSaldo()
        );
    }
}
