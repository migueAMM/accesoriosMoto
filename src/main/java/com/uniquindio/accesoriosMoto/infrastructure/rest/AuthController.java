package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.accesoriosMoto.application.dto.response.TokenResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.UsuarioResponse;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarCompradorUseCase;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.UsuarioMapper;
import com.uniquindio.accesoriosMoto.infrastructure.security.AutenticacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AutenticacionService autenticacionService;
    private final RegistrarCompradorUseCase registrarCompradorUseCase;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return autenticacionService.login(request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String,String> credencialesInvalidas() {
        return Map.of("error", "Usuario o contraseña incorrectos");
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarCompradorRequest request) {
        String hash;
        try { hash = passwordEncoder.encode(request.contrasena()); }
        catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no es compatible con el codificador");
        }
        var comprador = registrarCompradorUseCase.ejecutar(UUID.randomUUID().toString(),
                request.nombre(), request.correo(), hash);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(comprador));
    }
}
