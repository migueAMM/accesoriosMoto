package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.CompradorRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompradorRepositoryEnMemoriaTest {

    @Test
    public void guardaYRecuperaCompradoresPorSuId() {
        CompradorRepositoryEnMemoria repository = new CompradorRepositoryEnMemoria();
        Comprador primero = new Comprador("u1", "Ana", "ana@gmail.com", "clave");
        Comprador segundo = new Comprador("u2", "Luis", "luis@gmail.com", "clave");

        repository.guardar(primero);
        repository.guardar(segundo);
        var resultado1 = repository.obtenerPorId("u1").orElseThrow();
        var resultado2 = repository.obtenerPorId("u2").orElseThrow();

        assertSame(primero, resultado1);
        assertSame(segundo, resultado2);
    }

    @Test
    public void devuelveVacioSiNoExisteComprador() {
        CompradorRepositoryEnMemoria repository = new CompradorRepositoryEnMemoria();

        var resultado1 = repository.obtenerPorId("inexistente").isEmpty();

        assertTrue(resultado1);
    }

    @Test
    public void reemplazaCompradorAlGuardarElMismoId() {
        CompradorRepositoryEnMemoria repository = new CompradorRepositoryEnMemoria();
        Comprador original = new Comprador("u1", "Ana", "ana@gmail.com", "clave");
        Comprador actualizado = new Comprador("u1", "Ana actualizada", "nuevo@correo.com", "otra clave");
        repository.guardar(original);

        repository.guardar(actualizado);
        var resultado1 = repository.obtenerPorId("u1").orElseThrow();

        assertSame(actualizado, resultado1);
    }
}
