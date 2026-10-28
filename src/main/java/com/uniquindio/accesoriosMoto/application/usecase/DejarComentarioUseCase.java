package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.service.VerificadorDeCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DejarComentarioUseCase {
    private final CompradorRepository compradorRepository;
    private final PedidoRepository pedidoRepository;
    private final ComentarioRepository comentarioRepository;
    private final VerificadorDeCompra verificador;

    @Transactional
    public Comentario ejecutar(String idComentario, String compradorId, String pedidoId, String productoId, int calificacion, String texto) {
        Comprador comprador = compradorRepository.obtenerPorId(compradorId).orElseThrow();
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId).orElseThrow();
        if (!pedido.getComprador().getId().equals(compradorId)) {
            throw new AccessDeniedException("No tiene permiso para realizar esta operación");
        }
        verificador.verificarQuePuedeComentar(pedidoId, comprador, productoId);
        Comentario comentario = Comentario.dejar(idComentario, comprador, pedido, productoId, calificacion, texto);
        comentarioRepository.guardar(comentario);
        return comentario;
    }
}
