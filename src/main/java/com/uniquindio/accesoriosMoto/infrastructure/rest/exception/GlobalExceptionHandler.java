package com.uniquindio.accesoriosMoto.infrastructure.rest.exception;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String,Object>> manejarReglaDominio(ReglaDominioException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String,Object>> manejarNoEncontrado(NoSuchElementException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, "El recurso solicitado no existe");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String,Object>> manejarDuplicidad(DuplicateKeyException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, "El correo ya está registrado");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> manejarAcceso(AccessDeniedException ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, "No tiene permiso para realizar esta operación");
    }

    @ExceptionHandler({org.springframework.web.bind.MethodArgumentNotValidException.class,
            org.springframework.validation.BindException.class, jakarta.validation.ConstraintViolationException.class,
            org.springframework.web.method.annotation.HandlerMethodValidationException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String,Object>> manejarValidacion(Exception ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Solicitud inválida");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> manejarErrorTecnico(ResponseStatusException ex) {
        return construirRespuesta(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason() == null ? "Solicitud inválida" : ex.getReason());
    }
    private ResponseEntity<Map<String,Object>> construirRespuesta(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("timestamp", LocalDateTime.now().toString(), "status", status.value(), "mensaje", mensaje));
    }
}
