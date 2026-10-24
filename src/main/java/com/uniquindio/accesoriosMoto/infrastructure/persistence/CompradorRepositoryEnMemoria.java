package com.uniquindio.accesoriosMoto.infrastructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CompradorRepositoryEnMemoria implements CompradorRepository {
    private final Map<String, Comprador> compradores = new HashMap<>();

    @Override
    public Optional<Comprador> obtenerPorId(String id) {
        return Optional.ofNullable(compradores.get(id));
    }

    @Override
    public void guardar(Comprador comprador) {
        compradores.put(comprador.getId(), comprador);
    }
}
