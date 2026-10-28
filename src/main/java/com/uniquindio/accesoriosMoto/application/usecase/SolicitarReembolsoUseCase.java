package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitarReembolsoUseCase {

    private final PedidoRepository repository;

    @Transactional
    public void ejecutar(String pedidoId, String compradorId, String motivo) {
        Pedido pedido = repository.obtenerPorId(pedidoId).orElseThrow();
        if (!pedido.getComprador().getId().equals(compradorId))
            throw new AccessDeniedException("No tiene permiso para realizar esta operación");
        pedido.solicitarReembolso(motivo);
        repository.registrar(pedido);
    }
}
