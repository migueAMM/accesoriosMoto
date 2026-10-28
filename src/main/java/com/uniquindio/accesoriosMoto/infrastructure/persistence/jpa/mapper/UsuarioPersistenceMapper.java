package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper;

import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.AdministradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceMapper {

    private final CompradorPersistenceMapper compradores;

    public UsuarioPersistenceMapper(CompradorPersistenceMapper compradores) {
        this.compradores = compradores;
    }

    public UsuarioEntity toEntity(Usuario usuario) {
        if (usuario instanceof Comprador comprador) return compradores.toEntity(comprador);
        if (!(usuario instanceof Administrador)) throw new IllegalArgumentException("Tipo de usuario no soportado");
        AdministradorEntity entidad = new AdministradorEntity();
        entidad.setId(usuario.getId());
        entidad.setNombre(usuario.getNombre());
        entidad.setCorreo(usuario.getCorreo());
        entidad.setContrasena(usuario.getContrasena());
        return entidad;
    }

    public Usuario toDomain(UsuarioEntity original) {
        UsuarioEntity entidad = (UsuarioEntity) org.hibernate.Hibernate.unproxy(original);
        if (entidad instanceof CompradorEntity comprador) return compradores.toDomain(comprador);
        if (entidad instanceof AdministradorEntity) {
            return new Administrador(entidad.getId(), entidad.getNombre(), entidad.getCorreo(), entidad.getContrasena());
        }
        throw new IllegalArgumentException("Tipo de usuario persistido no soportado");
    }
}
