package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import com.uniquindio.accesoriosMoto.infrastructure.security.JwtConfig;
import com.uniquindio.accesoriosMoto.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class JwtTokenProviderTest {

    private final JwtConfig config = new JwtConfig("clave-test", "test");
    private final Clock reloj = Clock.fixed(Instant.parse("2026-10-25T12:00:00Z"), ZoneOffset.UTC);
    private final JwtEncoder encoder = config.jwtEncoder();
    private final JwtDecoder decoder = config.jwtDecoder(reloj);

    private Comprador comprador() {
        return new Comprador("c1", "Ana", "ana@gmail.com", "hash");
    }

    private String emitir(Usuario usuario, Clock tiempo) {
        var provider = new JwtTokenProvider(encoder); setField(provider, "issuer", "test"); return provider.generar(usuario.getId(), usuario.getCorreo(), java.util.List.of(usuario.getRol().name()), tiempo.instant(), tiempo.instant().plusSeconds(1800));
    }

    @Test
    public void contieneIdentidadRolYExpiracionSinCredenciales() {

        Jwt jwt = decoder.decode(emitir(comprador(), reloj));

        assertThat(jwt.getSubject()).isEqualTo("c1");
        assertThat(jwt.getClaimAsString("correo")).isEqualTo("ana@gmail.com");
        assertThat(jwt.getClaimAsString("rol")).isEqualTo("COMPRADOR");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo("test");
        assertThat(jwt.getIssuedAt()).isEqualTo(reloj.instant());
        assertThat(jwt.getExpiresAt()).isEqualTo(reloj.instant().plusSeconds(1800));
        assertThat(jwt.getClaims()).doesNotContainKeys("contrasena", "password", "hash");
    }

    @Test
    public void emiteRolAdministradorDerivadoDelDominio() {

        var admin = new Administrador("a1", "Admin", "admin@gmail.com", "hash");

        var jwt = decoder.decode(emitir(admin, reloj));
        var resultado1 = jwt.getClaimAsString("rol");

        assertThat(resultado1).isEqualTo("ADMINISTRADOR");
        assertThat(jwt.getSubject()).isEqualTo("a1");
        assertThat(jwt.getClaimAsString("correo")).isEqualTo("admin@gmail.com");
        assertThat(jwt.getClaims()).doesNotContainKeys("contrasena", "password", "hash");
    }

    @Test
    public void rechazaTokenExpiradoConTiempoDeterminista() {

        String token = emitir(comprador(), Clock.offset(reloj, Duration.ofHours(-1)));

        var accion1Error = catchThrowable(() -> decoder.decode(token));

        assertThat(accion1Error).isInstanceOf(JwtException.class);
    }

    @Test
    public void rechazaFirmaManipulada() {

        String token = emitir(comprador(), reloj);
        String[] partes = token.split("\\.");
        partes[1] = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"c1\",\"rol\":\"ADMINISTRADOR\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        var accion1Error = catchThrowable(() -> decoder.decode(String.join(".", partes)));

        assertThat(accion1Error).isInstanceOf(JwtException.class);
    }

    @Test
    public void rechazaFirmaConOtraClave() {

        var otra = new JwtConfig("otra-clave-tests", "test");

        var accion1Error = catchThrowable(() -> otra.jwtDecoder(reloj).decode(emitir(comprador(), reloj)));

        assertThat(accion1Error).isInstanceOf(JwtException.class);
    }

    @Test
    public void rechazaIssuerIncorrecto() {

        var otra = new JwtConfig("clave-test", "otro");

        var accion1Error = catchThrowable(() -> otra.jwtDecoder(reloj).decode(emitir(comprador(), reloj)));

        assertThat(accion1Error).isInstanceOf(JwtException.class);
    }

    @Test
    public void rechazaClaimsAusentesORolNoPermitido() {

        var claimsInvalidos = new JwtClaimsSet[]{
                JwtClaimsSet.builder().issuer("test").subject("c1").issuedAt(reloj.instant()).claim("rol", "COMPRADOR").build(),
                JwtClaimsSet.builder().issuer("test").subject("c1").issuedAt(reloj.instant()).expiresAt(reloj.instant().plusSeconds(60)).claim("rol", "SUPERADMIN").build()};

        var tokensInvalidos = stream(claimsInvalidos).map(claim -> encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claim)).getTokenValue()).toList();
        var acciones = tokensInvalidos.stream().map(token -> (org.assertj.core.api.ThrowableAssert.ThrowingCallable) () -> decoder.decode(token)).toList();

        for (var accion : acciones) { assertThatThrownBy(accion).isInstanceOf(JwtException.class); }
    }

    @Test
    public void exigeSecretoAdecuadoYNoLoMuestraEnToString() {

        var accion1Error = catchThrowable(() -> new JwtConfig("corta", "test"));

        assertThat(accion1Error).isInstanceOf(IllegalArgumentException.class);
        assertThat(config.toString()).doesNotContain("clave-test");
    }
}
