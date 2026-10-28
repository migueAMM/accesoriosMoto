package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.application.dto.response.TokenResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AutenticacionService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final Clock reloj;

    @Value("${security.jwt.expiration}")
    private Duration duracionConfigurada;

    @PostConstruct
    private void validarDuracion() {
        if (duracionConfigurada == null || duracionConfigurada.isNegative() || duracionConfigurada.isZero())
            throw new IllegalArgumentException("La duración JWT debe ser positiva");
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        if (request == null || request.correo() == null || request.contrasena() == null)
            throw new BadCredentialsException("Credenciales incorrectas");
        CustomUserDetails detalles;
        try {
            var autenticacion = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.correo().strip().toLowerCase(Locale.ROOT), request.contrasena()));
            detalles = (CustomUserDetails) autenticacion.getPrincipal();
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }
        List<String> roles = List.of(detalles.usuario().getRol().name());
        Instant emitidoEn = reloj.instant();
        Instant expiraEn = emitidoEn.plus(duracionConfigurada);
        String token = jwtTokenProvider.generar(detalles.usuario().getId(), roles, emitidoEn, expiraEn);
        return new TokenResponse(token, "Bearer", expiraEn, roles);
    }
}
