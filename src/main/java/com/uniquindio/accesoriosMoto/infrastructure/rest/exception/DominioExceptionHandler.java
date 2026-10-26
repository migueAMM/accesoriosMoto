package com.uniquindio.accesoriosMoto.infrastructure.rest.exception;

import com.uniquindio.accesoriosMoto.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class DominioExceptionHandler {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> recursoNoEncontrado(RecursoNoEncontradoException error) {
        return ResponseEntity.status(404).body(Map.of("mensaje", error.getMessage()));
    }

    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, String>> reglaDominio(ReglaDominioException error) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", error.getMessage()));
    }
}
