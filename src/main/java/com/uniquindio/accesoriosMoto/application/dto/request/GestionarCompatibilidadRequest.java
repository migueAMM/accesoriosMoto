package com.uniquindio.accesoriosMoto.application.dto.request;

import com.uniquindio.accesoriosMoto.domain.valueObject.TipoCompatibilidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GestionarCompatibilidadRequest(
        @NotBlank(message = "Debe indicar el producto") String productoId,
        @NotNull(message = "Debe indicar el tipo de compatibilidad") TipoCompatibilidad tipo,
        @NotBlank(message = "Debe indicar el modelo compatible") String modelo
) { }
