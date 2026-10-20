package com.uniquindio.accesoriosMoto.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompatibilidadTest {
    @Test
    void normalizaModeloYConsideraDestinoEnIgualdad() {
        Compatibilidad modelo = new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 ");
        assertEquals(new Compatibilidad(TipoCompatibilidad.CASCO, "K-3"), modelo);
        assertEquals("K-3", modelo.modelo());
        assertNotEquals(new Compatibilidad(TipoCompatibilidad.MOTO, "K-3"), modelo);
    }

    @Test
    void rechazaDatosInvalidos() {
        assertThrows(ReglaDominioException.class, () -> new Compatibilidad(null, "K-3"));
        assertThrows(ReglaDominioException.class, () -> new Compatibilidad(TipoCompatibilidad.CASCO, null));
        assertThrows(ReglaDominioException.class, () -> new Compatibilidad(TipoCompatibilidad.CASCO, " "));
    }
}
