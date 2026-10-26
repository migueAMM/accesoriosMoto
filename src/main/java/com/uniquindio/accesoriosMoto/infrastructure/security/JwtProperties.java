package com.uniquindio.accesoriosMoto.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ConfigurationProperties(prefix="security.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) {
    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes");
        }
        if (expiration == null || expiration.isNegative() || expiration.isZero() || issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("Configuración JWT inválida");
        }
    }
    @Override public String toString() { return "JwtProperties[secret=REDACTADO, expiration=" + expiration + ", issuer=" + issuer + "]"; }
}
