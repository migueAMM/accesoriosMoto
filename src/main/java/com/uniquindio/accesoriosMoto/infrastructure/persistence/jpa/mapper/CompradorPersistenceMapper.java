package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import org.springframework.stereotype.Component;

@Component
public class CompradorPersistenceMapper {
    public CompradorEntity toEntity(Comprador comprador) {
        CompradorEntity entidad = new CompradorEntity();
        entidad.setId(comprador.getId());
        entidad.setNombre(comprador.getNombre());
        entidad.setCorreo(comprador.getCorreo());
        entidad.setContrasena(comprador.getContrasena());
        return entidad;
    }
    public Comprador toDomain(CompradorEntity entidad) {
        return new Comprador(entidad.getId(), entidad.getNombre(), entidad.getCorreo(), entidad.getContrasena());
    }
}
