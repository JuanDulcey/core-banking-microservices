package com.devv.banking.cuentas.application.usecases;

import com.devv.banking.cuentas.domain.exception.SaldoNoDisponibleException;
import com.devv.banking.cuentas.domain.model.Cuenta;
import com.devv.banking.cuentas.domain.model.TipoCuenta;
import com.devv.banking.cuentas.domain.ports.in.MovimientoUseCase.RegistrarMovimientoCommand;
import com.devv.banking.cuentas.domain.ports.out.CuentaRepositoryPort;
import com.devv.banking.cuentas.domain.ports.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @InjectMocks
    private MovimientoUseCaseImpl movimientoUseCase;

    @Test
    @DisplayName("No debe persistir cambios si el retiro excede el saldo disponible")
    void noDebePersistirCuandoSaldoInsuficiente() {
        Cuenta cuenta = Cuenta.abrirNueva("225487", TipoCuenta.CORRIENTE, new BigDecimal("100.00"), true, UUID.randomUUID());
        when(cuentaRepository.findByNumeroCuentaForUpdate("225487")).thenReturn(Optional.of(cuenta));

        RegistrarMovimientoCommand cmd = new RegistrarMovimientoCommand(
                "225487", "Retiro", new BigDecimal("-250.00"), null
        );

        assertThatThrownBy(() -> movimientoUseCase.registrar(cmd))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .hasMessage("Saldo no disponible");

        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }
}
