package com.devv.banking.cuentas.domain.model;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.SaldoNoDisponibleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias puras del dominio financiero Cuenta y Movimiento (F2 y F3).
 */
class CuentaDomainTest {

    @Test
    @DisplayName("Debe abrir cuenta con saldo disponible igual al saldo inicial y permitir depósitos/retiros")
    void debeRegistrarDepositosYRetirosActualizandoSaldo() {
        Cuenta cuenta = Cuenta.abrirNueva(
                "478758",
                TipoCuenta.AHORROS,
                new BigDecimal("2000.00"),
                true,
                UUID.randomUUID()
        );

        assertThat(cuenta.getSaldoInicial()).isEqualByComparingTo("2000.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("2000.00");

        Movimiento retiro = cuenta.registrarMovimiento(
                new BigDecimal("-575.00"),
                null,
                LocalDateTime.of(2022, 2, 10, 10, 0)
        );

        assertThat(retiro.getTipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(retiro.getSaldoInicial()).isEqualByComparingTo("2000.00");
        assertThat(retiro.getValor()).isEqualByComparingTo("-575.00");
        assertThat(retiro.getSaldo()).isEqualByComparingTo("1425.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("1425.00");

        Movimiento deposito = cuenta.registrarMovimiento(
                new BigDecimal("600.00"),
                "Deposito",
                LocalDateTime.of(2022, 2, 11, 10, 0)
        );

        assertThat(deposito.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(deposito.getSaldoInicial()).isEqualByComparingTo("1425.00");
        assertThat(deposito.getSaldo()).isEqualByComparingTo("2025.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("2025.00");
    }

    @Test
    @DisplayName("F3: Debe lanzar SaldoNoDisponibleException con el mensaje exacto 'Saldo no disponible'")
    void debeAlertarSaldoNoDisponibleCuandoFondosInsuficientes() {
        Cuenta cuenta = Cuenta.abrirNueva(
                "495878",
                TipoCuenta.AHORROS,
                BigDecimal.ZERO,
                true,
                UUID.randomUUID()
        );

        assertThatThrownBy(() -> cuenta.registrarMovimiento(new BigDecimal("-1.00"), "Retiro", null))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .hasMessage("Saldo no disponible");

        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Debe permitir retirar la totalidad del saldo disponible dejando la cuenta en 0.00")
    void debePermitirRetiroTotalHastaSaldoCero() {
        Cuenta cuenta = Cuenta.abrirNueva(
                "496825",
                TipoCuenta.AHORROS,
                new BigDecimal("540.00"),
                true,
                UUID.randomUUID()
        );

        // También valida normalización cuando se envía tipoMovimiento 'Retiro' con monto positivo
        Movimiento retiroTotal = cuenta.registrarMovimiento(new BigDecimal("540.00"), "Retiro", null);

        assertThat(retiroTotal.getValor()).isEqualByComparingTo("-540.00");
        assertThat(retiroTotal.getSaldo()).isEqualByComparingTo("0.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Debe impedir movimientos sobre una cuenta inactiva")
    void debeRechazarMovimientosEnCuentaInactiva() {
        Cuenta cuenta = Cuenta.abrirNueva(
                "225487",
                TipoCuenta.CORRIENTE,
                new BigDecimal("100.00"),
                false,
                UUID.randomUUID()
        );

        assertThatThrownBy(() -> cuenta.registrarMovimiento(new BigDecimal("50.00"), "Deposito", null))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("inactiva");
    }

    @Test
    @DisplayName("Debe interpretar tipos de movimiento con tildes ('Débito' / 'Crédito') sin errores de signo")
    void debeNormalizarTiposConTildes() {
        Cuenta cuenta = Cuenta.abrirNueva(
                "585545",
                TipoCuenta.CORRIENTE,
                new BigDecimal("1000.00"),
                true,
                UUID.randomUUID()
        );

        Movimiento debito = cuenta.registrarMovimiento(new BigDecimal("250.00"), "Débito", null);
        assertThat(debito.getTipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(debito.getValor()).isEqualByComparingTo("-250.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("750.00");

        Movimiento credito = cuenta.registrarMovimiento(new BigDecimal("150.00"), "Crédito", null);
        assertThat(credito.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(credito.getValor()).isEqualByComparingTo("150.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("900.00");
    }
}
