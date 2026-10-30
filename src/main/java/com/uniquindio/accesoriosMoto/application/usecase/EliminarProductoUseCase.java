package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EliminarProductoUseCase {

    private final ProductoRepository productoRepository;

    @Transactional
    public void eliminar(String id) {
        Producto producto = productoRepository.obtenerPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un producto con ese id"));
        producto.eliminar();
        productoRepository.guardar(producto);
    }
}
