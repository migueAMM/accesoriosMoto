package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.accesoriosMoto.application.dto.response.LoginResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.UsuarioResponse;
import com.uniquindio.accesoriosMoto.application.usecase.IniciarSesionUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarCompradorUseCase;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrarCompradorUseCase registro;
    private final IniciarSesionUseCase login;
    private final UsuarioMapper mapper;
    public AuthController(RegistrarCompradorUseCase registro, IniciarSesionUseCase login, UsuarioMapper mapper) {
        this.registro = registro;
        this.login = login;
        this.mapper = mapper;
    }
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarCompradorRequest request) {
        return ResponseEntity.status(201).body(mapper.toResponse(registro.ejecutar(UUID.randomUUID().toString(),
                request.nombre(), request.correo(), request.contrasena())));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(login.ejecutar(request.correo(), request.contrasena()));
    }
}
