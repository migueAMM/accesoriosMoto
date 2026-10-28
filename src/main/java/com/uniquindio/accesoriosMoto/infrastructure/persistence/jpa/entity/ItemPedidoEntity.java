package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="items_pedido")

public class ItemPedidoEntity {
    @Id
    private String id;
    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="pedido_id")
    private PedidoEntity pedido;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="producto_id")
    private ProductoEntity producto;
    private int cantidad;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="monto", column=@Column(name="subtotal_monto")), @AttributeOverride(name="moneda", column=@Column(name="subtotal_moneda"))})
    private PrecioEmbeddable subtotal;
}
