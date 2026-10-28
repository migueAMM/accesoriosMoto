package com.uniquindio.accesoriosMoto.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String ESQUEMA = "bearer-jwt";
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("API accesoriosMoto").version("1.0")
                        .description("Catálogo de accesorios, pedidos y comentarios. Las operaciones protegidas requieren un JWT y el rol correspondiente."))
                .components(new Components().addSecuritySchemes(ESQUEMA, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
