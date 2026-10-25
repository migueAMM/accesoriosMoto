package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="comentarios")
public class ComentarioEntity {
    @Id
    private String id;
    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="comprador_id")
    private CompradorEntity comprador;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="producto_id")
    private ProductoEntity producto;
    private int calificacion;
    @Lob private String texto;
    @Lob private String respuestaVendedor;
}
