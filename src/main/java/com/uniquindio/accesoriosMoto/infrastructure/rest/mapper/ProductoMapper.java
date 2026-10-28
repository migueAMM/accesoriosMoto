package com.uniquindio.accesoriosMoto.infrastructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ProductoResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.ProductoVentasResponse;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public ProductoResponse toResponse(Producto producto) { return ProductoResponse.desde(producto); }
    public ProductoVentasResponse toResponse(ProductoVentas ventas) { return ProductoVentasResponse.desde(ventas); }
}
