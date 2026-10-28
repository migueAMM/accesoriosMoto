package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.response.PedidoResponse;
import com.uniquindio.accesoriosMoto.application.usecase.ListarPedidosUseCase;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class ConsultaPedidosController {
    private final ListarPedidosUseCase listarPedidosUseCase;
    private final PedidoMapper mapper;

    @GetMapping
    public List<PedidoResponse> listarPorComprador(Authentication autenticacion) {
        return listarPedidosUseCase.ejecutar(autenticacion.getName()).stream().map(mapper::toResponse).toList();
    }
}
