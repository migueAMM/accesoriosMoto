package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.jayway.jsonpath.JsonPath;
import com.uniquindio.accesoriosMoto.application.port.CodificadorContrasena;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutenticacionHttpIntegrationTest {
    @Value("${local.server.port}") int puerto;
    @Autowired
    UsuarioRepository usuarios;
    @Autowired
    CompradorRepository compradores;
    @Autowired
    ProductoRepository productos;
    @Autowired
    PedidoRepository pedidos;
    @Autowired
    CodificadorContrasena contrasenas;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired JwtDecoder decoder;
    @Autowired
    ApplicationContext contexto;
    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> enviar(String metodo, String ruta, String cuerpo, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .timeout(Duration.ofSeconds(20)).header("Content-Type", "application/json")
                .method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(cuerpo));
        if (token != null) request.header("Authorization", "Bearer " + token);
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    private String correo() { return UUID.randomUUID() + "@ejemplo.com"; }
    private String campo(HttpResponse<String> respuesta, String nombre) { return JsonPath.read(respuesta.body(), "$." + nombre); }
    private HttpResponse<String> registrar(String correo) throws Exception {
        return enviar("POST", "/api/auth/registro", "{\"nombre\":\"Ana\",\"correo\":\"" + correo + "\",\"contrasena\":\"ClaveSegura123\"}", null);
    }
    private HttpResponse<String> login(String correo, String clave) throws Exception {
        return enviar("POST", "/api/auth/login", "{\"correo\":\"" + correo + "\",\"contrasena\":\"" + clave + "\"}", null);
    }
    private Producto producto() {
        Producto producto = Casco.crear(UUID.randomUUID().toString(), new Marca("m", "Marca"),
                "K3", "Negro", Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT");
        productos.guardar(producto);
        return producto;
    }

    @Test
    void registraNormalizaGuardaBCryptYPermiteLoginConJwtSinExponerCredenciales() throws Exception {
        String correo = correo();
        var registro = registrar("  " + correo.toUpperCase(Locale.ROOT) + "  ");
        assertThat(registro.statusCode()).isEqualTo(201);
        assertThat(campo(registro, "correo")).isEqualTo(correo);
        assertThat(campo(registro, "rol")).isEqualTo("COMPRADOR");
        String id = campo(registro, "id");
        assertThat(compradores.obtenerPorId(id)).isPresent();
        String hash = jdbc.queryForObject("select contrasena from usuarios where id=?", String.class, id);
        assertThat(hash).startsWith("$2").isNotEqualTo("ClaveSegura123");
        assertThat(contrasenas.coincide("ClaveSegura123", hash)).isTrue();
        assertThat(registro.body()).doesNotContain("contrasena", hash);
        var login = login(" " + correo.toUpperCase(Locale.ROOT) + " ", "ClaveSegura123");
        assertThat(login.statusCode()).isEqualTo(200);
        assertThat(campo(login, "tipo")).isEqualTo("Bearer");
        var jwt = decoder.decode(campo(login, "token"));
        assertThat(jwt.getSubject()).isEqualTo(id);
        assertThat(jwt.getClaimAsString("rol")).isEqualTo("COMPRADOR");
        assertThat(jwt.getClaims()).doesNotContainKeys("contrasena", "password", "hash");
        assertThat(login.body()).doesNotContain(hash, "ClaveSegura123");
        assertThat(contexto.containsBean("administradorInicial")).isFalse();
    }

    @Test
    void rechazaCorreoDuplicadoNormalizado() throws Exception {
        String correo = correo();
        assertThat(registrar(correo).statusCode()).isEqualTo(201);
        assertThat(registrar(" " + correo.toUpperCase(Locale.ROOT) + " ").statusCode()).isEqualTo(409);
        assertThat(jdbc.queryForObject("select count(*) from usuarios where correo=?", Integer.class, correo)).isEqualTo(1);
    }

    @Test
    void registroPublicoNuncaConcedeAdministradorAunqueLoSolicite() throws Exception {
        String correo = correo();
        var respuesta = enviar("POST", "/api/auth/registro", "{\"nombre\":\"Ana\",\"correo\":\"" + correo
                + "\",\"contrasena\":\"ClaveSegura123\",\"rol\":\"ADMINISTRADOR\"}", null);
        assertThat(respuesta.statusCode()).isEqualTo(201);
        assertThat(campo(respuesta, "rol")).isEqualTo("COMPRADOR");
        assertThat(usuarios.obtenerPorCorreo(correo).orElseThrow()).isInstanceOf(Comprador.class);
    }

    @Test
    void credencialesErroneasDevuelvenElMismo401() throws Exception {
        String correo = correo();
        registrar(correo);
        var inexistente = login(correo(), "ClaveSegura123");
        var incorrecta = login(correo, "OtraClave123");
        assertThat(inexistente.statusCode()).isEqualTo(401);
        assertThat(incorrecta.statusCode()).isEqualTo(401);
        assertThat(inexistente.body()).isEqualTo(incorrecta.body()).doesNotContain(correo);
    }

    @Test
    void publicarDevuelve401Luego403YFinalmente200ConUsuariosYLoginReales() throws Exception {
        Producto producto = producto();
        String ruta = "/api/productos/" + producto.getId() + "/publicar";
        assertThat(enviar("PUT", ruta, null, null).statusCode()).isEqualTo(401);
        assertThat(productos.obtenerPorId(producto.getId()).orElseThrow().estaPublicado()).isFalse();

        String correoComprador = correo();
        registrar(correoComprador);
        String tokenComprador = campo(login(correoComprador, "ClaveSegura123"), "token");
        assertThat(enviar("PUT", ruta, null, tokenComprador).statusCode()).isEqualTo(403);
        assertThat(productos.obtenerPorId(producto.getId()).orElseThrow().estaPublicado()).isFalse();

        String correoAdmin = correo();
        usuarios.guardar(new Administrador(UUID.randomUUID().toString(), "Admin", correoAdmin, contrasenas.codificar("ClaveAdmin123")));
        var loginAdmin = login(correoAdmin, "ClaveAdmin123");
        assertThat(loginAdmin.statusCode()).isEqualTo(200);
        assertThat(campo(loginAdmin, "rol")).isEqualTo("ADMINISTRADOR");
        String tokenAdmin = campo(loginAdmin, "token");
        assertThat(enviar("PUT", ruta, null, tokenAdmin).statusCode()).isEqualTo(200);
        assertThat(jdbc.queryForObject("select estado_publicacion from productos where id=?", String.class, producto.getId())).isEqualTo("PUBLICADO");
    }

    @Test
    void impideSuplantacionEnPedidosYComentarios() throws Exception {
        String correo = correo();
        String id = campo(registrar(correo), "id");
        String otroId = campo(registrar(correo()), "id");
        String token = campo(login(correo, "ClaveSegura123"), "token");
        Producto producto = producto();
        String items = "\"items\":[{\"productoId\":\"" + producto.getId() + "\",\"cantidad\":2}]";
        assertThat(enviar("POST", "/api/pedidos", "{\"compradorId\":\"" + otroId + "\"," + items + "}", token).statusCode()).isEqualTo(403);
        var propio = enviar("POST", "/api/pedidos", "{\"compradorId\":\"" + id + "\"," + items + "}", token);
        assertThat(propio.statusCode()).isEqualTo(201);
        assertThat(pedidos.obtenerPorId(campo(propio, "id")).orElseThrow().getComprador().getId()).isEqualTo(id);
        assertThat(enviar("POST", "/api/comentarios", "{\"compradorId\":\"" + otroId
                + "\",\"pedidoId\":\"" + campo(propio, "id") + "\",\"productoId\":\"" + producto.getId()
                + "\",\"calificacion\":5,\"texto\":\"Bueno\"}", token).statusCode()).isEqualTo(403);
    }

    @Test
    void reembolsoAjenoSeRechazaYElPropietarioPuedeReembolsar() throws Exception {
        String correoPropio = correo();
        String propioId = campo(registrar(correoPropio), "id");
        String correoAjeno = correo();
        String ajenoId = campo(registrar(correoAjeno), "id");
        Comprador propietario = compradores.obtenerPorId(propioId).orElseThrow();
        Producto producto = producto();
        Pedido pedido = Pedido.registrar(UUID.randomUUID().toString(), propietario,
                List.of(ItemPedido.crear(UUID.randomUUID().toString(), producto, 1)), LocalDateTime.now());
        pedido.completar();
        pedidos.registrar(pedido);
        String ruta = "/api/pedidos/" + pedido.getId() + "/reembolso";
        String tokenAjeno = campo(login(correoAjeno, "ClaveSegura123"), "token");
        assertThat(enviar("PUT", ruta, "{\"motivo\":\"Defectuoso\"}", tokenAjeno).statusCode()).isEqualTo(403);
        assertThat(pedidos.obtenerPorId(pedido.getId()).orElseThrow().getEstadoPedido()).isEqualTo(EstadoPedido.COMPLETADA);
        String tokenPropio = campo(login(correoPropio, "ClaveSegura123"), "token");
        String comentarioAjeno = "{\"compradorId\":\"" + ajenoId + "\",\"pedidoId\":\"" + pedido.getId()
                + "\",\"productoId\":\"" + producto.getId() + "\",\"calificacion\":5,\"texto\":\"Bueno\"}";
        assertThat(enviar("POST", "/api/comentarios", comentarioAjeno, tokenAjeno).statusCode()).isEqualTo(403);
        String comentarioPropio = comentarioAjeno.replace(ajenoId, propioId);
        assertThat(enviar("POST", "/api/comentarios", comentarioPropio, tokenPropio).statusCode()).isEqualTo(201);
        assertThat(enviar("PUT", ruta, "{\"motivo\":\"Defectuoso\"}", tokenPropio).statusCode()).isEqualTo(200);
        assertThat(pedidos.obtenerPorId(pedido.getId()).orElseThrow().getEstadoPedido()).isEqualTo(EstadoPedido.REEMBOLSADA);
    }

    @Test
    void tokenManipuladoDevuelve401YCatalogoEsPublico() throws Exception {
        assertThat(enviar("GET", "/api/productos", null, null).statusCode()).isEqualTo(200);
        assertThat(enviar("PUT", "/api/productos/cualquier/publicar", null, "no.es.valido").statusCode()).isEqualTo(401);
        assertThat(enviar("GET", "/h2-console/", null, null).statusCode()).isEqualTo(401);
        assertThat(enviar("POST", "/api/auth/administradores", "{}", null).statusCode()).isEqualTo(401);
    }
}
