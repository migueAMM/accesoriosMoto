package com.uniquindio.accesoriosMoto.application.dto.request;

import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarChaquetaRequest(
        @NotBlank(message = "Debe indicar el identificador de la marca") String marcaId,
        @NotBlank(message = "Debe indicar el nombre de la marca") String marcaNombre,
        @NotNull(message = "Debe indicar el monto del precio")
        @Positive(message = "El monto del precio debe ser mayor a cero") Double precioMonto,
        @NotBlank(message = "Debe indicar la moneda del precio") String precioMoneda,
        @NotBlank(message = "Debe indicar el color") String color,
        @NotNull(message = "Debe indicar la talla") Talla talla,
        @NotNull(message = "Debe indicar el material") Material material,
        @NotNull(message = "Debe indicar la protección") Proteccion proteccion
) {
}
