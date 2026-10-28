package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties="security.cors.allowed-origins=https://cliente.pruebas.example")
@AutoConfigureMockMvc
public class SecurityConfigTest {
    @Autowired
    MockMvc mvc;
    @Test
    public void corsUsaUnicamenteElOrigenConfigurado() throws Exception {

        var solicitud1 = options("/api/productos").header("Origin","https://no-autorizado.example")
                .header("Access-Control-Request-Method","GET");
        var solicitud2 = options("/api/productos").header("Origin","https://cliente.pruebas.example")
                .header("Access-Control-Request-Method","GET").header("Access-Control-Request-Headers","Authorization");

        var respuesta1 = mvc.perform(solicitud2);
        var respuesta2 = mvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","https://cliente.pruebas.example"));
        respuesta2.andExpect(status().isForbidden());
    }
    @Test
    public void rutaNoDeclaradaSigueDenegadaAunqueExistaJwt() throws Exception {
        var solicitud1 = get("/api/ruta-no-autorizada").with(jwt().jwt(j->j.subject("c1").claim("rol","COMPRADOR"))
                .authorities(new SimpleGrantedAuthority("ROLE_COMPRADOR")));

        var respuesta1 = mvc.perform(solicitud1);

        respuesta1.andExpect(status().isForbidden());
    }
    @Test
    public void consolaH2NoEstaAbiertaEnPerfilNormal() throws Exception {

        var solicitud1 = get("/h2-console/");

        var respuesta1 = mvc.perform(solicitud1);

        respuesta1.andExpect(status().isUnauthorized());
    }
}

