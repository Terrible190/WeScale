/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;

/**
 *
 * @author Anas
 */
public class Efecto {

    private int id;
    private int tipo; // invocacion, estado, modificador
    private Integer tipoDanyo;
    private Integer rango;
    private Integer duracion;

    public Efecto(int id, int tipo, Integer tipoDanyo, Integer rango, Integer duracion) {
        this.id = id;
        this.tipo = tipo;
        this.tipoDanyo = tipoDanyo;
        this.rango = rango;
        this.duracion = duracion;
    }

    public int getId() { return id; }
    public int getTipo() { return tipo; }
}