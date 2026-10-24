package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.ProductoCompatible;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Compatibilidad;
import com.uniquindio.accesoriosMoto.domain.valueObject.TipoCompatibilidad;

public class GestionarCompatibilidadUseCase {
    private final ProductoRepository productoRepository;

    public GestionarCompatibilidadUseCase(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }

    /**
     * Indica una compatibilidad para un producto dado su id, tipo y modelo.
     * @param idProducto
     * @param tipo
     * @param modelo
     * @return
     */
    public Producto indicar(String idProducto, TipoCompatibilidad tipo, String modelo){
        Producto producto = obtenerProducto(idProducto);
        obtenerCompatible(producto).indicarCompatibilidad(new Compatibilidad(tipo, modelo));
        productoRepository.guardar(producto);
        return producto;
    }

    /**
     * Retira una compatibilidad para un producto dado su id, tipo y modelo.
     * @param producto
     * @return
     */
    private ProductoCompatible obtenerCompatible(Producto producto){
        if(producto instanceof ProductoCompatible compatible){
            return compatible;
        }
        throw new ReglaDominioException("El producto no admite gestión de compatibilidad");
    }

    /**
     * Retira una compatibilidad para un producto dado su id, tipo y modelo.
     * @param idProducto
     * @param tipo
     * @param modelo
     * @return
     */
    public Producto retirar(String idProducto, TipoCompatibilidad tipo, String modelo) {
        Producto producto = obtenerProducto(idProducto);
        obtenerCompatible(producto).retirarCompatibilidad(new Compatibilidad(tipo, modelo));
        productoRepository.guardar(producto);
        return producto;
    }


    /**
     * Obtiene un producto por su id, lanzando una excepción si no existe.
     * @param id
     * @return
     */
    private Producto obtenerProducto(String id){
        return productoRepository.obtenerPorId(id)
                .orElseThrow(() -> new ReglaDominioException("No existe un producto con ese id"));
    }
}
