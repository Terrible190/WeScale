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
    "items"
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

    @Column(name = "seleccionable")
    private boolean seleccionable;

    private boolean isAlive = true;

    public Personaje(int id,
            String nombre,
            float hp,
            float danyoFisico,
            float danyoMagico,
            float defensaFisica,
            float defensaMagica,
            float critico,
            float multiplicadorCritico,
            boolean seleccionable) {
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
        //
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "PERSONATGE_ACCIO",
            joinColumns = @JoinColumn(name = "id_personatge"),
            inverseJoinColumns = @JoinColumn(name = "id_objhabarm_actiu")
    )
    private List<Accio> acciones;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public float getDanyoFisico() {
        return danyoFisico;
    }

    public float getDanyoMagico() {
        return danyoMagico;
    }

    public boolean isSeleccionable() {
        return seleccionable;
    }

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

    float getDefensaFisica() {
        return defensaFisica;
    }

    float getDefensaMagica() {
        return defensaMagica;
    }

    float getCritico() {
        return critico;
    }
}
