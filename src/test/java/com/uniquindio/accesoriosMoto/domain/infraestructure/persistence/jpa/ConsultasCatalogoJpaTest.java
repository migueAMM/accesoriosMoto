package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.application.usecase.BuscarProductoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ConsultasCatalogoJpaTest {
    @Autowired
    ProductoRepository productos;
    @Autowired
    PedidoRepository pedidos;
    @Autowired
    CompradorRepository compradores;
    @Autowired
    EntityManager em;
    @Autowired
    BuscarProductoUseCase buscar;

    private Visor visor(String id, double monto, String moneda) {
        return Visor.crear(id, new Marca("m", "\u2003agv\t"), new Precio(monto, moneda), "\u2003negro\t", "Integral", List.of("K-3"));
    }
    private void guardar(Producto p) {
        p.publicar(); productos.guardar(p);
    }

    private List<String> ids(List<Producto> lista) {
        return lista.stream().map(Producto::getId).sorted().toList();
    }

    @Test
    public void filtrosJpaCoincidenConDominioIncluyendoNormalizacionYCompatibilidad() {
        var memoria = new ProductoRepositoryEnMemoria();
        List<Producto> datos = List.of(visor("f-a",100," cop "), visor("f-b",200,"COP"), visor("f-c",200,"USD"), visor("f-oculto",150,"COP"), Baul.crear("f-baul",new Marca("m","AGV"),Material.PLASTICO,30,"30x30",new Precio(150,"COP"),10,"Negro"));
        ((Baul)datos.get(4)).indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO,"MT-09"));
        for (Producto p : datos) {
            if (!p.getId().equals("f-oculto")) p.publicar(); productos.guardar(p); memoria.guardar(p);
        }

        var filtros = List.of(
                new FiltroProducto(null,null,null,null,null,null),
                new FiltroProducto(" agv "," NEGRO ",100.0,200.0,"cop",null),
                new FiltroProducto(null,null,200.0,200.0,"COP",null),
                new FiltroProducto(null,null,null,null,null,new Compatibilidad(TipoCompatibilidad.CASCO,"k-3")),
                new FiltroProducto(null,null,null,null,null,new Compatibilidad(TipoCompatibilidad.MOTO,"mt-09")),
                new FiltroProducto("inexistente",null,null,null,null,null));

        em.flush();
        em.clear();
        var resultadosMemoria = filtros.stream().map(filtro -> ids(memoria.buscar(filtro))).toList();
        var resultadosJpa = filtros.stream().map(filtro -> ids(productos.buscar(filtro))).toList();

        for (int indice = 0; indice < filtros.size(); indice++) {
            assertEquals(resultadosMemoria.get(indice), resultadosJpa.get(indice), filtros.get(indice).toString());
        }
    }

    @Test
    public void paginacionCuentaYOrdenaSinDuplicadosYNoExponeOcultos() {
        guardar(visor("pagina-c",100,"COP"));
        guardar(visor("pagina-a",100,"COP"));
        guardar(visor("pagina-b",100,"COP"));
        productos.guardar(visor("pagina-oculto",100,"COP"));
        var filtro = new FiltroProducto(null,null,null,null,null,null);

        var primera = buscar.buscar(filtro,0,2);
        var resultado1 = ids(primera.contenido());

        var resultado4 = ids(buscar.buscar(filtro,1,2).contenido());
        var resultado5 = buscar.buscar(filtro,100,2).contenido().isEmpty();
        var accion6Error = catchThrowable(()->buscar.buscar(filtro,Integer.MAX_VALUE,50));
        var accion7Error = catchThrowable(()->buscar.buscar(filtro,-1,2));
        var accion8Error = catchThrowable(()->buscar.buscar(filtro,0,51));

        assertEquals(List.of("pagina-a","pagina-b"), resultado1);
        assertEquals(3, primera.totalElementos());
        assertEquals(2, primera.totalPaginas());
        assertEquals(List.of("pagina-c"), resultado4);
        assertTrue(resultado5);
        assertInstanceOf(ReglaDominioException.class, accion6Error);
        assertInstanceOf(ReglaDominioException.class, accion7Error);
        assertInstanceOf(ReglaDominioException.class, accion8Error);
    }

    @Test
    public void consultaIndividualSoloDevuelvePublicados() {
        guardar(visor("visible",100,"COP"));
        productos.guardar(visor("oculto",100,"COP"));

        var resultado1 = buscar.obtener("visible").getId();
        var accion2Error = catchThrowable(()->buscar.obtener("oculto"));
        var accion3Error = catchThrowable(()->buscar.obtener("ausente"));

        assertEquals("visible", resultado1);
        assertInstanceOf(NoSuchElementException.class, accion2Error);
        assertInstanceOf(NoSuchElementException.class, accion3Error);
    }

    @Test
    public void rankingDelPuertoTieneLimiteDesempateYExcluyeEstadosSinVenta() {
        var comprador = new Comprador("ranking-u","Ana","ranking@correo.com","hash"); compradores.guardar(comprador);
        Producto a=visor("ranking-a",100,"COP"), b=visor("ranking-b",100,"COP");
        productos.guardar(a); productos.guardar(b);
        List<Pedido> fuente = new ArrayList<>();

        for (String id : List.of("a","b","pendiente","reembolso","cancelado")) {
            Producto p = id.equals("a") ? a : b;
            Pedido pedido = Pedido.registrar(id,comprador,List.of(ItemPedido.crear("item-"+id,p,id.length()==1?2:100)), LocalDateTime.now());
            if (!id.equals("pendiente")) pedido.completar();
            if (id.equals("reembolso")) pedido.solicitarReembolso("Motivo");
            if (id.equals("cancelado")) pedido=Pedido.reconstruir(pedido.getId(),comprador,pedido.getItems(), EstadoPedido.CANCELADO,pedido.getFecha(),pedido.getTotal(),null);
            pedidos.registrar(pedido); fuente.add(pedido);
        }

        var esperado = List.of(new ProductoVentas("ranking-a",2),new ProductoVentas("ranking-b",2));

        em.flush();
        em.clear();
        var ranking = buscar.masVendidos(20);
        var limitado = buscar.masVendidos(1);
        var memoria = new ProductoRepositoryEnMemoria(() -> fuente).masVendidos(20);
        var ceroError = catchThrowable(() -> buscar.masVendidos(0));
        var excesivoError = catchThrowable(() -> buscar.masVendidos(21));

        assertEquals(esperado, ranking);
        assertEquals(esperado.subList(0,1), limitado);
        assertEquals(esperado, memoria);
        assertInstanceOf(ReglaDominioException.class, ceroError);
        assertInstanceOf(ReglaDominioException.class, excesivoError);
    }
}
