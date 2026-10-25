package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompradorJpaDataRepository extends JpaRepository<CompradorEntity, String> {
}
