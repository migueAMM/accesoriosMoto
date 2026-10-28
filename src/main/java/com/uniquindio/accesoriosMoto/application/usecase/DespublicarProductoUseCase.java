package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DespublicarProductoUseCase {

    private final ProductoRepository productoRepository;

    @Transactional
    public Producto ejecutar(String id){
        Producto producto = productoRepository.obtenerPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un producto con ese id"));
        producto.despublicar();
        productoRepository.guardar(producto);
        return producto;
    }
}
