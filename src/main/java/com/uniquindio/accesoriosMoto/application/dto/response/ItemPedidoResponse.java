package com.uniquindio.accesoriosMoto.application.dto.response;

public record ItemPedidoResponse(
        String id,
        String productoId,
        int cantidad,
        double subtotalMonto,
        String subtotalMoneda
) {}
