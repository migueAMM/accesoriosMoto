package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="compradores")
public class CompradorEntity extends UsuarioEntity {
    @Id
    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
}
