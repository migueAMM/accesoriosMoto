package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.application.usecase.*;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.Pagina;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.rest.ProductoController;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ProductoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import(ProductoMapper.class)
@AutoConfigureMockMvc(addFilters = false)

public class ProductoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RegistrarBaulUseCase registrarBaulUseCase;
    @MockitoBean
    private RegistrarCascoUseCase registrarCascoUseCase;
    @MockitoBean
    private RegistrarChaquetaUseCase registrarChaquetaUseCase;
    @MockitoBean
    private RegistrarGuantesUseCase registrarGuantesUseCase;
    @MockitoBean
    private RegistrarImpermeableUseCase registrarImpermeableUseCase;
    @MockitoBean
    private RegistrarIntercomunicadorUseCase registrarIntercomunicadorUseCase;
    @MockitoBean
    private RegistrarSoporteCelularUseCase registrarSoporteCelularUseCase;
    @MockitoBean
    private RegistrarVisorUseCase registrarVisorUseCase;
    @MockitoBean
    private PublicarProductoUseCase publicarProductoUseCase;
    @MockitoBean
    private DespublicarProductoUseCase despublicarProductoUseCase;
    @MockitoBean
    private GestionarCompatibilidadUseCase gestionarCompatibilidadUseCase;
    @MockitoBean
    private BuscarProductoUseCase buscarProductoUseCase;

    private MockHttpServletRequestBuilder prepararRegistro(String ruta, String especificos) {
        String json = "{\"marcaId\":\"m1\",\"marcaNombre\":\"Marca\",\"precioMonto\":100,\"precioMoneda\":\"COP\",\"color\":\"Negro\"," + especificos + "}";
        return post("/api/productos/" + ruta).contentType(MediaType.APPLICATION_JSON).content(json);
    }

    private void comprobarRegistro(ResultActions respuesta, String tipo) throws Exception {
        respuesta.andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("http://localhost/api/productos/[^/]+")))
                .andExpect(jsonPath("$.id", not(emptyOrNullString())))
                .andExpect(jsonPath("$.tipo").value(tipo))
                .andExpect(jsonPath("$.marca").value("Marca"))
                .andExpect(jsonPath("$.color").value("Negro"))
                .andExpect(jsonPath("$.precioMonto").value(100))
                .andExpect(jsonPath("$.precioMoneda").value("COP"))
                .andExpect(jsonPath("$.estadoPublicacion").value("NO_PUBLICADO"))
                .andExpect(jsonPath("$.precio").doesNotExist());
    }

    private void comprobarIdYLocation(ArgumentCaptor<String> id, MvcResult resultado) {
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
        assertEquals("http://localhost/api/productos/" + id.getValue(), resultado.getResponse().getHeader("Location"));
    }

    @Test
    public void registraBaulConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarBaulUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(Material.ABS), eq(30.0), eq("40x40"), eq(new Precio(100, "COP")), eq(5.0), eq("Negro")))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Baul", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("baules", "\"material\":\"ABS\",\"capacidadLitros\":30,\"dimensiones\":\"40x40\",\"capacidadMaximaCarga\":5");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Baul");
        verify(registrarBaulUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(Material.ABS), eq(30.0), eq("40x40"), eq(new Precio(100, "COP")), eq(5.0), eq("Negro"));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraCascoConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarCascoUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq("K-3"), eq("Negro"), eq(Talla.M), eq(new Precio(100, "COP")), eq(Proteccion.NIVEL_1), eq("DOT")))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Casco", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("cascos", "\"modelo\":\"K-3\",\"talla\":\"M\",\"proteccion\":\"NIVEL_1\",\"certificacionSeguridad\":\"DOT\"");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Casco");
        verify(registrarCascoUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq("K-3"), eq("Negro"), eq(Talla.M), eq(new Precio(100, "COP")), eq(Proteccion.NIVEL_1), eq("DOT"));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraChaquetaConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarChaquetaUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"),  eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Chaqueta", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("chaquetas", "\"talla\":\"M\",\"material\":\"CUERO\",\"proteccion\":\"NIVEL_1\"");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Chaqueta");
        verify(registrarChaquetaUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraGuantesConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarGuantesUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Guantes", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("guantes", "\"talla\":\"M\",\"material\":\"CUERO\",\"proteccion\":\"NIVEL_1\"");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Guantes");
        verify(registrarGuantesUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"),  eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraImpermeableConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarImpermeableUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(Talla.M), eq(Material.PVC)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Impermeable", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("impermeables", "\"talla\":\"M\",\"material\":\"PVC\"");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Impermeable");
        verify(registrarImpermeableUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"),  eq(Talla.M), eq(Material.PVC));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraIntercomunicadorConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarIntercomunicadorUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(100), eq(8), eq(false)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Intercomunicador", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("intercomunicadores", "\"alcanceMetros\":100,\"autonomiaHoras\":8,\"bluetooth\":false");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Intercomunicador");
        verify(registrarIntercomunicadorUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"),  eq(100), eq(8), eq(false));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraSoporteCelularConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarSoporteCelularUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"),  eq("Manillar")))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("SoporteCelular", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("soportes-celular", "\"tipoSujecion\":\"Manillar\"");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "SoporteCelular");
        verify(registrarSoporteCelularUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq("Manillar"));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void registraVisorConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarVisorUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq("Integral"), eq(List.of("K-3"))))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Visor", invocacion.getArgument(0, String.class)));
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        var solicitud1 = prepararRegistro("visores", "\"tipoVisor\":\"Integral\",\"modelosCascoCompatibles\":[\"K-3\"]");

        var respuesta = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta.andReturn();

        comprobarRegistro(respuesta, "Visor");
        verify(registrarVisorUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq("Integral"), eq(List.of("K-3")));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    public void noDeberiaLlamarAlCasoDeUsoSiLosDatosSonInvalidos() throws Exception {
        String[] rutas = {"baules", "cascos", "chaquetas", "guantes", "impermeables", "intercomunicadores", "soportes-celular", "visores"};
        var respuestas = new java.util.ArrayList<MvcResult>();

        for (String ruta : rutas) {
            respuestas.add(mockMvc.perform(post("/api/productos/" + ruta)
                    .contentType(MediaType.APPLICATION_JSON).content("{}")).andReturn());
        }

        for (MvcResult respuesta : respuestas) {
            assertEquals(400, respuesta.getResponse().getStatus());
        }
        verify(registrarCascoUseCase, never()).ejecutar(
                anyString(), any(), anyString(), anyString(), any(), any(), any(), anyString());
        verifyNoInteractions(registrarBaulUseCase, registrarCascoUseCase, registrarChaquetaUseCase, registrarGuantesUseCase, registrarImpermeableUseCase, registrarIntercomunicadorUseCase, registrarSoporteCelularUseCase, registrarVisorUseCase);
    }

    @Test
    public void publicaProductoPorIdDeUrl() throws Exception {
        Producto producto = DatosRestPrueba.producto("Casco", "p1");
        producto.publicar();

        when(publicarProductoUseCase.ejecutar("p1")).thenReturn(producto);

        var solicitud1 = put("/api/productos/p1/publicar");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.estadoPublicacion").value("PUBLICADO"));
        verify(publicarProductoUseCase).ejecutar("p1");
    }

    @Test
    public void despublicaProductoPorIdDeUrl() throws Exception {
        when(despublicarProductoUseCase.ejecutar("p1")).thenReturn(DatosRestPrueba.producto("Casco", "p1"));

        var solicitud1 = put("/api/productos/p1/despublicar");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk()).andExpect(jsonPath("$.estadoPublicacion").value("NO_PUBLICADO"));
        verify(despublicarProductoUseCase).ejecutar("p1");
    }

    @Test
    public void indicaCompatibilidadConIdSoloEnUrl() throws Exception {
        when(gestionarCompatibilidadUseCase.indicar("p1", TipoCompatibilidad.CASCO, "K-3"))
                .thenReturn(DatosRestPrueba.producto("Visor", "p1"));

        var solicitud1 = put("/api/productos/p1/compatibilidades").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"CASCO\",\"modelo\":\"K-3\"}");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk()).andExpect(jsonPath("$.id").value("p1"));
        verify(gestionarCompatibilidadUseCase).indicar("p1", TipoCompatibilidad.CASCO, "K-3");
    }

    @Test
    public void rechazaCompatibilidadSinDatosObligatorios() throws Exception {
        var solicitud1 = put("/api/productos/p1/compatibilidades").contentType(MediaType.APPLICATION_JSON).content("{}");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isBadRequest());
        verifyNoInteractions(gestionarCompatibilidadUseCase);
    }

    @Test
    public void retiraCompatibilidadMedianteQuerySinBody() throws Exception {
        when(gestionarCompatibilidadUseCase.retirar("p1", TipoCompatibilidad.CASCO, "K-3"))
                .thenReturn(DatosRestPrueba.producto("Visor", "p1"));

        var solicitud1 = delete("/api/productos/p1/compatibilidades").param("tipo", "CASCO").param("modelo", "K-3");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk()).andExpect(jsonPath("$.id").value("p1"));
        verify(gestionarCompatibilidadUseCase).retirar("p1", TipoCompatibilidad.CASCO, "K-3");
    }

    @Test
    public void buscaSinFiltrosYSinBody() throws Exception {
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, null);
        when(buscarProductoUseCase.buscar(filtro,0,5)).thenReturn(new Pagina<>(List.of(DatosRestPrueba.producto("Casco", "p1")),1,1,0,5));

        var solicitud1 = get("/api/productos");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value("p1"));
        verify(buscarProductoUseCase).buscar(filtro,0,5);
    }

    @Test
    public void transmiteTodosLosFiltrosDeQueryAlDominio() throws Exception {
        FiltroProducto filtro = new FiltroProducto("AGV", "negro", 100.0, 200.0, "COP", new Compatibilidad(TipoCompatibilidad.CASCO, "K-3"));
        when(buscarProductoUseCase.buscar(filtro,0,5)).thenReturn(new Pagina<>(List.of(),0,0,0,5));

        var solicitud1 = get("/api/productos").param("marca", "AGV").param("color", "negro").param("precioMinimo", "100").param("precioMaximo", "200").param("moneda", "COP").param("tipoCompatibilidad", "CASCO").param("modeloCompatibilidad", "K-3");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
        verify(buscarProductoUseCase).buscar(filtro,0,5);
    }

    @Test
    public void rechazaParametrosQueNoPuedenConvertirse() throws Exception {
        var solicitud1 = delete("/api/productos/p1/compatibilidades").param("tipo", "CASCO");
        var solicitud2 = delete("/api/productos/p1/compatibilidades").param("tipo", "INVALIDO").param("modelo", "K-3");
        var solicitud3 = get("/api/productos").param("precioMinimo", "abc");

        var respuesta1 = mockMvc.perform(solicitud3);
        var respuesta2 = mockMvc.perform(solicitud2);
        var respuesta3 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isBadRequest());
        respuesta2.andExpect(status().isBadRequest());
        respuesta3.andExpect(status().isBadRequest());
        verifyNoInteractions(buscarProductoUseCase, gestionarCompatibilidadUseCase);
    }

    @Test
    public void deberiaResponder404CuandoProductoNoExiste() throws Exception {
        when(publicarProductoUseCase.ejecutar("no-existe"))
                .thenThrow(new java.util.NoSuchElementException("No existe un producto con ese id"));

        var solicitud1 = put("/api/productos/no-existe/publicar");

        var respuesta = mockMvc.perform(solicitud1);

        respuesta.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El recurso solicitado no existe"));
    }

    @Test
    public void deberiaResponder400CuandoSeRompeReglaDeNegocio() throws Exception {
        when(publicarProductoUseCase.ejecutar("publicado"))
                .thenThrow(new ReglaDominioException("El producto ya esta publicado"));

        var solicitud1 = put("/api/productos/publicado/publicar");

        var respuesta = mockMvc.perform(solicitud1);

        respuesta.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El producto ya esta publicado"));
    }
}