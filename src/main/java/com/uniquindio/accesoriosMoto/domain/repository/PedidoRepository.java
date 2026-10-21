package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;

import java.util.Optional;

public interface PedidoRepository {
    void registrar(Pedido pedido);
    Optional<Pedido> obtenerPorId(String id);
}