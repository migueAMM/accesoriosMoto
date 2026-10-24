package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record RegistrarVisorRequest (

        @NotBlank(message = "Debe indicar el identificador de la marca") String marcaId,
        @NotBlank(message = "Debe indicar el nombre de la marca") String marcaNombre,
        @NotNull(message = "Debe indicar el monto del precio")
        @Positive(message = "El monto del precio debe ser mayor a cero") Double precioMonto,
        @NotBlank(message = "Debe indicar la moneda del precio") String precioMoneda,
        @NotBlank(message = "Debe indicar el color") String color,
        @NotBlank(message = "Debe indicar el tipo de visor") String tipoVisor,
        @NotNull(message = "Debe indicar la lista de modelos compatibles")
        List<@NotBlank(message = "El modelo compatible no puede estar vacío") String> modelosCascoCompatibles
){}
