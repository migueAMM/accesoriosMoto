package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

import java.util.Objects;

public abstract class Usuario {
    protected final String id;
    protected String nombre;
    protected String correo;
    protected String contrasena;

    public Usuario(String id, String nombre, String correo, String contrasena) {
        if (id == null || id.isBlank()){
            throw new ReglaDominioException("El usuario debe tener un identificador valido");
        }
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
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
}
