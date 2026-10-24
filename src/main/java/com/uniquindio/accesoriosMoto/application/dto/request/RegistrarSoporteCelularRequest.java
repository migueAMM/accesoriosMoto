package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarSoporteCelularRequest (

        @NotBlank(message = "Debe indicar el identificador de la marca") String marcaId,
        @NotBlank(message = "Debe indicar el nombre de la marca") String marcaNombre,
        @NotNull(message = "Debe indicar el monto del precio")
        @Positive(message = "El monto del precio debe ser mayor a cero") Double precioMonto,
        @NotBlank(message = "Debe indicar la moneda del precio") String precioMoneda,
        @NotBlank(message = "Debe indicar el color") String color,
        @NotBlank(message = "Debe indicar el tipo de sujeción") String tipoSujecion,
        @NotBlank(message = "Debe indicar el tamaño de celular compatible") String compatibleTamanoCelular
) {}