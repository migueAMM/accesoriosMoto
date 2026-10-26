package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.DejarComentarioRequest;
import com.uniquindio.accesoriosMoto.application.dto.response.ComentarioResponse;
import com.uniquindio.accesoriosMoto.application.usecase.DejarComentarioUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ComentarioMapper;
import com.uniquindio.accesoriosMoto.infrastructure.security.IdentidadComprador;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {
    private final DejarComentarioUseCase dejarComentarioUseCase;
    private final ComentarioMapper mapper;

    public ComentarioController(DejarComentarioUseCase dejarComentarioUseCase, ComentarioMapper mapper) {
        this.dejarComentarioUseCase = dejarComentarioUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(@Valid @RequestBody DejarComentarioRequest request, Authentication autenticacion) {
        Comentario comentario = dejarComentarioUseCase.ejecutar(UUID.randomUUID().toString(), IdentidadComprador.verificar(autenticacion, request.compradorId()), request.pedidoId(), request.productoId(), request.calificacion(), request.texto());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/comentarios/{id}").buildAndExpand(comentario.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(comentario));
    }
}
