package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class ComentarioRepositoryJpa {
    private final ComentarioJpaDataRepository repository;
    private final ComentarioPersistenceMapper mapper;
    private final CompradorJpaDataRepository compradores;
    private final ProductoJpaDataRepository productos;

    public ComentarioRepositoryJpa(ComentarioJpaDataRepository repository, ComentarioPersistenceMapper mapper, CompradorJpaDataRepository compradores, ProductoJpaDataRepository productos) {
        this.repository = repository;
        this.mapper = mapper;
        this.compradores = compradores;
        this.productos = productos;
    }

    @Override
    public Optional<Comentario> obtenerPorId(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void guardar(Comentario objeto) {
        repository.save(mapper.toEntity(objeto, compradores.getReferenceById(objeto.getComprador().getId()),
                productos.getReferenceById(objeto.getProducto().getId())));
    }
}
