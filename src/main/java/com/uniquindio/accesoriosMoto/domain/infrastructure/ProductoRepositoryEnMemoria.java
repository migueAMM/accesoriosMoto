package com.uniquindio.accesoriosMoto.domain.infrastructure;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductoRepositoryEnMemoria implements ProductoRepository{

    private final Map<String, Producto> productos = new HashMap<>();

    @Override
    public void guardar(com.uniquindio.accesoriosMoto.domain.entity.Producto producto) {
        productos.put(producto.getId(), producto);
    }

    @Override
    public Optional<Producto> obtenrePorId(String id) {
        return Optional.ofNullable(productos.get(id));
    }
}
