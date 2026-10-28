package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.RolSeguridad;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtEncoder encoder;
    @Value("${security.jwt.issuer}")
    private String issuer;

    public String generar(String usuarioId, Collection<String> roles, Instant emitidoEn, Instant expiraEn) {
        if (roles == null || roles.size() != 1) throw new IllegalArgumentException("Se requiere un único rol");
        String rol = RolSeguridad.valueOf(roles.iterator().next()).name();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var claims = JwtClaimsSet.builder().issuer(issuer).subject(usuarioId)
                .issuedAt(emitidoEn).expiresAt(expiraEn).claim("rol", rol).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
