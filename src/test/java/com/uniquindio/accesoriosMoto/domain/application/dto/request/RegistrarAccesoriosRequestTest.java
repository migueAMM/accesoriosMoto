package com.uniquindio.accesoriosMoto.domain.application.dto.request;

import com.uniquindio.accesoriosMoto.application.dto.request.*;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrarAccesoriosRequestTest {

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

    private List<Object> requests(String marcaId, String marcaNombre, Double monto, String moneda, String color) {
        return List.of(
                new RegistrarCascoRequest(marcaId, marcaNombre, monto, moneda, color, "K-3", Talla.M, Proteccion.NIVEL_1, "DOT"),
                new RegistrarChaquetaRequest(marcaId, marcaNombre, monto, moneda, color, Talla.M, Material.CUERO, Proteccion.NIVEL_1),
                new RegistrarGuantesRequest(marcaId, marcaNombre, monto, moneda, color, Talla.M, Material.CUERO, Proteccion.NIVEL_1),
                new RegistrarImpermeableRequest(marcaId, marcaNombre, monto, moneda, color, Talla.M, Material.PVC),
                new RegistrarIntercomunicadorRequest(marcaId, marcaNombre, monto, moneda, color, 100, 8, true),
                new RegistrarSoporteCelularRequest(marcaId, marcaNombre, monto, moneda, color, "Manillar"),
                new RegistrarVisorRequest(marcaId, marcaNombre, monto, moneda, color, "Integral", List.of("K-3")));
    }

    private Set<String> camposInvalidos(Object request) {
        return validator.validate(request).stream()
                .map(violacion -> violacion.getPropertyPath().toString()).collect(Collectors.toSet());
    }


    @Test
    public void aceptaDatosValidosEnLosSieteRequests() {
        var entradas = requests("m1", "Marca", 100.0, "COP", "Negro");
        Set<String> esperados = Set.of();

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void exigeTodosLosCamposComunes() {
        var entradas = requests(null, null, null, null, null);
        Set<String> esperados = Set.of("marcaId", "marcaNombre", "precioMonto", "precioMoneda", "color");

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void rechazaTextosComunesEnBlanco() {
        var entradas = requests(" ", " ", 100.0, " ", " ");
        Set<String> esperados = Set.of("marcaId", "marcaNombre", "precioMoneda", "color");

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void rechazaPrecioCeroONegativoEnTodosLosRequests() {
        var entradas = java.util.stream.DoubleStream.of(0, -1).boxed().flatMap(monto -> requests("m1", "Marca", monto, "COP", "Negro").stream()).toList();
        Set<String> esperados = Set.of("precioMonto");

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void exigeCamposEspecificosIncluidosEnumsBluetoothYListaDelVisor() {
        Map<Object, Set<String>> casos = Map.of(
                new RegistrarCascoRequest("m1", "Marca", 100.0, "COP", "Negro", null, null, null, null),
                Set.of("modelo", "talla", "proteccion", "certificacionSeguridad"),
                new RegistrarChaquetaRequest("m1", "Marca", 100.0, "COP", "Negro", null, null, null),
                Set.of("talla", "material", "proteccion"),
                new RegistrarGuantesRequest("m1", "Marca", 100.0, "COP", "Negro", null, null, null),
                Set.of("talla", "material", "proteccion"),
                new RegistrarImpermeableRequest("m1", "Marca", 100.0, "COP", "Negro", null, null),
                Set.of("talla", "material"),
                new RegistrarIntercomunicadorRequest("m1", "Marca", 100.0, "COP", "Negro", null, null, null),
                Set.of("alcanceMetros", "autonomiaHoras", "bluetooth"),
                new RegistrarSoporteCelularRequest("m1", "Marca", 100.0, "COP", "Negro", null),
                Set.of("tipoSujecion"),
                new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", null, null),
                Set.of("tipoVisor", "modelosCascoCompatibles"));

        var resultados = new java.util.HashMap<Object, Set<String>>();
        casos.keySet().forEach(request -> resultados.put(request, camposInvalidos(request)));

        casos.forEach((request, esperados) -> assertEquals(esperados, resultados.get(request), request.getClass().getSimpleName()));
    }

    @Test
    public void rechazaTextosEspecificosEnBlanco() {
        var entradas = List.of(new RegistrarCascoRequest("m1", "Marca", 100.0, "COP", "Negro", " ", Talla.M, Proteccion.NIVEL_1, " "), new RegistrarSoporteCelularRequest("m1", "Marca", 100.0, "COP", "Negro", " "), new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", " ", List.of()));
        var esperados = List.of(Set.of("modelo", "certificacionSeguridad"), Set.of("tipoSujecion"), Set.of("tipoVisor"));

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados.get(indice), resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void rechazaAlcanceYAutonomiaNoPositivos() {
        var entradas = java.util.stream.IntStream.of(0, -1).mapToObj(numero -> (Object) new RegistrarIntercomunicadorRequest("m1", "Marca", 100.0, "COP", "Negro", numero, numero, true)).toList();
        Set<String> esperados = Set.of("alcanceMetros", "autonomiaHoras");

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void aceptaBluetoothFalse() {
        var entradas = List.of(new RegistrarIntercomunicadorRequest("m1", "Marca", 100.0, "COP", "Negro", 100, 8, false));
        Set<String> esperados = Set.of();

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void aceptaVisorSinCompatibilidadesIniciales() {
        var entradas = List.of(new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", "Integral", List.of()));
        Set<String> esperados = Set.of();

        var resultados = entradas.stream().map(this::camposInvalidos).toList();

        for (int indice = 0; indice < entradas.size(); indice++) {
            assertEquals(esperados, resultados.get(indice), entradas.get(indice).getClass().getSimpleName());
        }
    }

    @Test
    public void rechazaModelosDeCascoVaciosEnBlancoONulos() {
        RegistrarVisorRequest request = new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", "Integral", Arrays.asList("", " ", null));

        Set<String> campos = camposInvalidos(request);

        assertEquals(3, campos.size());
        for (int indice = 0; indice < 3; indice++) {
            String prefijo = "modelosCascoCompatibles[" + indice + "]";
            assertTrue(campos.stream().anyMatch(campo -> campo.startsWith(prefijo)));
        }
    }
}