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
    @Column(name = "dany_fisic_base")
    private float danyoFisico;
    @Column(name = "dany_magic_base")
    private float danyoMagico;
    @Column(name = "defensa_fisica_base")
    private float defensaFisica;
    @Column(name = "defensa_magica_base")
    private float defensaMagica;
    @Column(name = "critic_base")
    private float critico;
    @Column(name = "critic_multiplicador_base")
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

    public float getDanyoFisico() {
        return danyoFisico;
    }

    public void setDanyoFisico(float danyoFisico) {
        this.danyoFisico = danyoFisico;
    }

    public float getDanyoMagico() {
        return danyoMagico;
    }

    public void setDanyoMagico(float danyoMagico) {
        this.danyoMagico = danyoMagico;
    }

    public float getDefensaFisica() {
        return defensaFisica;
    }

    public void setDefensaFisica(float defensaFisica) {
        this.defensaFisica = defensaFisica;
    }

    public float getDefensaMagica() {
        return defensaMagica;
    }

    public void setDefensaMagica(float defensaMagica) {
        this.defensaMagica = defensaMagica;
    }

    public float getCritico() {
        return critico;
    }

    public void setCritico(float critico) {
        this.critico = critico;
    }

    public float getMultiplicadorCritico() {
        return multiplicadorCritico;
    }

    public void setMultiplicadorCritico(float multiplicadorCritico) {
        this.multiplicadorCritico = multiplicadorCritico;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + " - Nom: " + getNombre() + " - Vida: " + getHp();
    }
}
