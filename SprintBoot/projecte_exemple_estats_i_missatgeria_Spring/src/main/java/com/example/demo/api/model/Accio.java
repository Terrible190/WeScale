package com.example.demo.api.model;

import java.util.List;
import java.util.ArrayList;

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

    @Column(name = "estadistica")
    private Integer estadistica;

    @Column(name = "descripcio")
    private String descripcion;

    private int targetType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
    name = "ACCIO_EFECTE",
    joinColumns = @JoinColumn(name = "id_accio"),
    inverseJoinColumns = @JoinColumn(name = "id_efecte")
    )
    private List<Efecto> efectos =
        new ArrayList<>();

    public List<Efecto> getEfectos() {
        return efectos;
    }

    public int getTargetType() {
        return targetType;
    }   

    public void setEfectos(List<Efecto> efectos) {
        this.efectos = efectos;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTipo() {
        return tipo;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Integer getEstadistica() {
        return estadistica;
    }

    public String getDescripcion() {
        return descripcion;
    }
}