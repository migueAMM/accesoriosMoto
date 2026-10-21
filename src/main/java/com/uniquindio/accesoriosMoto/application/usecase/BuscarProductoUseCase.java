package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;

import java.util.List;

public class BuscarProductoUseCase {
    private final ProductoRepository productoRepository;

    public BuscarProductoUseCase(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }

    public List<Producto> ejecutar(FiltroProducto filtro){
        return productoRepository.buscar(filtro);
    }
}
