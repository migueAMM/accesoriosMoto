package com.uniquindio.accesoriosMoto.infrastructure.persistence;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComentarioRepositoryEnMemoria implements ComentarioRepository {
    private final Map<String, Comentario> comentarios = new HashMap<>();

    @Override
    public void guardar(Comentario comentario){
        comentarios.put(comentario.getId(), comentario);
    }

    @Override
    public Optional<Comentario> obtenerPorID(String id){
        return Optional.ofNullable(comentarios.get(id));
    }
}
