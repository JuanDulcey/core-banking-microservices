package com.devv.banking.cuentas.application.usecases;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Utilidad tolerante para interpretar fechas ISO-8601 y formato latino (d/M/yyyy).
 */
public final class FechaParser {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    private static final DateTimeFormatter REPORTE_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");

    private FechaParser() {
    }

    public static LocalDateTime parsearFechaHoraOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpio = texto.trim();
        try {
            return LocalDateTime.parse(limpio, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Intentar como LocalDate al inicio del día o con hora actual
        }
        LocalDate fecha = parsearFecha(limpio);
        return fecha.atTime(12, 0);
    }

    public static LocalDate parsearFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new BusinessValidationException("El valor de fecha no puede ser vacío");
        }
        String limpio = texto.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(limpio, formatter);
            } catch (DateTimeParseException ignored) {
                // Probar siguiente formato
            }
        }
        throw new BusinessValidationException(
                "Formato de fecha inválido: '" + texto + "'. Use yyyy-MM-dd o d/M/yyyy (ejemplo: 2022-02-01,2022-02-28)"
        );
    }

    public static RangoFechas parsearRango(String rangoCombinado, String fechaInicioParam, String fechaFinParam) {
        LocalDate inicio;
        LocalDate fin;

        if (rangoCombinado != null && !rangoCombinado.isBlank()) {
            String trimmed = rangoCombinado.trim();
            String[] partes;
            if (trimmed.contains(",")) {
                partes = trimmed.split(",", 2);
            } else if (trimmed.contains(":")) {
                partes = trimmed.split(":", 2);
            } else if (trimmed.contains("..")) {
                partes = trimmed.split("\\.\\.", 2);
            } else {
                // Si se envía una sola fecha, el rango cubre ese mismo día completo
                LocalDate unica = parsearFecha(trimmed);
                return new RangoFechas(unica, unica);
            }
            if (partes.length != 2) {
                throw new BusinessValidationException("Rango de fechas inválido. Ejemplo esperado: fecha=2022-02-01,2022-02-28");
            }
            inicio = parsearFecha(partes[0]);
            fin = parsearFecha(partes[1]);
        } else if (fechaInicioParam != null && !fechaInicioParam.isBlank()
                && fechaFinParam != null && !fechaFinParam.isBlank()) {
            inicio = parsearFecha(fechaInicioParam);
            fin = parsearFecha(fechaFinParam);
        } else {
            throw new BusinessValidationException(
                    "Debe especificar el parámetro 'fecha' con el rango de fechas (ejemplo: ?fecha=2022-02-01,2022-02-28&cliente=...)"
            );
        }

        if (fin.isBefore(inicio)) {
            throw new BusinessValidationException("La fecha final del reporte no puede ser anterior a la fecha inicial");
        }
        return new RangoFechas(inicio, fin);
    }

    public static String formatearFechaReporte(LocalDateTime fechaHora) {
        return fechaHora.toLocalDate().format(REPORTE_OUTPUT_FORMAT);
    }

    public record RangoFechas(LocalDate inicio, LocalDate fin) {}
}
