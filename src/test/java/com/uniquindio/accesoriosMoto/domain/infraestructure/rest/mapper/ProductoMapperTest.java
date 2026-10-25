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
        for (String tipo : new String[]{"Baul", "Casco", "Chaqueta", "Guantes", "Impermeable", "Intercomunicador", "SoporteCelular", "Visor"}) {
            Producto producto = DatosRestPrueba.producto(tipo, "p1");

            ProductoResponse response = mapper.toResponse(producto);

            assertEquals(new ProductoResponse("p1", tipo, "Marca", "Negro", 100, "COP", "NO_PUBLICADO"), response);
            assertFalse(producto.estaPublicado());
        }
    }

    @Test
    void reflejaPublicacionSinModificarProducto() {
        Producto producto = DatosRestPrueba.producto("Casco", "p1");
        producto.publicar();
        assertEquals("PUBLICADO", mapper.toResponse(producto).estadoPublicacion());
        assertTrue(producto.estaPublicado());
        producto.despublicar();
        assertEquals("NO_PUBLICADO", mapper.toResponse(producto).estadoPublicacion());
    }
}
