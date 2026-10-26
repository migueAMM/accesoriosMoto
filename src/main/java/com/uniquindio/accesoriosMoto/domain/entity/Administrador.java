package com.uniquindio.accesoriosMoto.domain.entity;

public class Administrador extends Usuario{

    public Administrador(String id, String nombre, String correo, String contrasena) {
        super(id, nombre, correo, contrasena);
    }

    @Override
    public com.uniquindio.accesoriosMoto.domain.valueObject.Rol getRol() {
        return com.uniquindio.accesoriosMoto.domain.valueObject.Rol.ADMINISTRADOR;
    }
}
