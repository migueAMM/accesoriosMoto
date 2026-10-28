package com.uniquindio.accesoriosMoto.domain.repository;

import java.util.List;

public record Pagina<T>(List<T> contenido, long totalElementos, int totalPaginas, int numero, int tamano) {
    public Pagina {
        contenido = List.copyOf(contenido);
    }
}
