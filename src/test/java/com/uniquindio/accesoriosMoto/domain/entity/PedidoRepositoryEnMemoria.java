package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;

import java.util.HashMap;
import java.util.Map;

public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public void registrar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    public Pedido buscarPorId(String id) {
        return pedidos.get(id);
    }

    public int cantidadRegistrada() {
        return pedidos.size();
    }
}