package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarPedidoRequest;
import com.uniquindio.accesoriosMoto.application.dto.request.SolicitarReembolsoRequest;
import com.uniquindio.accesoriosMoto.application.dto.response.PedidoResponse;
import com.uniquindio.accesoriosMoto.application.usecase.CompletarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.PedidoMapper;
import com.uniquindio.accesoriosMoto.infrastructure.security.IdentidadComprador;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final RegistrarPedidoUseCase registrarPedidoUseCase;
    private final CompletarPedidoUseCase completarPedidoUseCase;
    private final SolicitarReembolsoUseCase solicitarReembolsoUseCase;
    private final PedidoMapper mapper;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody RegistrarPedidoRequest request, Authentication autenticacion) {
        if (!autenticacion.getName().equals(request.compradorId()))
            throw new AccessDeniedException("No tiene permiso para realizar esta operación");
        var items = request.items().stream().map(i -> new RegistrarPedidoUseCase.ItemSolicitado(i.productoId(), i.cantidad())).toList();
        var pedido = registrarPedidoUseCase.ejecutar(UUID.randomUUID().toString(), autenticacion.getName(), items);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pedido.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(pedido));
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<Void> completar(@PathVariable("id") String id) {
        completarPedidoUseCase.ejecutar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reembolso")
    public ResponseEntity<Void> solicitarReembolso(@PathVariable("id") String id,
                                                   @Valid @RequestBody SolicitarReembolsoRequest request, Authentication autenticacion) {
        solicitarReembolsoUseCase.ejecutar(id, autenticacion.getName(), request.motivo());
        return ResponseEntity.ok().build();
    }
}
