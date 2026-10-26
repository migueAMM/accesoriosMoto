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

    private void comprobarCampos(Object request, Set<String> esperados) {
        assertEquals(esperados, camposInvalidos(request), request.getClass().getSimpleName());
    }

    @Test
    void aceptaDatosValidosEnLosSieteRequests() {
        for (Object request : requests("m1", "Marca", 100.0, "COP", "Negro")) {
            comprobarCampos(request, Set.of());
        }
    }

    @Test
    void exigeTodosLosCamposComunes() {
        for (Object request : requests(null, null, null, null, null)) {
            comprobarCampos(request, Set.of("marcaId", "marcaNombre", "precioMonto", "precioMoneda", "color"));
        }
    }

    @Test
    void rechazaTextosComunesEnBlanco() {
        for (Object request : requests(" ", " ", 100.0, " ", " ")) {
            comprobarCampos(request, Set.of("marcaId", "marcaNombre", "precioMoneda", "color"));
        }
    }

    @Test
    void rechazaPrecioCeroONegativoEnTodosLosRequests() {
        for (double monto : new double[]{0, -1}) {
            for (Object request : requests("m1", "Marca", monto, "COP", "Negro")) {
                comprobarCampos(request, Set.of("precioMonto"));
            }
        }
    }

    @Test
    void exigeCamposEspecificosIncluidosEnumsBluetoothYListaDelVisor() {
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

        casos.forEach(this::comprobarCampos);
    }

    @Test
    void rechazaTextosEspecificosEnBlanco() {
        comprobarCampos(new RegistrarCascoRequest("m1", "Marca", 100.0, "COP", "Negro", " ", Talla.M, Proteccion.NIVEL_1, " "), Set.of("modelo", "certificacionSeguridad"));
        comprobarCampos(new RegistrarSoporteCelularRequest("m1", "Marca", 100.0, "COP", "Negro", " "), Set.of("tipoSujecion"));
        comprobarCampos(new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", " ", List.of()), Set.of("tipoVisor"));
    }

    @Test
    void rechazaAlcanceYAutonomiaNoPositivos() {
        for (int numero : new int[]{0, -1}) {
            RegistrarIntercomunicadorRequest request = new RegistrarIntercomunicadorRequest("m1", "Marca", 100.0, "COP", "Negro", numero, numero, true);

            comprobarCampos(request, Set.of("alcanceMetros", "autonomiaHoras"));
        }
    }

    @Test
    void aceptaBluetoothFalse() {
        RegistrarIntercomunicadorRequest request = new RegistrarIntercomunicadorRequest("m1", "Marca", 100.0, "COP", "Negro", 100, 8, false);

        comprobarCampos(request, Set.of());
    }

    @Test
    void aceptaVisorSinCompatibilidadesIniciales() {
        RegistrarVisorRequest request = new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", "Integral", List.of());

        comprobarCampos(request, Set.of());
    }

    @Test
    void rechazaModelosDeCascoVaciosEnBlancoONulos() {
        RegistrarVisorRequest request = new RegistrarVisorRequest("m1", "Marca", 100.0, "COP", "Negro", "Integral", Arrays.asList("", " ", null));

        Set<String> campos = camposInvalidos(request);
        assertEquals(3, campos.size());
        for (int indice = 0; indice < 3; indice++) {
            String prefijo = "modelosCascoCompatibles[" + indice + "]";
            assertTrue(campos.stream().anyMatch(campo -> campo.startsWith(prefijo)));
        }
    }
}
