package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.Pagina;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.BaulEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ProductoEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.VisorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.ProductoPersistenceMapper;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class ProductoRepositoryJpa implements ProductoRepository {

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
        if (filtro == null) { throw new com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException("Debe proporcionar los criterios de búsqueda"); }
        return repository.findAll(criterios(filtro), org.springframework.data.domain.Sort.by("id")).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Pagina<Producto> buscar(FiltroProducto filtro, int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > 50 || (long) pagina * tamano > Integer.MAX_VALUE) throw new com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException("Paginación inválida");
        var resultado = repository.findAll(criterios(filtro), PageRequest.of(pagina, tamano, org.springframework.data.domain.Sort.by("id")));
        return new com.uniquindio.accesoriosMoto.domain.repository.Pagina<>(resultado.getContent().stream().map(mapper::toDomain).toList(), resultado.getTotalElements(), resultado.getTotalPages(), pagina, tamano);
    }

    @Override
    public List<ProductoVentas> masVendidos(int limite) {
        if (limite < 1 || limite > 20) throw new com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException("El límite debe estar entre 1 y 20");
        return repository.masVendidos(EstadoPedido.COMPLETADA, org.springframework.data.domain.PageRequest.of(0, limite));
    }    private static Specification<ProductoEntity> criterios(FiltroProducto filtro) {
        if (filtro == null) throw new ReglaDominioException("Debe proporcionar los criterios de búsqueda");
        return (producto, consulta, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            condiciones.add(cb.equal(producto.get("estadoPublicacion"), EstadoPublicacion.PUBLICADO));
            if (filtro.marca() != null) condiciones.add(cb.equal(normalizar(cb, producto.get("marca").get("nombre")), filtro.marca()));
            if (filtro.color() != null) condiciones.add(cb.equal(normalizar(cb, producto.get("color")), filtro.color()));
            if (filtro.moneda() != null) condiciones.add(cb.equal(normalizar(cb, producto.get("precio").get("moneda")), filtro.moneda()));
            if (filtro.precioMinimo() != null) condiciones.add(cb.ge(producto.get("precio").get("monto"), filtro.precioMinimo()));
            if (filtro.precioMaximo() != null) condiciones.add(cb.le(producto.get("precio").get("monto"), filtro.precioMaximo()));
            if (filtro.compatibilidad() != null) {
                Compatibilidad compatibilidad = filtro.compatibilidad();
                Class<? extends ProductoEntity> tipo = compatibilidad.tipo() == TipoCompatibilidad.MOTO ? BaulEntity.class : VisorEntity.class;
                Subquery<String> subconsulta = consulta.subquery(String.class);
                Root<? extends ProductoEntity> compatible = subconsulta.from(tipo);
                Join<?, ?> valor = compatible.join("compatibilidades");
                subconsulta.select(compatible.get("id")).where(
                        cb.equal(compatible.get("id"), producto.get("id")),
                        cb.equal(valor.get("tipo"), compatibilidad.tipo()),
                        cb.equal(valor.get("modelo"), compatibilidad.modelo()));
                condiciones.add(cb.exists(subconsulta));
            }
            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }

    private static Expression<String> normalizar(CriteriaBuilder cb, Expression<String> texto) {
        return cb.upper(cb.function("regexp_replace", String.class, texto,
                cb.literal("^[\\p{javaWhitespace}]+|[\\p{javaWhitespace}]+$"), cb.literal("")));
    }
}
