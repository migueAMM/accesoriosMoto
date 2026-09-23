package com.uniquindio.accesoriosMoto.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarcaTest {

    @Test
    public void dosMarcasConElMismoValorSonIguales(){
        Marca marca1 = new Marca("M1", "AGV");
        Marca marca2 = new Marca("M1", "AGV");

        assertEquals(marca1, marca2);

    }

}
