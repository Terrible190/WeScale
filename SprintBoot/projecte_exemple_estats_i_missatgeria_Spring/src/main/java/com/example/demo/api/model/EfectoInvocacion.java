/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;

/**
 *
 * @author Anas
 */
public class EfectoInvocacion extends Efecto {

    private Personaje invocado;

    public EfectoInvocacion(int id, int tipo, Integer tipoDanyo,
                            Integer rango, Integer duracion, Personaje invocado) {

        super(id, tipo, tipoDanyo, rango, duracion);
        this.invocado = invocado;
    }

    public Personaje getInvocado() { return invocado; }
}