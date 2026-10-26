package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.jayway.jsonpath.JsonPath;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.ProductoRepositoryJpa;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductoHttpH2IntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManager entityManager;
    @Autowired private ApplicationContext contexto;

    @Test
    void registraPublicaYConsultaPorHttpConDatosConfirmadosEnH2() throws Exception {

        String cuerpo = """
                {"marcaId":"http-marca","marcaNombre":"Marca HTTP","precioMonto":500000,"precioMoneda":"COP",
                 "color":"Negro","modelo":"K3","talla":"M","proteccion":"NIVEL_1","certificacionSeguridad":"DOT"}
                """;
        var administrador = jwt().jwt(j -> j.subject("admin-integracion").claim("rol", "ADMINISTRADOR"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));

        var creado = mockMvc.perform(post("/api/productos/cascos").with(administrador)
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andReturn();
        entityManager.flush();
        entityManager.clear();

        assertThat(creado.getResponse().getStatus()).isEqualTo(201);
        String id = JsonPath.read(creado.getResponse().getContentAsString(), "$.id");
        assertThat(creado.getResponse().getHeader("Location")).isEqualTo("http://localhost/api/productos/" + id);
        try (var conexion = jdbc.getDataSource().getConnection()) {
            assertThat(conexion.getMetaData().getURL()).startsWith("jdbc:h2:mem:");
        }
        assertThat(contexto.getBeansOfType(ProductoRepository.class)).hasSize(1);
        assertThat(AopUtils.getTargetClass(contexto.getBean(ProductoRepository.class))).isEqualTo(ProductoRepositoryJpa.class);
        assertThat(contexto.getBeansOfType(PedidoRepository.class)).hasSize(1);
        assertThat(contexto.getBeansOfType(CompradorRepository.class)).hasSize(1);
        assertThat(contexto.getBeansOfType(ComentarioRepository.class)).hasSize(1);
        assertThat(jdbc.queryForObject("select tipo_producto from productos where id=?", String.class, id)).isEqualTo("CASCO");
        assertThat(jdbc.queryForObject("select estado_publicacion from productos where id=?", String.class, id)).isEqualTo("NO_PUBLICADO");

        var publicado = mockMvc.perform(put("/api/productos/" + id + "/publicar").with(administrador));
        entityManager.flush();
        entityManager.clear();

        publicado.andExpect(status().isOk()).andExpect(jsonPath("$.estadoPublicacion").value("PUBLICADO"));
        assertThat(jdbc.queryForObject("select estado_publicacion from productos where id=?", String.class, id)).isEqualTo("PUBLICADO");

        var catalogo = mockMvc.perform(get("/api/productos").param("marca", "Marca HTTP"));

        catalogo.andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].precioMonto").value(500000))
                .andExpect(jsonPath("$[0].precioMoneda").value("COP"))
                .andExpect(jsonPath("$[0].estadoPublicacion").value("PUBLICADO"));
    }
}
