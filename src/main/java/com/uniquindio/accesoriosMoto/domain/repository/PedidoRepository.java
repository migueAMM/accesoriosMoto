package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;

import java.util.Optional;

public interface PedidoRepository {

    void registrar(Pedido pedido);

    java.util.List<Pedido> listarPorComprador(String compradorId);

    Optional<Pedido> obtenerPorId(String id);
}