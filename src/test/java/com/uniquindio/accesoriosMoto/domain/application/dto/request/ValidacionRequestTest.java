package com.uniquindio.accesoriosMoto.domain.application.dto.request;

import com.uniquindio.accesoriosMoto.application.dto.request.*;
import com.uniquindio.accesoriosMoto.domain.valueObject.TipoCompatibilidad;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidacionRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void prepararValidacion() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidacion() {
        factory.close();
    }

    private Set<String> camposInvalidos(Object request) {
        return validator.validate(request).stream()
                .map(violacion -> violacion.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void compatibilidadAceptaAmbosDestinos() {
        for (TipoCompatibilidad tipo : TipoCompatibilidad.values()) {
            GestionarCompatibilidadRequest request = new GestionarCompatibilidadRequest( tipo, "K-3");

            assertTrue(validator.validate(request).isEmpty());
        }
    }

    @Test
    void compatibilidadExigeProductoTipoYModelo() {
        assertEquals(Set.of("tipo", "modelo"),
                camposInvalidos(new GestionarCompatibilidadRequest(null, null)));
        assertEquals(Set.of("modelo"),
                camposInvalidos(new GestionarCompatibilidadRequest( TipoCompatibilidad.MOTO, " ")));
    }

    @Test
    void busquedaPermiteTodosLosFiltrosOmitidos() {
        BuscarProductoRequest request = new BuscarProductoRequest(null, null, null, null, null, null, null);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void busquedaAceptaCeroYLimitesIndependientes() {
        List<BuscarProductoRequest> requests = List.of(
                new BuscarProductoRequest("AGV", "Negro", 0.0, 100.0, "COP", TipoCompatibilidad.CASCO, "K-3"),
                new BuscarProductoRequest(null, null, 100.0, null, "COP", null, null),
                new BuscarProductoRequest(null, null, null, 0.0, "COP", null, null));

        for (BuscarProductoRequest request : requests) {
            assertTrue(validator.validate(request).isEmpty());
        }
    }

    @Test
    void busquedaRechazaPreciosNegativos() {
        BuscarProductoRequest request = new BuscarProductoRequest(null, null, -1.0, -2.0, "COP", null, null);

        assertEquals(Set.of("precioMinimo", "precioMaximo"), camposInvalidos(request));
    }

    @Test
    void reembolsoAceptaMotivoSinImponerLongitudDelProyectoEjemplo() {
        SolicitarReembolsoRequest request = new SolicitarReembolsoRequest("Dañado");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void reembolsoExigeMotivo() {
        for (String texto : new String[]{null, "", " "}) {
            assertEquals(Set.of("motivo"), camposInvalidos(new SolicitarReembolsoRequest(texto)));
        }
    }

    @Test
    void itemAceptaCantidadPositiva() {
        assertTrue(validator.validate(new ItemPedidoRequest("p1", 1)).isEmpty());
    }

    @Test
    void itemExigeProductoYCantidad() {
        assertEquals(Set.of("productoId", "cantidad"), camposInvalidos(new ItemPedidoRequest(null, null)));
        assertEquals(Set.of("productoId"), camposInvalidos(new ItemPedidoRequest(" ", 1)));
    }

    @Test
    void itemRechazaCantidadCeroONegativa() {
        for (int cantidad : new int[]{0, -1}) {
            assertEquals(Set.of("cantidad"), camposInvalidos(new ItemPedidoRequest("p1", cantidad)));
        }
    }

    @Test
    void pedidoAceptaVariosItemsValidos() {
        RegistrarPedidoRequest request = new RegistrarPedidoRequest("u1", List.of(new ItemPedidoRequest("p1", 1), new ItemPedidoRequest("p2", 2)));

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void pedidoExigeCompradorEItemsNoVacios() {
        assertEquals(Set.of("compradorId", "items"), camposInvalidos(new RegistrarPedidoRequest(null, null)));
        assertEquals(Set.of("compradorId", "items"), camposInvalidos(new RegistrarPedidoRequest(" ", List.of())));
    }

    @Test
    void pedidoValidaLosCamposDeCadaItemAnidado() {
        RegistrarPedidoRequest request = new RegistrarPedidoRequest("u1", List.of(new ItemPedidoRequest(" ", 0), new ItemPedidoRequest(null, null)));

        assertEquals(Set.of("items[0].productoId", "items[0].cantidad", "items[1].productoId", "items[1].cantidad"), camposInvalidos(request));
    }

    @Test
    void pedidoRechazaElementoNuloDentroDeLaLista() {
        RegistrarPedidoRequest request = new RegistrarPedidoRequest("u1", Arrays.asList((ItemPedidoRequest) null));

        Set<String> campos = camposInvalidos(request);
        assertEquals(1, campos.size());
        assertTrue(campos.iterator().next().startsWith("items[0]"));
    }

    @Test
    void comentarioAceptaCalificacionesDeUnoACinco() {
        for (int calificacion = 1; calificacion <= 5; calificacion++) {
            DejarComentarioRequest request = new DejarComentarioRequest("u1", "ped1", "p1", calificacion, "Opinión");

            assertTrue(validator.validate(request).isEmpty());
        }
    }

    @Test
    void comentarioExigeTodosSusCampos() {
        assertEquals(Set.of("compradorId", "pedidoId", "productoId", "calificacion", "texto"), camposInvalidos(new DejarComentarioRequest(null, null, null, null, null)));
        assertEquals(Set.of("compradorId", "pedidoId", "productoId", "texto"), camposInvalidos(new DejarComentarioRequest(" ", " ", " ", 3, " ")));
    }

    @Test
    void comentarioRechazaCalificacionesFueraDelRango() {
        for (int calificacion : new int[]{-1, 0, 6}) {
            DejarComentarioRequest request = new DejarComentarioRequest("u1", "ped1", "p1", calificacion, "Opinión");

            assertEquals(Set.of("calificacion"), camposInvalidos(request));
        }
    }
}
