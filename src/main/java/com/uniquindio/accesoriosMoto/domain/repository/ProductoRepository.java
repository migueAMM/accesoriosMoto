package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> obtenerPorId(String id);

    void guardar(Producto producto);

    List<Producto> buscar(FiltroProducto filtro);
}
