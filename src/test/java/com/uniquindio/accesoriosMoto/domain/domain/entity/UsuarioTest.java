package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UsuarioTest {

    @Test
    void permiteObtenerIdentificadorDeAmbosTiposDeUsuario() {
        assertEquals("c1", new Comprador("c1", "Ana", "ana@correo.com", "clave").getId());
        assertEquals("a1", new Administrador("a1", "Admin", "admin@correo.com", "clave").getId());
    }

    @Test
    void rechazaIdentificadorNuloVacioOEnBlanco() {
        for (String id : new String[]{null, "", " \t"}) {
            assertThrows(ReglaDominioException.class, () -> new Comprador(id, "Ana", "correo", "clave"));
            assertThrows(ReglaDominioException.class, () -> new Administrador(id, "Admin", "correo", "clave"));
        }
    }

    @Test
    void conservaIgualdadYHashCodePorIdentidadYTipo() {
        Comprador original = new Comprador("c1", "Ana", "correo", "clave");
        Comprador actualizado = new Comprador("c1", "Otro nombre", "otro correo", "otra clave");

        assertEquals(original, actualizado);
        assertEquals(original.hashCode(), actualizado.hashCode());
        assertNotEquals(original, new Comprador("c2", "Ana", "correo", "clave"));
        assertNotEquals(original, new Administrador("c1", "Ana", "correo", "clave"));
    }
}
