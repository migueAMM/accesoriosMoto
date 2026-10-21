package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RegistrarPedidoRequest(
        @NotBlank(message = "Debe indicar el comprador") String compradorId,
        @NotEmpty(message = "Debe indicar al menos un ítem")
        List<@NotNull(message = "El ítem no puede ser nulo") @Valid ItemPedidoRequest> items
) { }
