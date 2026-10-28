package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
public class UsuarioTest {

    @Test
    public void permiteObtenerIdentificadorDeAmbosTiposDeUsuario() {

        var resultado1 = new Comprador("c1", "Ana", "ana@correo.com", "clave").getId();
        var resultado2 = new Administrador("a1", "Admin", "admin@correo.com", "clave").getId();

        assertEquals("c1", resultado1);
        assertEquals("a1", resultado2);
    }

    @Test
    public void rechazaIdentificadorNuloVacioOEnBlanco() {

        var entradas1 = new String[]{null, "", " \t"};

        var acciones = new ArrayList<Executable>();
        for (String id : entradas1) {
            acciones.add(() -> new Comprador(id, "Ana", "correo", "clave"));
            acciones.add(() -> new Administrador(id, "Admin", "correo", "clave"));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void conservaIgualdadYHashCodePorIdentidadYTipo() {

        Comprador original = new Comprador("c1", "Ana", "correo", "clave");
        Comprador actualizado = new Comprador("c1", "Otro nombre", "otro correo", "otra clave");

        var resultado2 = new Comprador("c2", "Ana", "correo", "clave");
        var resultado3 = new Administrador("c1", "Ana", "correo", "clave");

        assertEquals(original, actualizado);
        assertEquals(original.hashCode(), actualizado.hashCode());
        assertNotEquals(original, resultado2);
        assertNotEquals(original, resultado3);
    }
}
