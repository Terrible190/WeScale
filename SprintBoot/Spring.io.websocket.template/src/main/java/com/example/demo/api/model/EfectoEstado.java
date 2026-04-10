/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;

/**
 *
 * @author Anas
 */

public class EfectoEstado extends Efecto {

    private Estado estado;

    public EfectoEstado(int id, int tipo, Integer tipoDanyo, Integer rango, Integer duracion, Estado estado) {
        super(id, tipo, tipoDanyo, rango, duracion);
        this.estado = estado;
    }

    public Estado getEstado() { return estado; }
}