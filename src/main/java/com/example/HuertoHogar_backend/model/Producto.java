package com.example.HuertoHogar_backend.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    private String id; // p. ej. "FR001"

    private String codigo;
    private String name;
    private String categoria;
    private Double price;
    private String unidad;
    private String image;
    private String description;
    private String origen;
    private Integer stock;
}