/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;

/**
 *
 * @author Anas
 */
public class Jugador {

    private int id;
    private String nombre;
    private Personaje personaje;

    public Jugador(int id, String nombre, Personaje personaje) {
        this.id = id;
        this.nombre = nombre;
        this.personaje = personaje;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Personaje getPersonaje() { return personaje; }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }
}