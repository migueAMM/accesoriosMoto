package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.CompradorPersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CompradorRepositoryJpa implements CompradorRepository {

    private final CompradorJpaDataRepository repository;
    private final CompradorPersistenceMapper mapper;

    public CompradorRepositoryJpa(CompradorJpaDataRepository repository, CompradorPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Comprador> obtenerPorId(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void guardar(Comprador objeto) {
        repository.save(mapper.toEntity(objeto));
    }
}
