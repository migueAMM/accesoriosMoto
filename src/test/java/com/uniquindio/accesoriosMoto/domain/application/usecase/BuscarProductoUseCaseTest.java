package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.BuscarProductoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.Pagina;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BuscarProductoUseCaseTest {

    @Test
    public void delegaCriteriosYPaginacionAlPuertoSinGuardarNiBuscarPorId() {
        var filtro = new FiltroProducto("AGV",null,null,null,null,null);
        Pagina<Producto> resultado = new Pagina<>(List.of(),0,0,0,5);
        var puerto = mock(ProductoRepository.class);
        when(puerto.buscar(filtro,0,5)).thenReturn(resultado);
        var caso = new BuscarProductoUseCase(puerto);

        var recibido = caso.buscar(filtro,0,5);

        assertSame(resultado, recibido);
        verify(puerto).buscar(same(filtro), eq(0), eq(5));
        verify(puerto, never()).guardar(any());
        verify(puerto, never()).obtenerPorId(anyString());
    }
    @Test
    public void consultaCatalogoEnMemoriaYReflejaDespublicacion() {
        var repository=new ProductoRepositoryEnMemoria();
        var publicado=Visor.crear("v1",new Marca("m","AGV"),new Precio(100,"COP"),"Negro","Integral",List.of("K-3"));
        var oculto=Visor.crear("v2",new Marca("m","AGV"),new Precio(100,"COP"),"Negro","Integral",List.of("K-3"));
        publicado.publicar();
        repository.guardar(publicado);
        repository.guardar(oculto);
        var caso=new BuscarProductoUseCase(repository);
        var filtro=new FiltroProducto("agv","NEGRO",100.0,200.0,"COP",new Compatibilidad(TipoCompatibilidad.CASCO,"K-3"));

        var resultado1 = caso.buscar(filtro,0,50).contenido();
        var resultado2 = caso.buscar(new FiltroProducto(null,null,null,null,null,null),0,50).contenido();
        var resultado3 = caso.buscar(new FiltroProducto("Givi",null,null,null,null,null),0,50).contenido().isEmpty();
        publicado.despublicar();
        var resultado4 = caso.buscar(filtro,0,50).contenido().isEmpty();

        assertEquals(List.of(publicado), resultado1);
        assertEquals(List.of(publicado), resultado2);
        assertTrue(resultado3);
        assertTrue(resultado4);
    }
    @Test
    public void catalogoVacioDevuelvePaginaVaciaYRechazaFiltroNulo() {
        var caso=new BuscarProductoUseCase(new ProductoRepositoryEnMemoria());

        var resultado1 = caso.buscar(new FiltroProducto(null,null,null,null,null,null),0,5).contenido().isEmpty();
        var accion2Error = catchThrowable(()->caso.buscar(null,0,5));

        assertTrue(resultado1);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
    }
}
