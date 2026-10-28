package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Locale;

@Configuration
public class UserConfig {
    @Bean
    public UserDetailsService userDetailsService(UsuarioJpaDataRepository repository) {
        return correo -> repository.findByCorreo(correo.strip().toLowerCase(Locale.ROOT))
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe"));
    }
}
