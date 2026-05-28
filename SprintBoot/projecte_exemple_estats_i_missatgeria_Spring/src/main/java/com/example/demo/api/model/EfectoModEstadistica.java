/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;

/**
 *
 * @author Anas
 */
public class EfectoModEstadistica extends Efecto {

    private int stat;
    private int operacion;
    private float valor;

    public EfectoModEstadistica(int id, int tipo, Integer tipoDanyo, Integer rango,
                                Integer duracion, int stat, int operacion, float valor) {

        super(id, tipo, tipoDanyo, rango, duracion);
        this.stat = stat;
        this.operacion = operacion;
        this.valor = valor;
    }
    
}