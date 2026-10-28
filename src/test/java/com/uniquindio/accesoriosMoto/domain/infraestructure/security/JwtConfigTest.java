package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.security.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtConfigTest {
    private final JwtConfig config = new JwtConfig("clave-unitaria-de-tests-01234567890123456789", "prueba");

    @Test
    public void converterInterpretaClaimRolConPrefixExistente() {

        var jwt = Jwt.withTokenValue("token").header("alg","HS256").subject("c1")
                .claim("rol","COMPRADOR").build();

        var autenticacion = config.jwtAuthenticationConverter().convert(jwt);

        assertEquals("c1", autenticacion.getName());
        assertEquals(List.of("ROLE_COMPRADOR"), autenticacion.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(a -> a.startsWith("ROLE_")).toList());
    }
    @Test void decoderSigueRechazandoEmisionFutura() {

        var reloj = Clock.fixed(Instant.parse("2026-10-27T12:00:00Z"), ZoneOffset.UTC);
        var claims = JwtClaimsSet.builder().issuer("prueba").subject("c1").claim("rol","COMPRADOR")
                .issuedAt(reloj.instant().plusSeconds(60)).expiresAt(reloj.instant().plusSeconds(120)).build();
        String token = config.jwtEncoder().encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();

        var accion1Error = catchThrowable(()->config.jwtDecoder(reloj).decode(token));

        assertInstanceOf(JwtException.class, accion1Error);
    }
}

