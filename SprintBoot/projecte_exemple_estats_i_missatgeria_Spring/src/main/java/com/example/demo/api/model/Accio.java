/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;

import java.util.List;

/**
 *
 * @author Anas
 */
public class Accio {

    private int id;
    private String nombre;
    private int tipo;
    private int cooldown;
    private String descripcion;

    private List<Efecto> efectos;

    public Accio(int id, String nombre, int tipo, int cooldown, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cooldown = cooldown;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getTipo() { return tipo; }

    public List<Efecto> getEfectos() { return efectos; }
    public void setEfectos(List<Efecto> efectos) { this.efectos = efectos; }
}