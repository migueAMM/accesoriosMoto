package com.uniquindio.accesoriosMoto.application.dto.response;

public record ProductoResponse(
   String id,
   String tipo,
   String marca,
   String color,
   double precioMonto,
   String precioMoneda,
   String estadoPublicacion
) {}
