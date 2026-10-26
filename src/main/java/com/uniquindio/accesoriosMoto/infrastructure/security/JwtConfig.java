package com.uniquindio.accesoriosMoto.infrastructure.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    @Bean
    public Clock relojJwt() { return Clock.systemUTC(); }
    private SecretKey clave(JwtProperties properties) {
        return new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
    @Bean public JwtEncoder jwtEncoder(JwtProperties properties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave(properties)));
    }
    @Bean public JwtDecoder jwtDecoder(JwtProperties properties, Clock relojJwt) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(clave(properties)).macAlgorithm(MacAlgorithm.HS256).build();
        JwtTimestampValidator tiempo = new JwtTimestampValidator(Duration.ZERO);
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
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(tiempo, new JwtIssuerValidator(properties.issuer()), contrato));
        return decoder;
    }
}
