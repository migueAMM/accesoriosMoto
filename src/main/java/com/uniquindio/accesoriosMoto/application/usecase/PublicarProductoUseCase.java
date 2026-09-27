package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;

public class PublicarProductoUseCase {

    private final ProductoRepository productoRepository;

    public PublicarProductoUseCase(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto ejecutar(String id) {
        Producto producto = productoRepository.obtenerPorId(id)
                .orElseThrow(() -> new ReglaDominioException("No existe un producto con ese id"));
        producto.publicar();
        productoRepository.guardar(producto);
        return producto;
    }
}
