package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.application.usecase.*;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.rest.ProductoController;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ProductoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import({ProductoMapper.class, com.uniquindio.accesoriosMoto.infrastructure.security.SecurityConfig.class})
@org.springframework.security.test.context.support.WithMockUser(username="u1", roles="ADMINISTRADOR")
class ProductoControllerTest {
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
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

    private MvcResult registrar(String ruta, String especificos, String tipo) throws Exception {
        String json = "{\"marcaId\":\"m1\",\"marcaNombre\":\"Marca\",\"precioMonto\":100,\"precioMoneda\":\"COP\",\"color\":\"Negro\"," + especificos + "}";
        return mockMvc.perform(post("/api/productos/" + ruta).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("http://localhost/api/productos/[^/]+")))
                .andExpect(jsonPath("$.id", not(emptyOrNullString())))
                .andExpect(jsonPath("$.tipo").value(tipo))
                .andExpect(jsonPath("$.marca").value("Marca"))
                .andExpect(jsonPath("$.color").value("Negro"))
                .andExpect(jsonPath("$.precioMonto").value(100))
                .andExpect(jsonPath("$.precioMoneda").value("COP"))
                .andExpect(jsonPath("$.estadoPublicacion").value("NO_PUBLICADO"))
                .andExpect(jsonPath("$.precio").doesNotExist()).andReturn();
    }

    private void comprobarIdYLocation(ArgumentCaptor<String> id, MvcResult resultado) {
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
        assertEquals("http://localhost/api/productos/" + id.getValue(), resultado.getResponse().getHeader("Location"));
    }

    @Test
    void registraBaulConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarBaulUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(Material.ABS), eq(30.0), eq("40x40"), eq(new Precio(100, "COP")), eq(5.0), eq("Negro")))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Baul", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("baules", "\"material\":\"ABS\",\"capacidadLitros\":30,\"dimensiones\":\"40x40\",\"capacidadMaximaCarga\":5", "Baul");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarBaulUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(Material.ABS), eq(30.0), eq("40x40"), eq(new Precio(100, "COP")), eq(5.0), eq("Negro"));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraCascoConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarCascoUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq("K-3"), eq("Negro"), eq(Talla.M), eq(new Precio(100, "COP")), eq(Proteccion.NIVEL_1), eq("DOT")))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Casco", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("cascos", "\"modelo\":\"K-3\",\"talla\":\"M\",\"proteccion\":\"NIVEL_1\",\"certificacionSeguridad\":\"DOT\"", "Casco");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarCascoUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq("K-3"), eq("Negro"), eq(Talla.M), eq(new Precio(100, "COP")), eq(Proteccion.NIVEL_1), eq("DOT"));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraChaquetaConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarChaquetaUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Chaqueta", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("chaquetas", "\"talla\":\"M\",\"material\":\"CUERO\",\"proteccion\":\"NIVEL_1\"", "Chaqueta");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarChaquetaUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraGuantesConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarGuantesUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Guantes", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("guantes", "\"talla\":\"M\",\"material\":\"CUERO\",\"proteccion\":\"NIVEL_1\"", "Guantes");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarGuantesUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(Talla.M), eq(Material.CUERO), eq(Proteccion.NIVEL_1));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraImpermeableConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarImpermeableUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(Talla.M), eq(Material.PVC)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Impermeable", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("impermeables", "\"talla\":\"M\",\"material\":\"PVC\"", "Impermeable");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarImpermeableUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(Talla.M), eq(Material.PVC));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraIntercomunicadorConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarIntercomunicadorUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(100), eq(8), eq(false)))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Intercomunicador", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("intercomunicadores", "\"alcanceMetros\":100,\"autonomiaHoras\":8,\"bluetooth\":false", "Intercomunicador");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarIntercomunicadorUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq(100), eq(8), eq(false));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraSoporteCelularConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarSoporteCelularUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq("Manillar"), eq("4 a 7 pulgadas")))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("SoporteCelular", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("soportes-celular", "\"tipoSujecion\":\"Manillar\",\"compatibleTamanoCelular\":\"4 a 7 pulgadas\"", "SoporteCelular");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarSoporteCelularUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq("Manillar"), eq("4 a 7 pulgadas"));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void registraVisorConIdGeneradoYLocationGeneral() throws Exception {
        when(registrarVisorUseCase.ejecutar(anyString(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq("Integral"), eq(List.of("K-3"))))
                .thenAnswer(invocacion -> DatosRestPrueba.producto("Visor", invocacion.getArgument(0, String.class)));

        MvcResult resultado = registrar("visores", "\"tipoVisor\":\"Integral\",\"modelosCascoCompatibles\":[\"K-3\"]", "Visor");

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarVisorUseCase).ejecutar(id.capture(), eq(new Marca("m1", "Marca")), eq(new Precio(100, "COP")), eq("Negro"), eq(EstadoPublicacion.NO_PUBLICADO), eq("Integral"), eq(List.of("K-3")));
        comprobarIdYLocation(id, resultado);
    }

    @Test
    void rechazaRequestsInvalidosSinEjecutarRegistros() throws Exception {
        for (String ruta : new String[]{"baules", "cascos", "chaquetas", "guantes", "impermeables", "intercomunicadores", "soportes-celular", "visores"}) {
            mockMvc.perform(post("/api/productos/" + ruta).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(registrarBaulUseCase, registrarCascoUseCase, registrarChaquetaUseCase,
                registrarGuantesUseCase, registrarImpermeableUseCase, registrarIntercomunicadorUseCase,
                registrarSoporteCelularUseCase, registrarVisorUseCase);
    }

    @Test
    void publicaProductoPorIdDeUrl() throws Exception {
        Producto producto = DatosRestPrueba.producto("Casco", "p1");
        producto.publicar();
        when(publicarProductoUseCase.ejecutar("p1")).thenReturn(producto);

        mockMvc.perform(put("/api/productos/p1/publicar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.estadoPublicacion").value("PUBLICADO"));

        verify(publicarProductoUseCase).ejecutar("p1");
    }

    @Test
    void despublicaProductoPorIdDeUrl() throws Exception {
        when(despublicarProductoUseCase.ejecutar("p1")).thenReturn(DatosRestPrueba.producto("Casco", "p1"));

        mockMvc.perform(put("/api/productos/p1/despublicar"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estadoPublicacion").value("NO_PUBLICADO"));

        verify(despublicarProductoUseCase).ejecutar("p1");
    }

    @Test
    void indicaCompatibilidadConIdSoloEnUrl() throws Exception {
        when(gestionarCompatibilidadUseCase.indicar("p1", TipoCompatibilidad.CASCO, "K-3"))
                .thenReturn(DatosRestPrueba.producto("Visor", "p1"));

        mockMvc.perform(put("/api/productos/p1/compatibilidades").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"CASCO\",\"modelo\":\"K-3\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("p1"));

        verify(gestionarCompatibilidadUseCase).indicar("p1", TipoCompatibilidad.CASCO, "K-3");
    }

    @Test
    void rechazaCompatibilidadSinDatosObligatorios() throws Exception {
        mockMvc.perform(put("/api/productos/p1/compatibilidades").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(gestionarCompatibilidadUseCase);
    }

    @Test
    void retiraCompatibilidadMedianteQuerySinBody() throws Exception {
        when(gestionarCompatibilidadUseCase.retirar("p1", TipoCompatibilidad.CASCO, "K-3"))
                .thenReturn(DatosRestPrueba.producto("Visor", "p1"));

        mockMvc.perform(delete("/api/productos/p1/compatibilidades").param("tipo", "CASCO").param("modelo", "K-3"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("p1"));

        verify(gestionarCompatibilidadUseCase).retirar("p1", TipoCompatibilidad.CASCO, "K-3");
    }

    @Test
    void buscaSinFiltrosYSinBody() throws Exception {
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, null);
        when(buscarProductoUseCase.ejecutar(filtro)).thenReturn(List.of(DatosRestPrueba.producto("Casco", "p1")));

        mockMvc.perform(get("/api/productos")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("p1"));

        verify(buscarProductoUseCase).ejecutar(filtro);
    }

    @Test
    void transmiteTodosLosFiltrosDeQueryAlDominio() throws Exception {
        FiltroProducto filtro = new FiltroProducto("AGV", "negro", 100.0, 200.0, "COP",
                new Compatibilidad(TipoCompatibilidad.CASCO, "K-3"));
        when(buscarProductoUseCase.ejecutar(filtro)).thenReturn(List.of());

        mockMvc.perform(get("/api/productos").param("marca", "AGV").param("color", "negro")
                        .param("precioMinimo", "100").param("precioMaximo", "200").param("moneda", "COP")
                        .param("tipoCompatibilidad", "CASCO").param("modeloCompatibilidad", "K-3"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));

        verify(buscarProductoUseCase).ejecutar(filtro);
    }

    @Test
    void rechazaParametrosQueNoPuedenConvertirse() throws Exception {
        mockMvc.perform(get("/api/productos").param("precioMinimo", "abc")).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/productos/p1/compatibilidades").param("tipo", "INVALIDO").param("modelo", "K-3"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/productos/p1/compatibilidades").param("tipo", "CASCO"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(buscarProductoUseCase, gestionarCompatibilidadUseCase);
    }

    @Test
    void propagaErrorDeDominioSinTryCatchLocal() {
        ReglaDominioException error = new ReglaDominioException("No existe el producto");
        when(publicarProductoUseCase.ejecutar("no")).thenThrow(error);

        Exception propagado = assertThrows(Exception.class, () -> mockMvc.perform(put("/api/productos/no/publicar")));

        assertSame(error, propagado.getCause());
    }
}

