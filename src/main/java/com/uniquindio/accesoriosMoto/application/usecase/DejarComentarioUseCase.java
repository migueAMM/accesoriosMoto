package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;

public class DejarComentarioUseCase {
    private final PedidoRepository pedidoRepository;
    private final ComentarioRepository comentarioRepository;

    public DejarComentarioUseCase(PedidoRepository pedidoRepository, ComentarioRepository comentarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    public Comentario ejecutar(String idComentario, Comprador comprador, String idPedido, String idProducto, int calificacion, String texto) {
        Pedido pedido = pedidoRepository.obtenerPorId(idPedido)
                .orElseThrow(() -> new ReglaDominioException("No existe un pedido con ese id"));
        Comentario comentario = Comentario.dejar(idComentario, comprador, pedido, idProducto, calificacion, texto);
        comentarioRepository.guardar(comentario);
        return comentario;
    }
}
