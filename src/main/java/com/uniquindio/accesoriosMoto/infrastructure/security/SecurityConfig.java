package com.uniquindio.accesoriosMoto.infrastructure.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Import(JwtConfig.class)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    @Value("${security.cors.allowed-origins:}")
    private String origenesPermitidos;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a -> a
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registro", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/{id}", "/api/productos/mas-vendidos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").hasRole("COMPRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/{id}/completar").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/baules", "/api/productos/cascos",
                                "/api/productos/chaquetas", "/api/productos/guantes", "/api/productos/impermeables",
                                "/api/productos/intercomunicadores", "/api/productos/soportes-celular", "/api/productos/visores").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/{id}/publicar", "/api/productos/{id}/despublicar",
                                "/api/productos/{id}/compatibilidades").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/{id}/compatibilidades").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos", "/api/comentarios").hasRole("COMPRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/{id}/reembolso").hasRole("COMPRADOR")
                        .anyRequest().denyAll())
                .exceptionHandling(e -> e.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                .oauth2ResourceServer(o -> o.jwt(j -> j.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())).build();
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(origenesPermitidos.split(",")).map(String::strip).filter(s -> !s.isEmpty()).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean @Order(1) @Profile("dev")
    public SecurityFilterChain consolaH2(HttpSecurity http) throws Exception {
        return http.securityMatcher("/h2-console/**")
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(f -> f.sameOrigin())).build();
    }
}
