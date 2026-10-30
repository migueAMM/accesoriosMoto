package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.BusquedaProducto;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> obtenerPorId(String id);

    void guardar(Producto producto);

    List<Producto> buscar(FiltroProducto filtro);

    List<ProductoVentas> masVendidos(int limite);

    default Pagina<Producto> buscarPorTexto(BusquedaProducto busqueda, int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > 50 || (long) pagina * tamano > Integer.MAX_VALUE)
            throw new ReglaDominioException("Paginación inválida");
        Comparator<Producto> base = switch (busqueda.ordenarPor()) {
            case "precio" -> Comparator.comparingDouble(p -> p.getPrecio().monto());
            case "marca" -> Comparator.comparing(p -> p.getMarca().nombre(), String.CASE_INSENSITIVE_ORDER);
            case "color" -> Comparator.comparing(p -> p.getColor() == null ? "" : p.getColor(), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Producto::getId);
        };
        Comparator<Producto> orden = (busqueda.ascendente() ? base : base.reversed()).thenComparing(Producto::getId);
        List<Producto> todos = buscar(new FiltroProducto(null, null, null, null, null, null)).stream()
                .filter(busqueda::coincideCon).sorted(orden).toList();
        long inicio = (long) pagina * tamano;
        List<Producto> contenido = inicio >= todos.size() ? List.of()
                : todos.subList((int) inicio, (int) Math.min(inicio + tamano, todos.size()));
        return new Pagina<>(contenido, todos.size(), (int) ((todos.size() + (long) tamano - 1) / tamano), pagina, tamano);
    }
}
