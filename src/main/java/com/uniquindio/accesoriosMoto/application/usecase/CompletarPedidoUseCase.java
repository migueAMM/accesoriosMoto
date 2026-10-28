package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompletarPedidoUseCase {

    private final PedidoRepository repository;

    @Transactional
    public void ejecutar(String pedidoId) {
        Pedido pedido = repository.obtenerPorId(pedidoId).orElseThrow();
        pedido.completar();
        repository.registrar(pedido);
    }
}
