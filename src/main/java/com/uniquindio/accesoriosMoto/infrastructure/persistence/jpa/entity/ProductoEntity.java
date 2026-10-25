package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="productos")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipo_producto")
public abstract class ProductoEntity {
    @Id private String id;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="id", column=@Column(name="marca_id")), @AttributeOverride(name="nombre", column=@Column(name="marca_nombre"))})
    private MarcaEmbeddable marca;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="monto", column=@Column(name="precio_monto")), @AttributeOverride(name="moneda", column=@Column(name="precio_moneda"))})
    private PrecioEmbeddable precio;
    private String color;
    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoPublicacion;
}
