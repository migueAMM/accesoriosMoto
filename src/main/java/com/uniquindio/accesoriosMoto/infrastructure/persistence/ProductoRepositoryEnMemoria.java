package com.uniquindio.accesoriosMoto.infrastructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.*;
import java.util.function.Supplier;

public class ProductoRepositoryEnMemoria implements ProductoRepository{

    private final Map<String, Producto> productos = new HashMap<>();
    private final Supplier<List<Pedido>> pedidos;

    public ProductoRepositoryEnMemoria() { this(List::of); }

    public ProductoRepositoryEnMemoria(Supplier<List<Pedido>> pedidos) { this.pedidos = Objects.requireNonNull(pedidos); }

    @Override
    public void guardar(Producto producto) {
        productos.put(producto.getId(), producto);
    }

    @Override
    public Optional<Producto> obtenerPorId(String id) {
        return Optional.ofNullable(productos.get(id));
    }

    @Override
    public List<Producto> buscar(FiltroProducto filtro){
        if(filtro == null){
            throw new ReglaDominioException("Debe proporcionar los criterios de búsqueda");
        }
        return productos.values().stream().filter(filtro::coincideCon).toList();
    }

    public List<ProductoVentas> masVendidos(int limite) {
        if (limite < 1 || limite > 20) throw new ReglaDominioException("El límite debe estar entre 1 y 20");
        Map<String, Long> cantidades = new HashMap<>();
        pedidos.get().stream().filter(p -> p.getEstadoPedido() == EstadoPedido.COMPLETADA)
                .flatMap(p -> p.getItems().stream())
                .forEach(i -> cantidades.merge(i.getProducto().getId(), (long) i.getCantidad(), Long::sum));
        return cantidades.entrySet().stream().map(e -> new ProductoVentas(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ProductoVentas::cantidadVendida).reversed().thenComparing(ProductoVentas::productoId))
                .limit(limite).toList();
    }
}
