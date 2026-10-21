package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Pedido {

    private final String id;
    private Comprador comprador;
    private List<ItemPedido> items;
    private EstadoPedido estadoPedido;
    private LocalDateTime fecha;
    private Precio total;
    private String motivoReembolso;

    private Pedido(String id, Comprador comprador, List<ItemPedido> items, EstadoPedido estadoPedido, LocalDateTime fecha, Precio total) {
        this.id = id;
        this.comprador = comprador;
        this.items = items;
        this.estadoPedido = estadoPedido;
        this.fecha = fecha;
        this.total = total;
    }

    public void completar(){
        if (this.estadoPedido != EstadoPedido.PENDIENTE){
            throw new ReglaDominioException("Solo se puede completar un pedido pendiente");
        }

        this.estadoPedido = EstadoPedido.COMPLETADA;
    }

    public void solicitarReembolso(String motivo){
        if(this.estadoPedido != EstadoPedido.COMPLETADA){
            throw new ReglaDominioException("Solo se puede solicitar reembolso de un pedido completado");
        }
        if(motivo == null || motivo.isBlank()){
            throw new ReglaDominioException("Debe indicar el motivo del reembolso");
        }
        this.motivoReembolso = motivo.strip();
        this.estadoPedido = EstadoPedido.REEMBOLSADA;
    }

    public String getMotivoReembolso(){
        return motivoReembolso;
    }

    public EstadoPedido getEstadoPedido(){
        return estadoPedido;
    }

    public Producto obtenerProductoParaComentar(Comprador comprador, String idProducto){
        if (comprador == null || this.comprador == null || !this.comprador.equals(comprador)){
            throw new ReglaDominioException("Solo el comprador del pedido puede comentar sus productos");
        }
        if (estadoPedido != EstadoPedido.COMPLETADA){
            throw new ReglaDominioException("Solo se pueden comentar productos de un pedido completado");
        }
        if (idProducto == null || idProducto.isBlank()){
            throw new ReglaDominioException("Debe indicar el producto que desea comentar");
        }
        return items.stream()
                .filter(Objects::nonNull)
                .map(ItemPedido::getProducto)
                .filter(Objects::nonNull)
                .filter(producto -> idProducto.equals(producto.getId()))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("El producto no pertenece al pedido"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido)) return false;
        return id.equals(((Pedido) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public void validarParaRegistro() {
        if (this.items == null || this.items.isEmpty()) {
            throw new ReglaDominioException("Un pedido no puede registrarse sin items");
        }
        if (this.total == null || this.total.esNegativoOCero()) {
            throw new ReglaDominioException("Un pedido debe tener un total valido");
        }
    }

    public static Pedido registrar(String id, Comprador comprador, List<ItemPedido> items, LocalDateTime fecha, Precio total){
        Pedido pedido = new Pedido(id, comprador, items, EstadoPedido.PENDIENTE, fecha, total);
        pedido.validarParaRegistro();
        return pedido;
    }

    public String getId() {
        return id;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public List<ItemPedido> getItems() {
        return items;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Precio getTotal() {
        return total;
    }
}
