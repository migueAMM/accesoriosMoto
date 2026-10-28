package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="pedidos")

public class PedidoEntity {
    @Id
    private String id;
    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="comprador_id")
    private CompradorEntity comprador;
    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoPedido;
    private LocalDateTime fecha;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="monto", column=@Column(name="total_monto")), @AttributeOverride(name="moneda", column=@Column(name="total_moneda"))})
    private PrecioEmbeddable total;
    @Lob private String motivoReembolso;
    @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderColumn(name="posicion")
    private List<ItemPedidoEntity> items = new ArrayList<>();
}
