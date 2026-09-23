package com.uniquindio.accesoriosMoto.application;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;

public class RegistrarCompraUseCase {

    private final PedidoRepository pedidoRepository;

    public RegistrarCompraUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(Pedido pedido) {
        pedidoRepository.registrar(pedido);
    }

}
