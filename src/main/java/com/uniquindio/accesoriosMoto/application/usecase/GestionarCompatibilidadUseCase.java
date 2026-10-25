package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.ProductoCompatible;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Compatibilidad;
import com.uniquindio.accesoriosMoto.domain.valueObject.TipoCompatibilidad;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GestionarCompatibilidadUseCase {
    private final ProductoRepository productoRepository;

    public GestionarCompatibilidadUseCase(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }


    public Producto indicar(String idProducto, TipoCompatibilidad tipo, String modelo){
        Producto producto = obtenerProducto(idProducto);
        obtenerCompatible(producto).indicarCompatibilidad(new Compatibilidad(tipo, modelo));
        productoRepository.guardar(producto);
        return producto;
    }


    private ProductoCompatible obtenerCompatible(Producto producto){
        if(producto instanceof ProductoCompatible compatible){
            return compatible;
        }
        throw new ReglaDominioException("El producto no admite gestión de compatibilidad");
    }


    public Producto retirar(String idProducto, TipoCompatibilidad tipo, String modelo) {
        Producto producto = obtenerProducto(idProducto);
        obtenerCompatible(producto).retirarCompatibilidad(new Compatibilidad(tipo, modelo));
        productoRepository.guardar(producto);
        return producto;
    }


    private Producto obtenerProducto(String id){
        return productoRepository.obtenerPorId(id)
                .orElseThrow(() -> new ReglaDominioException("No existe un producto con ese id"));
    }
}
