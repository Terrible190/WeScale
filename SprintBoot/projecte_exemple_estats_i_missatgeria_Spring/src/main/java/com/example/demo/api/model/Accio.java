package com.example.demo.api.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "id",
    "nombre",
    "tipo",
    "cooldown",
    "descripcion"
})
@Entity
@Table(name = "ACCIO")
public class Accio {

    @Id
    @Column(name = "id_obj_actiu")
    private int id;

    @Column(name = "nom")
    private String nombre;

    @Column(name = "tipus")
    private int tipo;

    @Column(name = "cooldown")
    private int cooldown;

    @Column(name = "descripcio")
    private String descripcion;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getTipo() { return tipo; }
    public int getCooldown() { return cooldown; }
    public String getDescripcion() { return descripcion; }
}
