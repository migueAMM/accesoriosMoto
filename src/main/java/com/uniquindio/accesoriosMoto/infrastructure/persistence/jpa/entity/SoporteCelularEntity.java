package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("SOPORTE_CELULAR")

public class SoporteCelularEntity extends ProductoEntity {
    @Column(name="soportecelular_tipo_sujecion")
    private String tipoSujecion;
    @Column(name="soportecelular_compatible_tamano_celular")
    private String compatibleTamanoCelular;
}
