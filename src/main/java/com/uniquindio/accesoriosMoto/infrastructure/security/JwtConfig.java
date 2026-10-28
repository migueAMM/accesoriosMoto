package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;

@Configuration
public class JwtConfig {
    private final String jwtSecret;
    private final String issuer;

    public JwtConfig(@Value("${security.jwt.secret}") String jwtSecret,
                     @Value("${security.jwt.issuer}") String issuer) {
        if (jwtSecret == null || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32)
            throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes");
        if (issuer == null || issuer.isBlank())
            throw new IllegalArgumentException("El issuer JWT es obligatorio");
        this.jwtSecret = jwtSecret;
        this.issuer = issuer;
    }

    @Bean public Clock relojJwt() { return Clock.systemUTC(); }

    @Bean public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
    }

    @Bean public JwtDecoder jwtDecoder(Clock relojJwt) {
        var decoder = NimbusJwtDecoder.withSecretKey(
                        new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256).build();
        var tiempo = new JwtTimestampValidator(Duration.ZERO);
        tiempo.setClock(relojJwt);
        OAuth2TokenValidator<Jwt> contrato = jwt -> {
            String rol = jwt.getClaimAsString("rol");
            boolean valido = jwt.getSubject() != null && !jwt.getSubject().isBlank()
                    && jwt.getIssuedAt() != null && jwt.getExpiresAt() != null
                    && !jwt.getIssuedAt().isAfter(relojJwt.instant())
                    && ("COMPRADOR".equals(rol) || "ADMINISTRADOR".equals(rol));
            return valido ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Claims inválidos", null));
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(tiempo, new JwtIssuerValidator(issuer), contrato));
        return decoder;
    }

    @Bean public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("rol");
        authorities.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
