package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.Pagina;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import com.uniquindio.accesoriosMoto.domain.valueObject.BusquedaProducto;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarProductoUseCase {

    private static final int TAMANO_MAXIMO = 50;
    private static final int RANKING_MAXIMO = 20;
    private final ProductoRepository repository;

    @Transactional(readOnly = true)
    public Pagina<Producto> buscar(FiltroProducto filtro, int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > TAMANO_MAXIMO || (long) pagina * tamano > Integer.MAX_VALUE)
            throw new ReglaDominioException("La página debe ser no negativa, el tamaño entre 1 y " + TAMANO_MAXIMO
                    + " y el desplazamiento no puede superar " + Integer.MAX_VALUE);
        return repository.buscar(filtro, pagina, tamano);
    }

    @Transactional(readOnly = true)
    public Producto obtener(String id) {
        return repository.obtenerPorId(id).filter(Producto::estaPublicado).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<ProductoVentas> masVendidos(int limite) {
        if (limite < 1 || limite > RANKING_MAXIMO)
            throw new ReglaDominioException("El límite debe estar entre 1 y " + RANKING_MAXIMO);
        return repository.masVendidos(limite);
    }

    //Paginacion
    @Transactional(readOnly = true)
    public Pagina<Producto> buscarPorTexto(BusquedaProducto busqueda, int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > TAMANO_MAXIMO || (long) pagina * tamano > Integer.MAX_VALUE)
            throw new ReglaDominioException("La página debe ser no negativa y el tamaño entre 1 y " + TAMANO_MAXIMO);
        return repository.buscarPorTexto(busqueda, pagina, tamano);
    }
}
