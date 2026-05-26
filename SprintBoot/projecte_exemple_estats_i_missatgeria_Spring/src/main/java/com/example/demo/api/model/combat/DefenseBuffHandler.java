package com.example.demo.api.model.combat;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.example.demo.api.model.*;
import com.example.demo.components.*;
/**
 *
 * @author Anas
 */
public class DefenseBuffHandler
        implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game
    ) {
        System.out.println("hola");
        if (targetAlly == null) {
            return;
        }

        targetAlly.setDefensaFisica(
                targetAlly.getDefensaFisica()
                * 1.7f
        );
    }
}