package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaulTest {

    private Baul crear(Material material, double litros, double carga) {
        return Baul.crear("b1", new Marca("m1", "Givi"), material, litros, null, new Precio(100, "COP"), carga, null);
    }

    @Test
    void permiteCapacidadesValidasSinImponerColorODimensiones() {
        assertNotNull(crear(Material.ABS, 30, 5));
    }

    @Test
    void rechazaMaterialNulo() {
        assertThrows(ReglaDominioException.class, () -> crear(null, 30, 5));
    }

    @Test
    void rechazaCapacidadEnLitrosNoPositivaONoFinita() {
        for (double litros : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(ReglaDominioException.class, () -> crear(Material.ABS, litros, 5));
        }
    }

    @Test
    void rechazaCargaNoPositivaONoFinita() {
        for (double carga : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(ReglaDominioException.class, () -> crear(Material.ABS, 30, carga));
        }
    }
}
