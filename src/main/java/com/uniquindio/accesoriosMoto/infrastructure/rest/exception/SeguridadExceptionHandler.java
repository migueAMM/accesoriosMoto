package com.uniquindio.accesoriosMoto.infrastructure.rest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class SeguridadExceptionHandler {
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> credenciales(CredencialesInvalidasException e) {
        return ResponseEntity.status(401).body(Map.of("mensaje", e.getMessage()));
    }
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, String>> acceso(AccesoDenegadoException e) {
        return ResponseEntity.status(403).body(Map.of("mensaje", e.getMessage()));
    }
    @ExceptionHandler(CorreoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> correo(CorreoDuplicadoException e) {
        return ResponseEntity.status(409).body(Map.of("mensaje", e.getMessage()));
    }
    @ExceptionHandler(ContrasenaInvalidaException.class)
    public ResponseEntity<Map<String, String>> contrasena(ContrasenaInvalidaException e) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
    }
}
