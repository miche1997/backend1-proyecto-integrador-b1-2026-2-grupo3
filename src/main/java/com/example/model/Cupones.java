package com.example.model;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "cupones")
public class Cupones {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    private String codigo;
    private double porcentaje; 
    private int usos; 
    private String estado; 
    private LocalDateTime fechaCreacion;

    // Constructor completo
    public Cupones(Long id, String codigo, double porcentaje, int usos, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.usos = usos;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    // Constructor sin ID

    public Cupones(String codigo, double porcentaje, int usos, String estado, LocalDateTime fechaCreacion) {
        this(null, codigo, porcentaje, usos, estado, fechaCreacion);
    }

    // Constructor vacío

    public Cupones() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public double getPorcentaje() {
        return porcentaje;
    }
    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
    public int getUsos() {
        return usos;
    }
    public void setUsos(int usos) {
        this.usos = usos;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Cupon [id=" + id + ", codigo=" + codigo + ", porcentaje=" + porcentaje + "%, usos=" + usos + ", estado=" + estado + "]";
    }
}

