package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@AllArgsConstructor
public class PrecioEmbeddable {
    private double monto;
    private String moneda;
}
