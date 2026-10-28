package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public record CustomUserDetails(UsuarioEntity usuario) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override public String getPassword() {
        return usuario.getContrasena();
    }

    @Override public String getUsername() {
        return usuario.getCorreo();
    }
}
