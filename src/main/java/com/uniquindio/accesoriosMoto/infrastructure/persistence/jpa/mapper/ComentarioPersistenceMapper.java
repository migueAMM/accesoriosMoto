package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ComentarioEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ProductoEntity;
import org.springframework.stereotype.Component;

@Component
public class ComentarioPersistenceMapper {
    private final CompradorPersistenceMapper compradores;
    private final ProductoPersistenceMapper productos;
    public ComentarioPersistenceMapper(CompradorPersistenceMapper compradores, ProductoPersistenceMapper productos) {
        this.compradores = compradores;
        this.productos = productos;
    }
    public ComentarioEntity toEntity(Comentario comentario, CompradorEntity comprador, ProductoEntity producto) {
        ComentarioEntity entidad = new ComentarioEntity();
        entidad.setId(comentario.getId());
        entidad.setComprador(comprador);
        entidad.setProducto(producto);
        entidad.setCalificacion(comentario.getCalificacion());
        entidad.setTexto(comentario.getTexto());
        entidad.setRespuestaVendedor(comentario.getRespuestaVendedor());
        return entidad;
    }
    public Comentario toDomain(ComentarioEntity entidad) {
        return Comentario.reconstruir(entidad.getId(), compradores.toDomain(entidad.getComprador()),
                productos.toDomain(entidad.getProducto()), entidad.getCalificacion(), entidad.getTexto(),
                entidad.getRespuestaVendedor());
    }
}
