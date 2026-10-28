package com.uniquindio.accesoriosMoto.domain.service;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerificadorDeCompra {
    private final PedidoRepository pedidoRepository;

    public void verificarQuePuedeComentar(String pedidoId, Comprador comprador, String productoId) {
        pedidoRepository.obtenerPorId(pedidoId).orElseThrow()
                .obtenerProductoParaComentar(comprador, productoId);
    }
}
