package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.security.AutenticacionService;
import com.uniquindio.accesoriosMoto.infrastructure.security.JwtTokenProvider;
import com.uniquindio.accesoriosMoto.infrastructure.security.UserConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AutenticacionServiceTest {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UsuarioJpaDataRepository usuarios = mock(UsuarioJpaDataRepository.class);
    private final JwtTokenProvider tokens = mock(JwtTokenProvider.class);
    private final Clock reloj = Clock.fixed(Instant.parse("2026-10-27T12:00:00Z"), ZoneOffset.UTC);

    private AutenticacionService servicio() {
        var provider = new DaoAuthenticationProvider(new UserConfig().userDetailsService(usuarios));
        provider.setPasswordEncoder(encoder);
        var servicio = new AutenticacionService(new ProviderManager(provider), tokens, reloj);
        ReflectionTestUtils.setField(servicio, "duracionConfigurada", Duration.ofMinutes(30));
        return servicio;
    }
    @Test
    public void loginUsaAuthenticationManagerYEntregaInstantesAlProvider() {

        var usuario = new CompradorEntity();
        usuario.setId("c1");
        usuario.setCorreo("ana@correo.com");
        usuario.setContrasena(encoder.encode("ClaveSegura123"));
        when(usuarios.findByCorreo("ana@correo.com")).thenReturn(Optional.of(usuario));
        when(tokens.generar("c1", List.of("COMPRADOR"), reloj.instant(), reloj.instant().plusSeconds(1800))).thenReturn("jwt");

        var respuesta = servicio().login(new LoginRequest(" ANA@CORREO.COM ", "ClaveSegura123"));

        assertEquals("Bearer", respuesta.type());
        assertEquals(List.of("COMPRADOR"), respuesta.roles());
        assertEquals(reloj.instant().plusSeconds(1800), respuesta.expireAt());
        assertEquals("jwt", respuesta.token());
        verify(tokens).generar("c1", List.of("COMPRADOR"), reloj.instant(), reloj.instant().plusSeconds(1800));
    }
    @Test
    public void credencialesIncorrectasONoRegistradasNuncaEmitenToken() {

        var usuario = new CompradorEntity();
        usuario.setCorreo("ana@correo.com");
        usuario.setContrasena(encoder.encode("ClaveSegura123"));
        when(usuarios.findByCorreo("ana@correo.com")).thenReturn(Optional.of(usuario));
        var servicio = servicio();

        var accion1Error = catchThrowable(() -> servicio.login(new LoginRequest("ana@correo.com", "Incorrecta123")));
        var accion2Error = catchThrowable(() -> servicio.login(new LoginRequest("ausente@correo.com", "Incorrecta123")));

        assertInstanceOf(BadCredentialsException.class, accion1Error);
        assertInstanceOf(BadCredentialsException.class, accion2Error);
        verifyNoInteractions(tokens);
    }
}
