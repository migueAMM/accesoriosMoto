package com.uniquindio.accesoriosMoto.application.dto.response;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.ProductoVentas;

public record ProductoVentasResponse(String productoId, long cantidadVendida) {

    public static ProductoVentasResponse desde(ProductoVentas fila) {
        return new ProductoVentasResponse(fila.productoId(), fila.cantidadVendida());
    }
}
