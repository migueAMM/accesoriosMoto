package com.uniquindio.accesoriosMoto.application.dto.request;

import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarBaulRequest(
        @NotBlank(message = "Debe indicar el identificador de la marca")
        String marcaId,
        @NotBlank(message = "Debe indicar el nombre de la marca")
        String marcaNombre,
        @NotNull(message = "Debe indicar el material del baúl")
        Material material,
        @NotNull(message = "Debe indicar la capacidad en litros")
        @Positive(message = "La capacidad en litros debe ser mayor a cero")
        Double capacidadLitros,
        @NotNull(message = "Debe indicar la capacidad máxima de carga")
        @Positive(message = "La capacidad máxima de carga debe ser mayor a cero")
        Double capacidadMaximaCarga,
        @NotBlank(message = "Debe indicar las dimensiones del baúl")
        String dimensiones,
        @NotBlank(message = "Debe indicar el color del baúl")
        String color,
        @NotNull(message = "Debe indicar el monto del precio")
        @Positive(message = "El monto del precio debe ser mayor a cero")
        Double precioMonto,
        @NotBlank(message = "Debe indicar la moneda del precio")
        String precioMoneda
) {
}
