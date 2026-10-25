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
@DiscriminatorValue("INTERCOMUNICADOR")
public class IntercomunicadorEntity extends ProductoEntity {
    @Column(name="intercomunicador_alcance_metros")
    private int alcanceMetros;
    @Column(name="intercomunicador_autonomia_horas")
    private int autonomiaHoras;
    @Column(name="intercomunicador_bluetooth")
    private boolean bluetooth;
}