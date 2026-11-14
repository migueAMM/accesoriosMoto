package com.uniquindio.accesoriosMoto.application.dto.response;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;

public record ProductoResponse(String id, String tipo, String marca, String color, double precioMonto, String precioMoneda, String estadoPublicacion, String imagenUrl) {

    public ProductoResponse(String id, String tipo, String marca, String color, double precioMonto, String precioMoneda, String estadoPublicacion) {
        this(id, tipo, marca, color, precioMonto, precioMoneda, estadoPublicacion, null);
    }

    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getClass().getSimpleName(), producto.getMarca().nombre(), producto.getColor(), producto.getPrecio().monto(), producto.getPrecio().moneda(), producto.getEstadoPublicacion().name(), producto.getImagenUrl());
    }
}
