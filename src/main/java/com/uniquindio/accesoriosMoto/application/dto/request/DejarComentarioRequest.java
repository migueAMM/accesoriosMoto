package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DejarComentarioRequest (
    @NotBlank(message = "Debe indicar el comprador") String compradorId,
    @NotBlank(message = "Debe indicar el pedido") String pedidoId,
    @NotBlank(message = "Debe indicar el producto") String productoId,
    @NotNull(message = "Debe indicar la calificación")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5") Integer calificacion,
    @NotBlank(message = "Debe escribir el comentario") String texto
){}
