package com.example.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;


@Entity
@Table(name = "pedidos")
public class Pedidos {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long idPedido;
private int numeroPedido;
private LocalDate fechaPedido; 
private int numeroProducto;


public Pedidos(Long idPedido, int numeroPedido, LocalDate fechaPedido, int numeroProducto) {
    this.idPedido = idPedido;
    this.numeroPedido = numeroPedido;
    this.fechaPedido = fechaPedido;
    this.numeroProducto = numeroProducto;
}

public Long getIdPedido() {
    return idPedido;
}

public void setIdPedido(Long idPedido) {
    this.idPedido = idPedido;
}

public int getNumeroPedido() {
    return numeroPedido;
}

public void setNumeroPedido(int numeroPedido) {
    this.numeroPedido = numeroPedido;
}

public LocalDate getFechaPedido() {
    return fechaPedido;
}

public void setFechaPedido(LocalDate fechaPedido) {
    this.fechaPedido = fechaPedido;
}

public int getNumeroProducto() {
    return numeroProducto;
}

public void setNumeroProducto(int numeroProducto) {
    this.numeroProducto = numeroProducto;
}

@Override
public String toString() {
    return "Pedidos [idPedido=" + idPedido + ", numeroPedido=" + numeroPedido + ", fechaPedido=" + fechaPedido
            + ", numeroProducto=" + numeroProducto + ", getIdPedido()=" + getIdPedido() + ", getNumeroPedido()="
            + getNumeroPedido() + ", getFechaPedido()=" + getFechaPedido() + ", getNumeroProducto()="
            + getNumeroProducto() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
            + super.toString() + "]";
}

// jjjj

}

