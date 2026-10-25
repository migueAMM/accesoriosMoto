package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.ProductoPersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class ProductoRepositoryJpa {
    private final ProductoJpaDataRepository repository;
    private final ProductoPersistenceMapper mapper;

    public ProductoRepositoryJpa(ProductoJpaDataRepository repository, ProductoPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Producto> obtenerPorId(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void guardar(Producto objeto) {
        repository.save(mapper.toEntity(objeto));
    }

    @Override
    public List<Producto> buscar(FiltroProducto filtro) {
        // Conserva la semántica de normalización y coincidencia definida por el dominio.
        if (filtro == null) { throw new com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException("Debe proporcionar los criterios de búsqueda"); }
        return repository.findAll().stream().map(mapper::toDomain).filter(filtro::coincideCon).toList();
    }
}
