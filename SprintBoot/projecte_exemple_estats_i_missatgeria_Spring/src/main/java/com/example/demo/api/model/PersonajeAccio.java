package com.example.demo.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PERSONATGE_ACCIO")
public class PersonajeAccio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personatge_accio")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_personatge")
    private Personaje personaje;

    @ManyToOne
    @JoinColumn(name = "id_objhabarm_actiu")
    private Accio accio;

    @Column(name = "equipada")
    private boolean equipada;

    public Accio getAccio() {
        return accio;
    }
}