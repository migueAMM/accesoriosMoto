package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DejarComentarioUseCase {
    private final CompradorRepository compradorRepository;
    private final PedidoRepository pedidoRepository;
    private final ComentarioRepository comentarioRepository;

    public DejarComentarioUseCase(CompradorRepository compradorRepository, PedidoRepository pedidoRepository, ComentarioRepository comentarioRepository) {
        this.compradorRepository = compradorRepository;
        this.pedidoRepository = pedidoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    public Comentario ejecutar(String idComentario, String compradorId, String idPedido, String idProducto, int calificacion, String texto) {
        Comprador comprador = compradorRepository.obtenerPorId(compradorId)
                .orElseThrow(() -> new ReglaDominioException("No existe un comprador con ese id"));

        Pedido pedido = pedidoRepository.obtenerPorId(idPedido)
                .orElseThrow(() -> new ReglaDominioException("No existe un pedido con ese id"));

        if (!pedido.getComprador().getId().equals(compradorId)) {
            throw new com.uniquindio.accesoriosMoto.application.exception.AccesoDenegadoException();
        }
        Comentario comentario = Comentario.dejar(idComentario, comprador, pedido, idProducto, calificacion, texto);
        comentarioRepository.guardar(comentario);
        return comentario;
    }
}
