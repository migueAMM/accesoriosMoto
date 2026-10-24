package com.uniquindio.accesoriosMoto.domain.application.dto.request;

import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarBaulRequest;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrarBaulRequestTest {

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

    @Test
    void aceptaDatosValidos() {
        RegistrarBaulRequest request = new RegistrarBaulRequest("m1", "Givi", Material.ABS, 30.0, 5.0, "40 x 40 x 30 cm", "Negro", 250000.0, "COP");
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rechazaCamposOmitidos() {
        RegistrarBaulRequest request = new RegistrarBaulRequest(null, null, null, null, null, null, null, null, null);

        Set<String> campos = validator.validate(request).stream()
                .map(violacion -> violacion.getPropertyPath().toString()).collect(Collectors.toSet());

        assertEquals(Set.of("marcaId", "marcaNombre", "material", "capacidadLitros", "capacidadMaximaCarga", "dimensiones", "color", "precioMonto", "precioMoneda"), campos);
    }

    @Test
    void rechazaTextosEnBlancoYCantidadesNoPositivas() {
        RegistrarBaulRequest request = new RegistrarBaulRequest(" ", " ", Material.ABS, 0.0, -1.0, " ", " ", 0.0, " ");

        Set<String> campos = validator.validate(request).stream()
                .map(violacion -> violacion.getPropertyPath().toString()).collect(Collectors.toSet());

        assertEquals(Set.of("marcaId", "marcaNombre", "capacidadLitros", "capacidadMaximaCarga", "dimensiones", "color", "precioMonto", "precioMoneda"), campos);
    }
}
