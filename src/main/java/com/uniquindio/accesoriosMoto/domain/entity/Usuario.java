package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Rol;
import lombok.Getter;

import java.util.Objects;

public abstract class Usuario {

    @Getter
    protected final String id;
    @Getter
    protected String nombre;
    @Getter
    protected String correo;
    @Getter
    protected String contrasena;

    public Usuario(String id, String nombre, String correo, String contrasena) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("El usuario debe tener un identificador válido");
        }

        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new ReglaDominioException("Nombre, correo y contraseña son obligatorios");
        }

        this.id = id;
        this.nombre = nombre;
        this.correo = correo.strip().toLowerCase(java.util.Locale.ROOT);
        this.contrasena = contrasena;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract Rol getRol();
}
