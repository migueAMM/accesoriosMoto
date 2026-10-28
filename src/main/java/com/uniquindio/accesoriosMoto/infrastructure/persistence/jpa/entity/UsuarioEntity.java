package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="usuarios", uniqueConstraints=@UniqueConstraint(name="uk_usuarios_correo", columnNames="correo"))
@Inheritance(strategy= InheritanceType.JOINED)
@DiscriminatorColumn(name="tipo_usuario")

public abstract class UsuarioEntity {
    @Id private String id;
    @Column(nullable=false) private String nombre;
    @Column(nullable=false) private String correo;
    @Column(nullable=false) private String contrasena;
    @Transient
    public abstract RolSeguridad getRol();
}
