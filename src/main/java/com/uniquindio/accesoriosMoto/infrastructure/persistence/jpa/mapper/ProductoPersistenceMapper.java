package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.valueObject.Compatibilidad;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.*;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Component
public class ProductoPersistenceMapper {
    public ProductoEntity toEntity(Producto producto) {
        ProductoEntity entidad = switch (producto) {
            case Baul p -> {
                BaulEntity e = new BaulEntity();
                e.setMaterial(p.getMaterial());
                e.setCapacidadLitros(p.getCapacidadLitros());
                e.setDimensiones(p.getDimensiones());
                e.setCapacidadMaximaCarga(p.getCapacidadMaximaCarga());
                e.setCompatibilidades(p.getCompatibilidades().stream().map(c -> new CompatibilidadEmbeddable(c.tipo(), c.modelo())).collect(Collectors.toCollection(LinkedHashSet::new)));
                yield e;
            }
            case Casco p -> {
                CascoEntity e = new CascoEntity();
                e.setModelo(p.getModelo());
                e.setTalla(p.getTalla());
                e.setProteccion(p.getProteccion());
                e.setCertificacionSeguridad(p.getCertificacionSeguridad());
                yield e;
            }
            case Chaqueta p -> {
                ChaquetaEntity e = new ChaquetaEntity();
                e.setTalla(p.getTalla());
                e.setMaterial(p.getMaterial());
                e.setProteccion(p.getProteccion());
                yield e;
            }
            case Guantes p -> {
                GuantesEntity e = new GuantesEntity();
                e.setTalla(p.getTalla());
                e.setMaterial(p.getMaterial());
                e.setProteccion(p.getProteccion());
                yield e;
            }
            case Impermeable p -> {
                ImpermeableEntity e = new ImpermeableEntity();
                e.setTalla(p.getTalla());
                e.setMaterial(p.getMaterial());
                yield e;
            }
            case Intercomunicador p -> {
                IntercomunicadorEntity e = new IntercomunicadorEntity();
                e.setAlcanceMetros(p.getAlcanceMetros());
                e.setAutonomiaHoras(p.getAutonomiaHoras());
                e.setBluetooth(p.isBluetooth());
                yield e;
            }
            case SoporteCelular p -> {
                SoporteCelularEntity e = new SoporteCelularEntity();
                e.setTipoSujecion(p.getTipoSujecion());
                e.setCompatibleTamanoCelular(p.getCompatibleTamanoCelular());
                yield e;
            }
            case Visor p -> {
                VisorEntity e = new VisorEntity();
                e.setTipoVisor(p.getTipoVisor());
                e.setCompatibilidades(p.getCompatibilidades().stream().map(c -> new CompatibilidadEmbeddable(c.tipo(), c.modelo())).collect(Collectors.toCollection(LinkedHashSet::new)));
                yield e;
            }
            default -> throw new IllegalArgumentException("Tipo de producto no soportado: " + producto.getClass().getName());
        };
        entidad.setId(producto.getId());
        entidad.setMarca(new MarcaEmbeddable(producto.getMarca().id(), producto.getMarca().nombre()));
        entidad.setPrecio(new PrecioEmbeddable(producto.getPrecio().monto(), producto.getPrecio().moneda()));
        entidad.setColor(producto.getColor());
        entidad.setEstadoPublicacion(producto.getEstadoPublicacion());
        return entidad;
    }

    public Producto toDomain(ProductoEntity entidad) {
        // Hibernate devuelve proxies para las relaciones polimórficas.
        ProductoEntity e = (ProductoEntity) org.hibernate.Hibernate.unproxy(entidad);
        Marca marca = new Marca(e.getMarca().getId(), e.getMarca().getNombre());
        Precio precio = new Precio(e.getPrecio().getMonto(), e.getPrecio().getMoneda());
        return switch (e) {
            case BaulEntity p -> Baul.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getMaterial(), p.getCapacidadLitros(), p.getDimensiones(), p.getCapacidadMaximaCarga(), p.getCompatibilidades().stream().map(c -> new Compatibilidad(c.getTipo(), c.getModelo())).collect(Collectors.toSet()));
            case CascoEntity p -> Casco.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getModelo(), p.getTalla(), p.getProteccion(), p.getCertificacionSeguridad());
            case ChaquetaEntity p -> Chaqueta.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getTalla(), p.getMaterial(), p.getProteccion());
            case GuantesEntity p -> Guantes.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getTalla(), p.getMaterial(), p.getProteccion());
            case ImpermeableEntity p -> Impermeable.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getTalla(), p.getMaterial());
            case IntercomunicadorEntity p -> Intercomunicador.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getAlcanceMetros(), p.getAutonomiaHoras(), p.isBluetooth());
            case SoporteCelularEntity p -> SoporteCelular.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getTipoSujecion(), p.getCompatibleTamanoCelular());
            case VisorEntity p -> Visor.reconstruir(e.getId(), marca, precio, e.getColor(), e.getEstadoPublicacion(), p.getTipoVisor(), p.getCompatibilidades().stream().map(c -> new Compatibilidad(c.getTipo(), c.getModelo())).collect(Collectors.toSet()));
            default -> throw new IllegalArgumentException("Tipo persistido no soportado: " + e.getClass().getName());
        };
    }
}
