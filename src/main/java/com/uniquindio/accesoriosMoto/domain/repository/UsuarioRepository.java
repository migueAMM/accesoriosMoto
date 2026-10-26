package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> obtenerPorCorreo(String correo);
    void guardar(Usuario usuario);
}
