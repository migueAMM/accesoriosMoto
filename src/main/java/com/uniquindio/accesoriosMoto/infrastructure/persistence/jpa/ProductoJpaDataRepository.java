package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoJpaDataRepository extends JpaRepository<ProductoEntity, String> {
}
