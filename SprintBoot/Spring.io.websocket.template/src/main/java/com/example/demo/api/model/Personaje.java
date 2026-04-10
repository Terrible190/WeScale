package com.example.demo.api.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Personatge")
public class Personaje {

    @Id
    @Column(name = "id_personatge")
    private int id;
    @Column(name = "nom")
    private String nombre;
    @Column(name = "hp_base")
    private float hp;
    private float danyoFisico;
    private float danyoMagico;

    private float defensaFisica;
    private float defensaMagica;

    private float critico;
    private float multiplicadorCritico;
    private boolean seleccionable;
    @Transient
    private List<Accio> acciones;

    public Personaje() {
    }

    public Personaje(int id, String nombre, float hp, float danyoFisico, float danyoMagico,
            float defensaFisica, float defensaMagica,
            float critico, float multiplicadorCritico) {

        this.id = id;
        this.nombre = nombre;
        this.hp = hp;
        this.danyoFisico = danyoFisico;
        this.danyoMagico = danyoMagico;
        this.defensaFisica = defensaFisica;
        this.defensaMagica = defensaMagica;
        this.critico = critico;
        this.multiplicadorCritico = multiplicadorCritico;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public float getHp() {
        return hp;
    }

    public List<Accio> getAcciones() {
        return acciones;
    }

    public void setAcciones(List<Accio> acciones) {
        this.acciones = acciones;
    }

    public boolean isSeleccionable() {
        return seleccionable;
    }
    
    @Override
    public String toString(){
        return "Id: " + getId() + " - Nom: " + getNombre() + " - Vida: " + getHp();
    }
}
