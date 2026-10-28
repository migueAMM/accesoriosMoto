package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Rol;

public class Comprador extends Usuario{

    public Comprador(String id, String nombre, String correo, String contrasena) {
        super(id, nombre, correo, contrasena);
    }

    @Override
    public Rol getRol() {
        return Rol.COMPRADOR;
    }
}
