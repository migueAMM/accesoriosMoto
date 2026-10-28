package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("IMPERMEABLE")

public class ImpermeableEntity extends ProductoEntity {
    @Enumerated(EnumType.STRING)
    @Column(name="impermeable_talla")
    private Talla talla;
    @Enumerated(EnumType.STRING)
    @Column(name="impermeable_material")
    private Material material;
}