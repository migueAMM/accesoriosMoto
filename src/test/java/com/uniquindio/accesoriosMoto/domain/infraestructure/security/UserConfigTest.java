package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.security.CustomUserDetails;
import com.uniquindio.accesoriosMoto.infrastructure.security.UserConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class UserConfigTest {
    @Test
    public void normalizaCorreoYRecuperaUsuarioReal() {

        var repo=mock(UsuarioJpaDataRepository.class);
        var usuario=new CompradorEntity();
        usuario.setId("c1");
        when(repo.findByCorreo("ana@correo.com")).thenReturn(Optional.of(usuario));

        var detalles=(CustomUserDetails)new UserConfig().userDetailsService(repo).loadUserByUsername(" ANA@CORREO.COM ");

        assertSame(usuario, detalles.usuario());
        verify(repo).findByCorreo("ana@correo.com");
    }

    @Test
    public void noInventaUsuarioCuandoNoExiste() {

        var repo=mock(UsuarioJpaDataRepository.class);
        when(repo.findByCorreo("ausente@correo.com")).thenReturn(Optional.empty());

        var accion1Error = catchThrowable(()->new UserConfig().userDetailsService(repo).loadUserByUsername("ausente@correo.com"));

        assertInstanceOf(UsernameNotFoundException.class, accion1Error);
    }
}

