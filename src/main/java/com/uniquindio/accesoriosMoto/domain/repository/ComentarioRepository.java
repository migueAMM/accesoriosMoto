package com.uniquindio.accesoriosMoto.domain.repository;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;

import java.util.Optional;

public interface ComentarioRepository {

    void guardar(Comentario comentario);
    Optional<Comentario> obtenerPorId(String id);
}
