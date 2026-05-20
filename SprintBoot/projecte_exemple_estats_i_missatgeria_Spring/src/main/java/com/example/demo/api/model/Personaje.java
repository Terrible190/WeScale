package com.example.demo.api.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "id",
    "nombre",
    "hp",
    "danyoFisico",
    "danyoMagico",
    "seleccionable",
    "acciones",
    "armes",
    "items",
    "isAlive"
})
@Entity
@Table(name = "PERSONATGE")
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

    @Column(name = "velocitat")
    private float velocidad;

    @Column(name = "seleccionable")
    private boolean seleccionable;

    // NO persistente combate runtime
    @Transient
    private boolean isAlive = true;

    public Personaje(
            int id,
            String nombre,
            float hp,
            float danyoFisico,
            float danyoMagico,
            float defensaFisica,
            float defensaMagica,
            float critico,
            float multiplicadorCritico,
            boolean seleccionable
    ) {

        this.id = id;
        this.nombre = nombre;

        this.hp = hp;

        this.danyoFisico = danyoFisico;
        this.danyoMagico = danyoMagico;

        this.defensaFisica = defensaFisica;
        this.defensaMagica = defensaMagica;

        this.critico = critico;

        this.multiplicadorCritico = multiplicadorCritico;

        this.seleccionable = seleccionable;
    }

    public Personaje() {
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "PERSONATGE_ACCIO",
            joinColumns = @JoinColumn(name = "id_personatge"),
            inverseJoinColumns = @JoinColumn(name = "id_objhabarm_actiu")
    )
    private List<Accio> acciones;

    // =========================
    // GETTERS
    // =========================
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public float getHp() {
        return hp;
    }

    public float getDanyoFisico() {
        return danyoFisico;
    }

    public float getDanyoMagico() {
        return danyoMagico;
    }

    public float getDefensaFisica() {
        return defensaFisica;
    }

    public float getDefensaMagica() {
        return defensaMagica;
    }

    public float getCritico() {
        return critico;
    }

    public float getMultiplicadorCritico() {
        return multiplicadorCritico;
    }
    public float getVelocidad() {
        return velocidad;
    }
    public boolean isSeleccionable() {
        return seleccionable;
    }

    public boolean isIsAlive() {
        return isAlive;
    }

    // =========================
    // SETTERS
    // =========================
    public void setHp(float hp) {

        this.hp = hp;

        if (this.hp <= 0) {

            this.hp = 0;
            this.isAlive = false;
        }
    }

    public void setDanyoFisico(float danyoFisico) {
        this.danyoFisico = danyoFisico;
    }

    public void setDanyoMagico(float danyoMagico) {
        this.danyoMagico = danyoMagico;
    }

    public void setDefensaFisica(float defensaFisica) {
        this.defensaFisica = defensaFisica;
    }

    public void setDefensaMagica(float defensaMagica) {
        this.defensaMagica = defensaMagica;
    }

    public void setCritico(float critico) {
        this.critico = critico;
    }

    public void setMultiplicadorCritico(float multiplicadorCritico) {
        this.multiplicadorCritico = multiplicadorCritico;
    }
    public void setVelocidad( float velocidad  ) {
        this.velocidad = velocidad;
    }
    public void setSeleccionable(boolean seleccionable) {
        this.seleccionable = seleccionable;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    // =========================
    // JSON HELPERS
    // =========================
    @JsonProperty("acciones")
    public List<Accio> getAcciones() {

        return acciones.stream()
                .filter(a -> a.getTipo() == 2)
                .toList();
    }

    @JsonProperty("armes")
    public List<Accio> getArmes() {

        return acciones.stream()
                .filter(a -> a.getTipo() == 1)
                .toList();
    }

    @JsonProperty("items")
    public List<Accio> getItems() {

        return acciones.stream()
                .filter(a -> a.getTipo() == 3)
                .toList();
    }

    public Personaje copy() {

        Personaje p = new Personaje();

        p.id = this.id;
        p.nombre = this.nombre;

        p.hp = this.hp;
        p.danyoFisico = this.danyoFisico;
        p.danyoMagico = this.danyoMagico;
        p.velocidad = this.velocidad;
        p.defensaFisica = this.defensaFisica;
        p.defensaMagica = this.defensaMagica;

        p.critico = this.critico;
        p.multiplicadorCritico = this.multiplicadorCritico;

        p.seleccionable = this.seleccionable;

        p.isAlive = true;

        // 🔥 IMPORTANTE: copiar lista (shallow copy)
        if (this.acciones != null) {
            p.acciones = new java.util.ArrayList<>(this.acciones);
        }

        return p;
    }
}
