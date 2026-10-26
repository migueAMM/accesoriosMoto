package com.uniquindio.accesoriosMoto.domain.entity;

public class Comprador extends Usuario{

    public Comprador(String id, String nombre, String correo, String contrasena) {
        super(id, nombre, correo, contrasena);
    }

    @Override
    public com.uniquindio.accesoriosMoto.domain.valueObject.Rol getRol() {
        return com.uniquindio.accesoriosMoto.domain.valueObject.Rol.COMPRADOR;
    }
}
