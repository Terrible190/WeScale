/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.combat;

import com.example.demo.api.model.Efecto;
import com.example.demo.api.model.EnemyInstance;
import com.example.demo.api.model.Personaje;
import com.example.demo.components.GameInstance;

/**
 *
 * @author Anas
 */
public class Angel_Custodi implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game
    ) {
        System.out.println("hola2");
        if (targetAlly == null) {
            return;
        }

        targetAlly.setHp(
                targetAlly.getHp()
                * 1.5f
        );
    }

}
