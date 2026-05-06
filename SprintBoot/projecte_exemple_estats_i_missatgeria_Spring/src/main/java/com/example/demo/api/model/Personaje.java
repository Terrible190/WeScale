package com.example.demo.api.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonPropertyOrder({
    "id",
    "nombre",
    "hp",
    "danyoFisico",
    "danyoMagico",
    "seleccionable",
    "acciones"
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "PERSONATGE_ACCIO",
        joinColumns = @JoinColumn(name = "id_personatge"),
        inverseJoinColumns = @JoinColumn(name = "id_objhabarm_actiu")
    )
    private List<Accio> acciones;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public float getHp() { return hp; }
    public float getDanyoFisico() { return danyoFisico; }
    public float getDanyoMagico() { return danyoMagico; }
    public boolean isSeleccionable() { return seleccionable; }

    public List<Accio> getAcciones() {
        return acciones;
    }
}