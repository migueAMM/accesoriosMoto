package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

public class BaulTest {

    private Baul crear(Material material, double litros, double carga) {
        return Baul.crear("b1", new Marca("m1", "Givi"), material, litros, null, new Precio(100, "COP"), carga, null);
    }

    @Test
    public void permiteCapacidadesValidasSinImponerColorODimensiones() {

        var resultado1 = crear(Material.ABS, 30, 5);

        assertNotNull(resultado1);
    }

    @Test
    public void rechazaMaterialNulo() {

        var accion1Error = catchThrowable(() -> crear(null, 30, 5));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaCapacidadEnLitrosNoPositivaONoFinita() {
        var entradas1 = new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        var acciones = new java.util.ArrayList<Executable>();
        for (double litros : entradas1) {
            acciones.add(() -> crear(Material.ABS, litros, 5));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void rechazaCargaNoPositivaONoFinita() {
        var entradas1 = new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        var acciones = new java.util.ArrayList<Executable>();
        for (double carga : entradas1) {
            acciones.add(() -> crear(Material.ABS, 30, carga));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }
}
