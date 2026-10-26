package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.PublicarProductoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class PublicarProductoUseCaseTest {
    @Test
    void noDeberiaGuardarSiElProductoNoExiste() {
        // Arrange
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.obtenerPorId("no-existe")).thenReturn(Optional.empty());
        PublicarProductoUseCase useCase = new PublicarProductoUseCase(repository);

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("no-existe"));

        // Assert
        assertEquals("No existe un producto con ese id", error.getMessage());
        verify(repository, never()).guardar(any());
    }

    @Test
    void noDeberiaGuardarSiElProductoYaEstaPublicado() {
        // Arrange
        ProductoRepository repository = mock(ProductoRepository.class);
        Casco casco = Casco.crear("casco", new Marca("m", "AGV"), "K3", "Negro", Talla.M,
                new Precio(500000, "COP"), Proteccion.NIVEL_1, "DOT");
        casco.publicar();
        when(repository.obtenerPorId("casco")).thenReturn(Optional.of(casco));
        PublicarProductoUseCase useCase = new PublicarProductoUseCase(repository);

        // Act
        ReglaDominioException error = assertThrows(ReglaDominioException.class,
                () -> useCase.ejecutar("casco"));

        // Assert
        assertEquals("El producto ya esta publicado", error.getMessage());
        assertTrue(casco.estaPublicado());
        verify(repository, never()).guardar(any());
    }
}
