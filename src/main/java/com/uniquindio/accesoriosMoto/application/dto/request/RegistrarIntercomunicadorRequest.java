package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarIntercomunicadorRequest(
        @NotBlank(message = "Debe indicar el identificador de la marca") String marcaId,
        @NotBlank(message = "Debe indicar el nombre de la marca") String marcaNombre,
        @NotNull(message = "Debe indicar el monto del precio")
        @Positive(message = "El monto del precio debe ser mayor a cero") Double precioMonto,
        @NotBlank(message = "Debe indicar la moneda del precio") String precioMoneda,
        @NotBlank(message = "Debe indicar el color") String color,
        @NotNull(message = "Debe indicar el alcance")
        @Positive(message = "El alcance debe ser mayor a cero") Integer alcanceMetros,
        @NotNull(message = "Debe indicar la autonomía")
        @Positive(message = "La autonomía debe ser mayor a cero") Integer autonomiaHoras,
        @NotNull(message = "Debe indicar si tiene bluetooth") Boolean bluetooth
) {
}
