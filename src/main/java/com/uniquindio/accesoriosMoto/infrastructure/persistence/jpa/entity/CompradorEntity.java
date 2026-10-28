package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="compradores")
@DiscriminatorValue("COMPRADOR")

public class CompradorEntity extends UsuarioEntity {

    @Override
    public RolSeguridad getRol(){
        return RolSeguridad.COMPRADOR;
    }
}
