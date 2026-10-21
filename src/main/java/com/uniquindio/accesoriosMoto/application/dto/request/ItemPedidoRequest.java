package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequest(
        @NotBlank(message = "Debe indicar el producto") String productoId,
        @NotNull(message = "Debe indicar la cantidad")
        @Positive(message = "La cantidad debe ser mayor a cero") Integer cantidad
) { }

