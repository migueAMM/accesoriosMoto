package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;

import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> obtenerPorId(String id);

    void guardar(Producto producto);
}
