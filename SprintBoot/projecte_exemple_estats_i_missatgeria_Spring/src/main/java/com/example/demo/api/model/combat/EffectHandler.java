/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.combat;
import com.example.demo.api.model.*;
import com.example.demo.components.*;
/**
 *
 * @author Anas
 */
public interface EffectHandler {

    void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game
    );
}