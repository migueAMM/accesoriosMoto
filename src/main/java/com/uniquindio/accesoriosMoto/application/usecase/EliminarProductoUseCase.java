package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EliminarProductoUseCase {

    private final ProductoRepository productoRepository;


    public void eliminar(String id) {

        Optional<Producto> productoOptional = productoRepository.obtenerPorId(id);
        if(productoOptional.isEmpty()){
            throw new ReglaDominioException("Este producto no existe");
        }

        Producto producto = productoOptional.get();

        if(producto.estaEliminado()){
            throw new ReglaDominioException("Este producto no existe");
        }

        producto.setEstadoPublicacion(EstadoPublicacion.ELIMINADO);
    }
}
