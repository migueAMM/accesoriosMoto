package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.CompradorRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompradorRepositoryEnMemoriaTest {

    @Test
    void guardaYRecuperaCompradoresPorSuId() {
        CompradorRepositoryEnMemoria repository = new CompradorRepositoryEnMemoria();
        Comprador primero = new Comprador("u1", "Ana", "ana@correo.com", "clave");
        Comprador segundo = new Comprador("u2", "Luis", "luis@correo.com", "clave");

        repository.guardar(primero);
        repository.guardar(segundo);

        assertSame(primero, repository.obtenerPorId("u1").orElseThrow());
        assertSame(segundo, repository.obtenerPorId("u2").orElseThrow());
    }

    @Test
    void devuelveVacioSiNoExisteComprador() {
        CompradorRepositoryEnMemoria repository = new CompradorRepositoryEnMemoria();

        assertTrue(repository.obtenerPorId("inexistente").isEmpty());
    }

    @Test
    void reemplazaCompradorAlGuardarElMismoId() {
        CompradorRepositoryEnMemoria repository = new CompradorRepositoryEnMemoria();
        Comprador original = new Comprador("u1", "Ana", "ana@correo.com", "clave");
        Comprador actualizado = new Comprador("u1", "Ana actualizada", "nuevo@correo.com", "otra clave");
        repository.guardar(original);

        repository.guardar(actualizado);

        assertSame(actualizado, repository.obtenerPorId("u1").orElseThrow());
    }
}
