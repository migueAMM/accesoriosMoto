package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;

import java.util.Optional;

public interface CompradorRepository {
    Optional<Comprador> obtenerPorId(String id);
    void guardar(Comprador comprador);
}
