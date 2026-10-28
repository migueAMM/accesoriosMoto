package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("VISOR")

public class VisorEntity extends ProductoEntity {
    @Column(name="visor_tipo_visor")
    private String tipoVisor;
    @ElementCollection
    @CollectionTable(name="visor_compatibilidades", joinColumns=@JoinColumn(name="producto_id"))
    private Set<CompatibilidadEmbeddable> compatibilidades = new LinkedHashSet<>();
}
