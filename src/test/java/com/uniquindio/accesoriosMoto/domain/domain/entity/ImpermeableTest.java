package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Impermeable;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImpermeableTest {

    private Impermeable crear(String id, Marca marca) {
        return Impermeable.crear(id, marca, new Precio(80000, "COP"), "Azul", Talla.XL, Material.PVC);
    }

    @Test
    public void nuevoImpermeableConservaAtributosYPuedePublicarseYDespublicarse() {
        Impermeable impermeable = crear("i1", new Marca("m1", "Rainwear"));

        assertFalse(impermeable.estaPublicado());
        impermeable.publicar();
        assertTrue(impermeable.estaPublicado());
        impermeable.despublicar();
        assertFalse(impermeable.estaPublicado());
        assertEquals(Talla.XL, impermeable.getTalla());
        assertEquals(Material.PVC, impermeable.getMaterial());
    }

    @Test
    public void rechazaIdentificadorBlancoYMarcaNula() {
        assertThrows(ReglaDominioException.class, () -> crear(" ", new Marca("m1", "Rainwear")));
        assertThrows(ReglaDominioException.class, () -> crear("i1", null));
    }
}