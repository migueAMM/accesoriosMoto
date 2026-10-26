package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadProductoEndpointTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ProductoRepository productos;
    @Autowired private EntityManager entityManager;

    private void prepararProducto() {
        productos.guardar(Casco.crear("casco-seguridad", new Marca("m", "AGV"), "K3", "Negro",
                Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT"));
        entityManager.flush();
        entityManager.clear();
    }

    private EstadoPublicacion estadoPersistido() {
        entityManager.flush();
        entityManager.clear();
        return productos.obtenerPorId("casco-seguridad").orElseThrow().getEstadoPublicacion();
    }

    @Test
    void sinTokenDeberiaResponder401() throws Exception {
        // Arrange
        prepararProducto();

        // Act
        var respuesta = mockMvc.perform(put("/api/productos/casco-seguridad/publicar"));

        // Assert
        respuesta.andExpect(status().isUnauthorized());
        assertEquals(EstadoPublicacion.NO_PUBLICADO, estadoPersistido());
    }

    @Test
    void conRolIncorrectoDeberiaResponder403() throws Exception {
        // Arrange
        prepararProducto();

        // Act
        var respuesta = mockMvc.perform(put("/api/productos/casco-seguridad/publicar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_COMPRADOR"))));

        // Assert
        respuesta.andExpect(status().isForbidden());
        assertEquals(EstadoPublicacion.NO_PUBLICADO, estadoPersistido());
    }

    @Test
    void conRolCorrectoDeberiaResponder200() throws Exception {
        // Arrange
        prepararProducto();

        // Act
        var respuesta = mockMvc.perform(put("/api/productos/casco-seguridad/publicar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));

        // Assert
        respuesta.andExpect(status().isOk());
        assertEquals(EstadoPublicacion.PUBLICADO, estadoPersistido());
    }
}
