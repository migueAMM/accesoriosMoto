package com.uniquindio.accesoriosMoto.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        String id,
        String estado,
        LocalDateTime fecha,
        List<ItemPedidoResponse> items,
        double totalMonto,
        String totalMoneda
) {}
