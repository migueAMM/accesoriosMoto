package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.time.LocalDateTime;
import java.util.List;

public class RegistrarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public RegistrarPedidoUseCase(PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido ejecutar(String id, Comprador comprador, List<ItemPedido> items, Precio precioTotal) {
        Pedido pedido = Pedido.registrar(id, comprador, items, LocalDateTime.now(), precioTotal);
        pedidoRepository.registrar(pedido);
        return pedido;
    }
}

