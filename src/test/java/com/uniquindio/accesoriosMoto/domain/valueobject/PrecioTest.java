package com.uniquindio.accesoriosMoto.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrecioTest {

    @Test
    void unPrecioNegativoLanzaReglaDominioException() {
        assertThrows(ReglaDominioException.class, () -> new Precio(-100, "COP"));
    }

}
