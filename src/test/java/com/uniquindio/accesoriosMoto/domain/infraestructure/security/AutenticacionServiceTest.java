package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.security.AutenticacionService;
import com.uniquindio.accesoriosMoto.infrastructure.security.CustomUserDetails;
import com.uniquindio.accesoriosMoto.infrastructure.security.JwtTokenProvider;
import com.uniquindio.accesoriosMoto.infrastructure.security.UserConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
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
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

public class AutenticacionServiceTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UsuarioJpaDataRepository usuarios = mock(UsuarioJpaDataRepository.class);
    private final JwtTokenProvider tokens = mock(JwtTokenProvider.class);
    private final Clock reloj = Clock.fixed(Instant.parse("2026-10-27T12:00:00Z"), ZoneOffset.UTC);

    private AutenticacionService servicio() {
        var provider = new DaoAuthenticationProvider(new UserConfig().userDetailsService(usuarios));
        provider.setPasswordEncoder(encoder);
        var servicio = new AutenticacionService(spy(new ProviderManager(provider)), tokens, reloj);
        ReflectionTestUtils.setField(servicio, "duracionConfigurada", Duration.ofMinutes(30));
        return servicio;
    }

    @Test
    public void loginUsaAuthenticationManagerYEntregaInstantesAlProvider() {

        var usuario = new CompradorEntity();
        usuario.setId("c1");
        usuario.setCorreo("ana@gmail.com");
        usuario.setContrasena(encoder.encode("ClaveSegura123"));
        when(usuarios.findByCorreo("ana@gmail.com")).thenReturn(Optional.of(usuario));
        when(tokens.generar("c1", "ana@gmail.com", List.of("COMPRADOR"), reloj.instant(), reloj.instant().plusSeconds(1800))).thenReturn("jwt");

        var servicio = servicio();
        var respuesta = servicio.login(new LoginRequest(" ANA@GMAIL.COM ", "ClaveSegura123"));

        assertEquals("Bearer", respuesta.type());
        assertEquals(List.of("COMPRADOR"), respuesta.roles());
        assertEquals(reloj.instant().plusSeconds(1800), respuesta.expireAt());
        assertEquals("jwt", respuesta.token());
        var manager = (org.springframework.security.authentication.AuthenticationManager) ReflectionTestUtils.getField(servicio, "authenticationManager");
        verify(manager).authenticate(argThat(credenciales -> credenciales.getName().equals("ana@gmail.com") && credenciales.getCredentials().equals("ClaveSegura123")));
        verify(usuarios).findByCorreo("ana@gmail.com");
        verify(tokens).generar("c1", "ana@gmail.com", List.of("COMPRADOR"), reloj.instant(), reloj.instant().plusSeconds(1800));
    }

    @Test
    public void credencialesIncorrectasONoRegistradasNuncaEmitenToken() {

        var usuario = new CompradorEntity();
        usuario.setId("c1");
        usuario.setCorreo("ana@gmail.com");
        usuario.setContrasena(encoder.encode("ClaveSegura123"));
        when(usuarios.findByCorreo("ana@gmail.com")).thenReturn(Optional.of(usuario));
        var servicio = servicio();

        var accion1Error = catchThrowable(() -> servicio.login(new LoginRequest("ana@gmail.com", "Incorrecta123")));
        var accion2Error = catchThrowable(() -> servicio.login(new LoginRequest("ausente@gmail.com", "Incorrecta123")));

        assertInstanceOf(BadCredentialsException.class, accion1Error);
        assertInstanceOf(BadCredentialsException.class, accion2Error);
        verifyNoInteractions(tokens);
    }

    @Test
    public void correoDelTokenProvieneDelPrincipalAutenticado() {
        var manager = mock(AuthenticationManager.class);
        var usuario = new CompradorEntity();
        usuario.setId("c1");
        usuario.setCorreo("ana@gmail.com");
        var detalles = new CustomUserDetails(usuario);
        when(manager.authenticate(any())).thenReturn(authenticated(detalles, null, detalles.getAuthorities()));
        when(tokens.generar("c1", "ana@gmail.com", List.of("COMPRADOR"), reloj.instant(), reloj.instant().plusSeconds(1800))).thenReturn("jwt");
        var servicio = new AutenticacionService(manager, tokens, reloj);
        ReflectionTestUtils.setField(servicio, "duracionConfigurada", Duration.ofMinutes(30));
        var request = new LoginRequest("alias@gmail.com", "ClaveSegura123");

        var respuesta = servicio.login(request);

        verify(manager).authenticate(argThat(credenciales -> credenciales.getName().equals("alias@gmail.com")));
        verify(tokens).generar("c1", "ana@gmail.com", List.of("COMPRADOR"), reloj.instant(), reloj.instant().plusSeconds(1800));
        assertEquals("jwt", respuesta.token());
        assertEquals("Bearer", respuesta.type());
        assertEquals(reloj.instant().plusSeconds(1800), respuesta.expireAt());
        assertEquals(List.of("COMPRADOR"), respuesta.roles());
    }
}
