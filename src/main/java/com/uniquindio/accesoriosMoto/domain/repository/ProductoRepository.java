package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;

import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> obtenrePorId(String id);

    void guardar(Producto producto);
}
