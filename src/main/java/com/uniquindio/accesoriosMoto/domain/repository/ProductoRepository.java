package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> obtenerPorId(String id);

    void guardar(Producto producto);

    List<Producto> buscar(FiltroProducto filtro);

    List<ProductoVentas> masVendidos(int limite);

    default Pagina<Producto> buscar(FiltroProducto filtro, int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > 50 || (long) pagina * tamano > Integer.MAX_VALUE)
            throw new ReglaDominioException("Paginación inválida");
        List<Producto> productos = buscar(filtro).stream().sorted(Comparator.comparing(Producto::getId)).toList();
        long inicio = (long) pagina * tamano;
        List<Producto> contenido = inicio >= productos.size() ? List.of() :
                productos.subList((int) inicio, (int) Math.min(inicio + tamano, productos.size()));
        return new Pagina<>(contenido, productos.size(), (int) ((productos.size() + (long) tamano - 1) / tamano), pagina, tamano);
    }
}
