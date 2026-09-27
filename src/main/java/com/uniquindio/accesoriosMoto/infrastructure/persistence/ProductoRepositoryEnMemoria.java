package com.uniquindio.accesoriosMoto.infrastructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductoRepositoryEnMemoria implements ProductoRepository{

    private final Map<String, Producto> productos = new HashMap<>();

    @Override
    public void guardar(Producto producto) {
        productos.put(producto.getId(), producto);
    }

    @Override
    public Optional<Producto> obtenerPorId(String id) {
        return Optional.ofNullable(productos.get(id));
    }
}
