package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
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
@DiscriminatorValue("GUANTES")

public class GuantesEntity extends ProductoEntity {
    @Enumerated(EnumType.STRING)
    @Column(name="guantes_talla")
    private Talla talla;
    @Enumerated(EnumType.STRING)
    @Column(name="guantes_material")
    private Material material;
    @Enumerated(EnumType.STRING)
    @Column(name="guantes_proteccion")
    private Proteccion proteccion;
}
