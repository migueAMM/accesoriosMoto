package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioSeguridadTest {
    @Test
    public void rolesSonDerivadosYCorreoEsCanonico() {

        Comprador comprador = new Comprador("c1", "Ana", " ANA@CORREO.COM ", "hash");

        var resultado3 = new Administrador("a1", "Admin", "admin@correo.com", "hash").getRol();

        assertEquals("ana@correo.com", comprador.getCorreo());
        assertEquals(Rol.COMPRADOR, comprador.getRol());
        assertEquals(Rol.ADMINISTRADOR, resultado3);
    }
    @Test
    public void camposObligatoriosSonInvariantesDelUsuario() {

        var entradas1 = new String[]{null, "", " "};

        var acciones = new ArrayList<Executable>();
        for (String invalido : entradas1) {
            acciones.add(() -> new Comprador("c", invalido, "correo", "hash"));
            acciones.add(() -> new Comprador("c", "Ana", invalido, "hash"));
            acciones.add(() -> new Administrador("a", "Admin", "correo", invalido));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }
}
