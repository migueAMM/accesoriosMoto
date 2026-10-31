package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

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
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.time.temporal.ChronoUnit.SECONDS;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AutenticacionHttpIntegrationTest {
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
    PasswordEncoder contrasenas;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    JwtDecoder decoder;
    @Autowired
    ApplicationContext contexto;

    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> enviar(String metodo, String ruta, String cuerpo, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta)).timeout(Duration.ofSeconds(20)).header("Content-Type", "application/json").method(metodo, cuerpo == null ? noBody() : ofString(cuerpo));
        if (token != null) request.header("Authorization", "Bearer " + token);
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    private String correo() {
        return UUID.randomUUID() + "@ejemplo.com";
    }

    private String campo(HttpResponse<String> respuesta, String nombre) {
        return JsonPath.read(respuesta.body(), "$." + nombre);
    }

    private HttpResponse<String> registrar(String correo) throws Exception {
        return enviar("POST", "/api/auth/registro", "{\"nombre\":\"Ana\",\"correo\":\"" + correo + "\",\"contrasena\":\"ClaveSegura123\"}", null);
    }

    private HttpResponse<String> login(String correo, String clave) throws Exception {
        return enviar("POST", "/api/auth/login", "{\"correo\":\"" + correo + "\",\"contrasena\":\"" + clave + "\"}", null);
    }

    private Producto producto() {
        Producto producto = Casco.crear(UUID.randomUUID().toString(), new Marca("m", "Marca"), "K3", "Negro", Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT");
        productos.guardar(producto);
        return producto;
    }

    @Test
    public void registraNormalizaGuardaBCryptYPermiteLoginConJwtSinExponerCredenciales() throws Exception {
        String correo = correo();

        var registro = registrar("  " + correo.toUpperCase(Locale.ROOT) + "  ");

        var resultado2 = campo(registro, "correo");
        var resultado3 = campo(registro, "rol");
        String id = campo(registro, "id");
        var resultado4 = compradores.obtenerPorId(id);
        String hash = jdbc.queryForObject("select contrasena from usuarios where id=?", String.class, id);

        var login = login(" " + correo.toUpperCase(Locale.ROOT) + " ", "ClaveSegura123");

        var resultado9 = campo(login, "type");
        var jwt = decoder.decode(campo(login, "token"));

        var resultado11 = java.time.Instant.parse(campo(login, "expireAt")).truncatedTo(SECONDS);
        var resultado12 = campo(login, "roles[0]");

        var resultado16 = contexto.containsBean("usuariosIniciales");

        assertThat(registro.statusCode()).isEqualTo(201);
        assertThat(resultado2).isEqualTo(correo);
        assertThat(resultado3).isEqualTo("COMPRADOR");
        assertThat(resultado4).isPresent();
        assertThat(hash).startsWith("$2").isNotEqualTo("ClaveSegura123");
        assertThat(contrasenas.matches("ClaveSegura123", hash)).isTrue();
        assertThat(registro.body()).doesNotContain("contrasena", hash);
        assertThat(login.statusCode()).isEqualTo(200);
        assertThat(resultado9).isEqualTo("Bearer");
        assertThat(jwt.getSubject()).isEqualTo(id);
        assertThat(jwt.getClaimAsString("correo")).isEqualTo(correo);
        assertThat(resultado11).isEqualTo(jwt.getExpiresAt());
        assertThat(resultado12).isEqualTo("COMPRADOR");
        assertThat(jwt.getClaimAsString("rol")).isEqualTo("COMPRADOR");
        assertThat(jwt.getClaims()).doesNotContainKeys("contrasena", "password", "hash");
        assertThat(login.body()).doesNotContain(hash, "ClaveSegura123");
        assertThat(resultado16).isFalse();
    }

    @Test
    public void rechazaCorreoDuplicadoNormalizado() throws Exception {
        String correo = correo();

        var resultado1 = registrar(correo).statusCode();
        var resultado2 = registrar(" " + correo.toUpperCase(Locale.ROOT) + " ").statusCode();
        var resultado3 = jdbc.queryForObject("select count(*) from usuarios where correo=?", Integer.class, correo);

        assertThat(resultado1).isEqualTo(201);
        assertThat(resultado2).isEqualTo(409);
        assertThat(resultado3).isEqualTo(1);
    }

    @Test
    public void registroPublicoNuncaConcedeAdministradorAunqueLoSolicite() throws Exception {
        String correo = correo();

        var respuesta = enviar("POST", "/api/auth/registro", "{\"nombre\":\"Ana\",\"correo\":\"" + correo + "\",\"contrasena\":\"ClaveSegura123\",\"rol\":\"ADMINISTRADOR\"}", null);

        var resultado2 = campo(respuesta, "rol");
        var resultado3 = usuarios.obtenerPorCorreo(correo).orElseThrow();

        assertThat(respuesta.statusCode()).isEqualTo(201);
        assertThat(resultado2).isEqualTo("COMPRADOR");
        assertThat(resultado3).isInstanceOf(Comprador.class);
    }

    @Test
    public void credencialesErroneasDevuelvenElMismo401() throws Exception {
        String correo = correo();
        registrar(correo);

        var inexistente = login(correo(), "ClaveSegura123");
        var incorrecta = login(correo, "OtraClave123");

        assertThat(inexistente.statusCode()).isEqualTo(401);
        assertThat(incorrecta.statusCode()).isEqualTo(401);
        assertThat(inexistente.body()).isEqualTo(incorrecta.body()).doesNotContain(correo);
    }

    @Test
    public void publicarDevuelve401Luego403YFinalmente200ConUsuariosYLoginReales() throws Exception {
        Producto producto = producto();
        String ruta = "/api/productos/" + producto.getId() + "/publicar";

        var resultado1 = enviar("PUT", ruta, null, null).statusCode();
        var resultado2 = productos.obtenerPorId(producto.getId()).orElseThrow().estaPublicado();
        String correoComprador = correo();
        registrar(correoComprador);
        String tokenComprador = campo(login(correoComprador, "ClaveSegura123"), "token");
        var resultado3 = enviar("PUT", ruta, null, tokenComprador).statusCode();
        var resultado4 = productos.obtenerPorId(producto.getId()).orElseThrow().estaPublicado();
        String correoAdmin = correo();
        usuarios.guardar(new Administrador(UUID.randomUUID().toString(), "Admin", correoAdmin, contrasenas.encode("ClaveAdmin123")));
        var loginAdmin = login(correoAdmin, "ClaveAdmin123");

        var resultado6 = campo(loginAdmin, "roles[0]");
        String tokenAdmin = campo(loginAdmin, "token");
        var resultado7 = enviar("PUT", ruta, null, tokenAdmin).statusCode();
        var resultado8 = jdbc.queryForObject("select estado_publicacion from productos where id=?", String.class, producto.getId());

        assertThat(resultado1).isEqualTo(401);
        assertThat(resultado2).isFalse();
        assertThat(resultado3).isEqualTo(403);
        assertThat(resultado4).isFalse();
        assertThat(loginAdmin.statusCode()).isEqualTo(200);
        assertThat(resultado6).isEqualTo("ADMINISTRADOR");
        assertThat(resultado7).isEqualTo(200);
        assertThat(resultado8).isEqualTo("PUBLICADO");
    }

    @Test
    public void impideSuplantacionEnPedidosYComentarios() throws Exception {
        String correo = correo();
        String id = campo(registrar(correo), "id");
        String otroId = campo(registrar(correo()), "id");
        String token = campo(login(correo, "ClaveSegura123"), "token");
        Producto producto = producto();
        String items = "\"items\":[{\"productoId\":\"" + producto.getId() + "\",\"cantidad\":2}]";

        var resultado1 = enviar("POST", "/api/pedidos", "{\"compradorId\":\"" + otroId + "\"," + items + "}", token).statusCode();
        var propio = enviar("POST", "/api/pedidos", "{\"compradorId\":\"" + id + "\"," + items + "}", token);

        var resultado3 = pedidos.obtenerPorId(campo(propio, "id")).orElseThrow().getComprador().getId();
        var resultado4 = enviar("POST", "/api/comentarios", "{\"compradorId\":\"" + otroId + "\",\"pedidoId\":\"" + campo(propio, "id") + "\",\"productoId\":\"" + producto.getId() + "\",\"calificacion\":5,\"texto\":\"Bueno\"}", token).statusCode();

        assertThat(resultado1).isEqualTo(403);
        assertThat(propio.statusCode()).isEqualTo(201);
        assertThat(resultado3).isEqualTo(id);
        assertThat(resultado4).isEqualTo(403);
    }

    @Test
    public void reembolsoAjenoSeRechazaYElPropietarioPuedeReembolsar() throws Exception {
        String correoPropio = correo();
        String propioId = campo(registrar(correoPropio), "id");
        String correoAjeno = correo();
        String ajenoId = campo(registrar(correoAjeno), "id");
        Comprador propietario = compradores.obtenerPorId(propioId).orElseThrow();
        Producto producto = producto();
        Pedido pedido = Pedido.registrar(UUID.randomUUID().toString(), propietario, List.of(ItemPedido.crear(UUID.randomUUID().toString(), producto, 1)), LocalDateTime.now());
        pedido.completar();
        pedidos.registrar(pedido);
        String ruta = "/api/pedidos/" + pedido.getId() + "/reembolso";
        String tokenAjeno = campo(login(correoAjeno, "ClaveSegura123"), "token");

        var resultado1 = enviar("PUT", ruta, "{\"motivo\":\"Defectuoso\"}", tokenAjeno).statusCode();
        var resultado2 = pedidos.obtenerPorId(pedido.getId()).orElseThrow().getEstadoPedido();
        String tokenPropio = campo(login(correoPropio, "ClaveSegura123"), "token");
        String comentarioAjeno = "{\"compradorId\":\"" + ajenoId + "\",\"pedidoId\":\"" + pedido.getId() + "\",\"productoId\":\"" + producto.getId() + "\",\"calificacion\":5,\"texto\":\"Bueno\"}";
        var resultado3 = enviar("POST", "/api/comentarios", comentarioAjeno, tokenAjeno).statusCode();
        String comentarioPropio = comentarioAjeno.replace(ajenoId, propioId);
        var resultado4 = enviar("POST", "/api/comentarios", comentarioPropio, tokenPropio).statusCode();
        var resultado5 = enviar("PUT", ruta, "{\"motivo\":\"Defectuoso\"}", tokenPropio).statusCode();
        var resultado6 = pedidos.obtenerPorId(pedido.getId()).orElseThrow().getEstadoPedido();

        assertThat(resultado1).isEqualTo(403);
        assertThat(resultado2).isEqualTo(EstadoPedido.COMPLETADA);
        assertThat(resultado3).isEqualTo(403);
        assertThat(resultado4).isEqualTo(201);
        assertThat(resultado5).isEqualTo(200);
        assertThat(resultado6).isEqualTo(EstadoPedido.REEMBOLSADA);
    }

    @Test
    public void tokenManipuladoDevuelve401YCatalogoEsPublico() throws Exception {

        var resultado1 = enviar("GET", "/api/productos", null, null).statusCode();
        var resultado2 = enviar("PUT", "/api/productos/cualquier/publicar", null, "no.es.valido").statusCode();
        var resultado3 = enviar("GET", "/h2-console/", null, null).statusCode();
        var resultado4 = enviar("POST", "/api/auth/administradores", "{}", null).statusCode();

        assertThat(resultado1).isEqualTo(200);
        assertThat(resultado2).isEqualTo(401);
        assertThat(resultado3).isEqualTo(401);
        assertThat(resultado4).isEqualTo(401);
    }
}