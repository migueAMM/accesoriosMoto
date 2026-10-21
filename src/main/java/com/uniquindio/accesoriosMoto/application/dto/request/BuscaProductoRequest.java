package com.uniquindio.accesoriosMoto.application.dto.request;

import com.uniquindio.accesoriosMoto.domain.valueObject.TipoCompatibilidad;
import jakarta.validation.constraints.PositiveOrZero;

public record BuscaProductoRequest (
  String marca,
  String color,
  @PositiveOrZero(message = "El precio minimo no puede ser negativo") Double precioMinimo,
  @PositiveOrZero(message = "El precio maximo no puede ser negativo") Double precioMaximo,
  String moneda,
  TipoCompatibilidad tipoCompatibilidad,
  String modeloCompatibilidad
){}
