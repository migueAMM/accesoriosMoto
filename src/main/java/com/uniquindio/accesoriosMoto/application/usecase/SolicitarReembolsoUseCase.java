package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SolicitarReembolsoUseCase {
    private final PedidoRepository pedidoRepository;

    public SolicitarReembolsoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido ejecutar(String idPedido, String compradorId, String motivo) {
        Pedido pedido = pedidoRepository.obtenerPorId(idPedido)
                .orElseThrow(() -> new ReglaDominioException("No existe un pedido con ese id"));
        if (!pedido.getComprador().getId().equals(compradorId)) {
            throw new com.uniquindio.accesoriosMoto.application.exception.AccesoDenegadoException();
        }
        pedido.solicitarReembolso(motivo);
        pedidoRepository.registrar(pedido);
        return pedido;
    }
}
