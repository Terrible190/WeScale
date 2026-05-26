/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.combat;
import com.example.demo.api.model.*;
import com.example.demo.components.*;
/**
/**
 *
 * @author Anas
 */
public class DamageEffectHandler
        implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game
    ) {

        if (targetEnemy == null) {
            return;
        }

        float damage =
                attacker.getDanyoFisico()
                - targetEnemy.getBase()
                        .getDefensaFisica();

        damage = Math.max(1, damage);

        float hp =
                targetEnemy.getBase().getHp()
                - damage;

        targetEnemy.getBase().setHp(hp);
    }
}