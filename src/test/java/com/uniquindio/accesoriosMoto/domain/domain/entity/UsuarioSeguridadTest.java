package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Rol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioSeguridadTest {
    @Test
    void rolesSonDerivadosYCorreoEsCanonico() {
        Comprador comprador = new Comprador("c1", "Ana", " ANA@CORREO.COM ", "hash");
        assertEquals("ana@correo.com", comprador.getCorreo());
        assertEquals(Rol.COMPRADOR, comprador.getRol());
        assertEquals(Rol.ADMINISTRADOR, new Administrador("a1", "Admin", "admin@correo.com", "hash").getRol());
    }
    @Test void camposObligatoriosSonInvariantesDelUsuario() {
        for (String invalido : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> new Comprador("c", invalido, "correo", "hash"));
            assertThrows(ReglaDominioException.class, () -> new Comprador("c", "Ana", invalido, "hash"));
            assertThrows(ReglaDominioException.class, () -> new Administrador("a", "Admin", "correo", invalido));
        }
    }
}
