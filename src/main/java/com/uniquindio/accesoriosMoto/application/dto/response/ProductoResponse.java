package com.uniquindio.accesoriosMoto.application.dto.response;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;

public record ProductoResponse(String id, String tipo, String marca, String color, double precioMonto, String precioMoneda, String estadoPublicacion) {

    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getClass().getSimpleName(), producto.getMarca().nombre(), producto.getColor(), producto.getPrecio().monto(), producto.getPrecio().moneda(), producto.getEstadoPublicacion().name());
    }
}
