package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class PedidoRepositoryJPA {
    private final PedidoJpaDataRepository repository;
    private final PedidoPersistenceMapper mapper;
    private final CompradorJpaDataRepository compradores;
    private final ProductoJpaDataRepository productos;

    public PedidoRepositoryJpa(PedidoJpaDataRepository repository, PedidoPersistenceMapper mapper, CompradorJpaDataRepository compradores, ProductoJpaDataRepository productos) {
        this.repository = repository;
        this.mapper = mapper;
        this.compradores = compradores;
        this.productos = productos;
    }

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void registrar(Pedido objeto) {
        Map<String, ProductoEntity> referencias = new HashMap<>();
        for (ItemPedido item : objeto.getItems()) {
            referencias.computeIfAbsent(item.getProducto().getId(), productos::getReferenceById);
        }
        repository.save(mapper.toEntity(objeto, compradores.getReferenceById(objeto.getComprador().getId()), referencias));
    }
}
