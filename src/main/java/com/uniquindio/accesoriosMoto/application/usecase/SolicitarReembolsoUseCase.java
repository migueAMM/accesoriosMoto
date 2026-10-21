package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;

public class SolicitarReembolsoUseCase {
    private final PedidoRepository pedidoRepository;

    public SolicitarReembolsoUseCase(PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido ejecutar(String idPedido, String motivo){
        Pedido pedido = pedidoRepository.obtenerPorId(idPedido)
                .orElseThrow(() -> new ReglaDominioException("No existe un pedido con ese id"));
        pedido.solicitarReembolso(motivo);
        pedidoRepository.registrar(pedido);
        return pedido;
    }

}
