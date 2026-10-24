package com.uniquindio.accesoriosMoto.infrastructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ProductoResponse;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getClass().getSimpleName(),
                producto.getMarca().nombre(),
                producto.getColor(),
                producto.getPrecio().monto(),
                producto.getPrecio().moneda(),
                producto.estaPublicado() ? EstadoPublicacion.PUBLICADO.name() : EstadoPublicacion.NO_PUBLICADO.name());
    }
}
