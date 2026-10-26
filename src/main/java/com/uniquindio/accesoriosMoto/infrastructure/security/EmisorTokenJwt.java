package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class EmisorTokenJwt implements EmisorToken {
    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock reloj;
    public EmisorTokenJwt(JwtEncoder encoder, JwtProperties properties, Clock reloj) {
        this.encoder = encoder;
        this.properties = properties;
        this.reloj = reloj;
    }
    @Override public String emitir(Usuario usuario) {
        var ahora = reloj.instant();
        var claims = JwtClaimsSet.builder().subject(usuario.getId()).issuer(properties.issuer())
                .issuedAt(ahora).expiresAt(ahora.plus(properties.expiration()))
                .claim("rol", usuario.getRol().name()).build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
