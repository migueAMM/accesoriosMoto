package com.uniquindio.accesoriosMoto.domain.infraestructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ProductoResponse;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.infraestructure.rest.DatosRestPrueba;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ProductoMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductoMapperTest {

    private final ProductoMapper mapper = new ProductoMapper();

    @Test
    void mapeaCamposYTiposDeLosOchoAccesorios() {
        // Arrange
        String[] tipos = {"Baul", "Casco", "Chaqueta", "Guantes", "Impermeable", "Intercomunicador", "SoporteCelular", "Visor"};
        var productos = java.util.Arrays.stream(tipos).map(tipo -> DatosRestPrueba.producto(tipo, "p1")).toList();

        // Act
        var respuestas = productos.stream().map(mapper::toResponse).toList();

        // Assert
        for (int indice = 0; indice < tipos.length; indice++) {
            assertEquals(new ProductoResponse("p1", tipos[indice], "Marca", "Negro", 100, "COP", "NO_PUBLICADO"), respuestas.get(indice));
            assertFalse(productos.get(indice).estaPublicado());
        }
    }

    @Test
    void reflejaPublicacionSinModificarProducto() {
        // Arrange
        Producto producto = DatosRestPrueba.producto("Casco", "p1");
        producto.publicar();

        // Act
        var resultado1 = mapper.toResponse(producto).estadoPublicacion();
        var resultado2 = producto.estaPublicado();
        producto.despublicar();
        var resultado3 = mapper.toResponse(producto).estadoPublicacion();

        // Assert
        assertEquals("PUBLICADO", resultado1);
        assertTrue(resultado2);
        assertEquals("NO_PUBLICADO", resultado3);
    }
}
