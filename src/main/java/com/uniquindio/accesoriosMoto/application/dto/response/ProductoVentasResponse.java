package com.uniquindio.accesoriosMoto.application.dto.response;

public record ProductoVentasResponse(String productoId, long cantidadVendida) {

    public static ProductoVentasResponse desde(ProductoVentas fila) {
        return new ProductoVentasResponse(fila.productoId(), fila.cantidadVendida());
    }
}
