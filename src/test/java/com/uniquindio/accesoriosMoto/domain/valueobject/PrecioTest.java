package com.uniquindio.accesoriosMoto.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;


public class PrecioTest {

    @Test
    void dosPreciosConElMismoValorDebenSerIguales(){
        Precio p1 = new Precio(15000 , "COP");
        Precio p2 = new Precio(15000 , "COP");
        assertEquals(p1 , p2);
    }

    @Test
    public void unPrecioNegativoLanzaReglaDominioException(){
        assertThrows(ReglaDominioException.class, () -> {
            new Precio(-100000, "COP");
        });
    }

}
