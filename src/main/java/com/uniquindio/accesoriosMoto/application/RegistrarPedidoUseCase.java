package com.uniquindio.accesoriosMoto.application;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;

public class RegistrarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public RegistrarPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(Pedido pedido) {
        if (pedido == null) {
            throw new ReglaDominioException("El pedido no puede ser nulo");
        }
        pedido.validarParaRegistro();

        pedidoRepository.registrar(pedido);
    }

}