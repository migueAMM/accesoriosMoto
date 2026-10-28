package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("CASCO")

public class CascoEntity extends ProductoEntity {
    @Column(name="casco_modelo")
    private String modelo;
    @Enumerated(EnumType.STRING)
    @Column(name="casco_talla")
    private Talla talla;
    @Enumerated(EnumType.STRING)
    @Column(name="casco_proteccion")
    private Proteccion proteccion;
    @Column(name="casco_certificacion_seguridad")
    private String certificacionSeguridad;
}
