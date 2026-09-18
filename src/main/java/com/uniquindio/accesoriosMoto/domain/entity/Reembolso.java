package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoReembolso;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reembolso {

    private final String id;
    private Pedido pedido;
    private String motivo;
    private EstadoReembolso estadoReembolso;
    private LocalDateTime fecha;

    public Reembolso(String id, Pedido pedido, String motivo, EstadoReembolso estadoReembolso, LocalDateTime fecha) {
        this.id = id;
        this.pedido = pedido;
        this.motivo = motivo;
        this.estadoReembolso = estadoReembolso;
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reembolso)) return false;
        return id.equals(((Reembolso) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
