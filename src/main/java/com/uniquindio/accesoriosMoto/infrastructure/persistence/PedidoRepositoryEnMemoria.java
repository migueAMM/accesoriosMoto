package com.uniquindio.accesoriosMoto.infrastructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public void registrar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    public int cantidadRegistrada() {
        return pedidos.size();
    }

    public boolean contiene(Pedido pedido){
        return pedidos.containsValue(pedido);
    }
}