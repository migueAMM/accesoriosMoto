package com.uniquindio.accesoriosMoto.infrastructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.HashMap;
import java.util.List;
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

    @Override
    public List<Producto> buscar(FiltroProducto filtro){
        if(filtro == null){
            throw new ReglaDominioException("Debe proporcionar los criterios de búsqueda");
        }
        return productos.values().stream().filter(filtro::coincideCon).toList();
    }
}
