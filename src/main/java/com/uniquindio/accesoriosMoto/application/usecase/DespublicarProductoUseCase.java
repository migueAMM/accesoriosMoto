package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DespublicarProductoUseCase {
    private final ProductoRepository productoRepository;

    public DespublicarProductoUseCase(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }


    public Producto ejecutar(String id){
        Producto producto = productoRepository.obtenerPorId(id)
                .orElseThrow(() -> new ReglaDominioException("No existe un producto con ese id"));
        producto.despublicar();
        productoRepository.guardar(producto);
        return producto;
    }
}
