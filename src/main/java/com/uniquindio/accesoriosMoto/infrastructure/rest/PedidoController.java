package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarPedidoRequest;
import com.uniquindio.accesoriosMoto.application.dto.request.SolicitarReembolsoRequest;
import com.uniquindio.accesoriosMoto.application.dto.response.PedidoResponse;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final RegistrarPedidoUseCase registrarPedidoUseCase;
    private final SolicitarReembolsoUseCase solicitarReembolsoUseCase;
    private final PedidoMapper mapper;

    public PedidoController(RegistrarPedidoUseCase registrarPedidoUseCase,
                            SolicitarReembolsoUseCase solicitarReembolsoUseCase, PedidoMapper mapper) {
        this.registrarPedidoUseCase = registrarPedidoUseCase;
        this.solicitarReembolsoUseCase = solicitarReembolsoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> registrar(@Valid @RequestBody RegistrarPedidoRequest request) {
        Pedido pedido = registrarPedidoUseCase.ejecutar(
                UUID.randomUUID().toString(), request.compradorId(), request.items());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/pedidos/{id}").buildAndExpand(pedido.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(pedido));
    }

    @PutMapping("/{id}/reembolso")
    public ResponseEntity<PedidoResponse> solicitarReembolso(
            @PathVariable("id") String id, @Valid @RequestBody SolicitarReembolsoRequest request) {
        Pedido pedido = solicitarReembolsoUseCase.ejecutar(id, request.motivo());
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }
}
