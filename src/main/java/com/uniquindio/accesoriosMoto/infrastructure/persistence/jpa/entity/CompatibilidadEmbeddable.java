package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.TipoCompatibilidad;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@AllArgsConstructor
@EqualsAndHashCode
public class CompatibilidadEmbeddable {
    @Enumerated(EnumType.STRING)
    private TipoCompatibilidad tipo;
    private String modelo;
}
