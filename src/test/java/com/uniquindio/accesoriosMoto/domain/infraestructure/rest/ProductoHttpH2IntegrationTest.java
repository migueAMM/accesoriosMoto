package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.jayway.jsonpath.JsonPath;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.ProductoRepositoryJpa;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@org.springframework.test.context.ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.h2.console.enabled=true")
class ProductoHttpH2IntegrationTest {
    @Value("${local.server.port}") int puerto;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    ApplicationContext contexto;
    @Autowired com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository usuarios;
    @Autowired com.uniquindio.accesoriosMoto.application.port.CodificadorContrasena contrasenas;
    private String token;
    @org.junit.jupiter.api.BeforeEach
    void autenticarAdministradorReal() throws Exception {
        if (usuarios.obtenerPorCorreo("admin-http@ejemplo.com").isEmpty()) {
            usuarios.guardar(new com.uniquindio.accesoriosMoto.domain.entity.Administrador("admin-http", "Admin HTTP", "admin-http@ejemplo.com", contrasenas.codificar("ClaveSegura123")));
        }
        var login = enviar("POST", "/api/auth/login", "{\"correo\":\"admin-http@ejemplo.com\",\"contrasena\":\"ClaveSegura123\"}");
        assertThat(login.statusCode()).isEqualTo(200);
        token = JsonPath.read(login.body(), "$.token");
    }
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private HttpResponse<String> enviar(String metodo, String ruta, String cuerpo) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .timeout(Duration.ofSeconds(20)).header("Content-Type", "application/json")
                .method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(cuerpo))
                ;
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void registraPublicaYConsultaPorHttpConDatosConfirmadosEnH2() throws Exception {
        try (var conexion = jdbc.getDataSource().getConnection()) {
            assertThat(conexion.getMetaData().getURL()).startsWith("jdbc:h2:mem:");
        }
        assertThat(contexto.getBeansOfType(ProductoRepository.class)).hasSize(1);
        assertThat(AopUtils.getTargetClass(contexto.getBean(ProductoRepository.class))).isEqualTo(ProductoRepositoryJpa.class);
        assertThat(contexto.getBeansOfType(PedidoRepository.class)).hasSize(1);
        assertThat(contexto.getBeansOfType(CompradorRepository.class)).hasSize(1);
        assertThat(contexto.getBeansOfType(ComentarioRepository.class)).hasSize(1);

        HttpResponse<String> creado = enviar("POST", "/api/productos/cascos", """
                {"marcaId":"http-marca","marcaNombre":"Marca HTTP","precioMonto":500000,"precioMoneda":"COP",
                 "color":"Negro","modelo":"K3","talla":"M","proteccion":"NIVEL_1","certificacionSeguridad":"DOT"}
                """);
        assertThat(creado.statusCode()).isEqualTo(201);
        String id = JsonPath.read(creado.body(), "$.id");
        assertThat(creado.headers().firstValue("Location")).hasValue("http://localhost:" + puerto + "/api/productos/" + id);
        assertThat(jdbc.queryForObject("select tipo_producto from productos where id=?", String.class, id)).isEqualTo("CASCO");
        assertThat(jdbc.queryForObject("select estado_publicacion from productos where id=?", String.class, id)).isEqualTo("NO_PUBLICADO");

        HttpResponse<String> publicado = enviar("PUT", "/api/productos/" + id + "/publicar", null);
        assertThat(publicado.statusCode()).isEqualTo(200);
        assertThat(jdbc.queryForObject("select estado_publicacion from productos where id=?", String.class, id)).isEqualTo("PUBLICADO");

        HttpResponse<String> catalogo = enviar("GET", "/api/productos?marca=Marca%20HTTP", null);
        assertThat(catalogo.statusCode()).isEqualTo(200);
        assertThat((java.util.List<String>) JsonPath.<java.util.List<String>>read(catalogo.body(), "$[*].id")).containsExactly(id);
        assertThat(catalogo.body()).doesNotContain("contrasena");
    }
    @Test
    void ofreceLaConsolaH2EnElServidor() throws Exception {
        HttpResponse<String> respuesta = enviar("GET", "/h2-console/", null);
        assertThat(respuesta.statusCode()).isBetween(200, 399);
    }
}
