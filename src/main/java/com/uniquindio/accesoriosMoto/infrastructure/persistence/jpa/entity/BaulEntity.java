package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor
@Entity
@DiscriminatorValue("BAUL")
public class BaulEntity extends ProductoEntity {
    @Enumerated(EnumType.STRING)
    @Column(name="baul_material")
    private Material material;
    @Column(name="baul_capacidad_litros")
    private double capacidadLitros;
    @Column(name="baul_dimensiones")
    private String dimensiones;
    @Column(name="baul_capacidad_maxima_carga")
    private double capacidadMaximaCarga;
    @ElementCollection
    @CollectionTable(name="baul_compatibilidades", joinColumns=@JoinColumn(name="producto_id"))
    private Set<CompatibilidadEmbeddable> compatibilidades = new LinkedHashSet<>();
}
