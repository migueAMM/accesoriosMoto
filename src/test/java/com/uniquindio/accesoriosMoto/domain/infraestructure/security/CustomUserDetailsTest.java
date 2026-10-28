package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.AdministradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.RolSeguridad;
import com.uniquindio.accesoriosMoto.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomUserDetailsTest {
    @Test
    public void compradorExponeIdCorreoHashYAuthorityCorrespondiente() {

        var usuario=new CompradorEntity();
        usuario.setId("c1");
        usuario.setCorreo("ana@correo.com");
        usuario.setContrasena("hash");

        var detalles=new CustomUserDetails(usuario);

        assertEquals("c1", detalles.usuario().getId());
        assertEquals("ana@correo.com", detalles.getUsername());
        assertEquals("hash", detalles.getPassword());
        assertEquals(RolSeguridad.COMPRADOR, detalles.usuario().getRol());
        assertEquals(List.of("ROLE_COMPRADOR"), detalles.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    @Test
    public void administradorDerivaSuRolDelTipoSinCampoMutable() {

        var usuario=new AdministradorEntity();
        usuario.setId("a1");

        var detalles=new CustomUserDetails(usuario);

        assertEquals(RolSeguridad.ADMINISTRADOR, detalles.usuario().getRol());
        assertEquals(List.of("ROLE_ADMINISTRADOR"), detalles.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}

