package com.uniquindio.accesoriosMoto.domain.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

public class CompatibilidadTest {
    @Test
    public void normalizaModeloYConsideraDestinoEnIgualdad() {

        Compatibilidad modelo = new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 ");

        assertEquals(new Compatibilidad(TipoCompatibilidad.CASCO, "K-3"), modelo);
        assertEquals("K-3", modelo.modelo());
        assertNotEquals(new Compatibilidad(TipoCompatibilidad.MOTO, "K-3"), modelo);
    }

    @Test
    public void rechazaDatosInvalidos() {

        var accion1Error = catchThrowable(() -> new Compatibilidad(null, "K-3"));
        var accion2Error = catchThrowable(() -> new Compatibilidad(TipoCompatibilidad.CASCO, null));
        var accion3Error = catchThrowable(() -> new Compatibilidad(TipoCompatibilidad.CASCO, " "));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertInstanceOf(ReglaDominioException.class, accion3Error);
    }
}
