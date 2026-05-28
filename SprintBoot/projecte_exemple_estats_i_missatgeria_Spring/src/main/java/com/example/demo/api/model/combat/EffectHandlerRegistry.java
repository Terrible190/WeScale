/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.combat;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.api.model.combat.EffectHandler;

/**
 *
 * @author Anas
 */
public class EffectHandlerRegistry {

    private final Map<EffectType, EffectHandler>
        handlers = new HashMap<>();

    public EffectHandlerRegistry() {

        register(
            EffectType.DAMAGE,
            new DamageEffectHandler()
        );

        register(
            EffectType.STATUS,
            new StatusEffectHandler()
        );

        register(
            EffectType.BUFF,
            new BuffEffectHandler()
        );

        /*register(
            EffectType.SUMMON,
            new SummonEffectHandler()
        );*/
    }


    public void register(
        EffectType type,
        EffectHandler handler
    ) {
        handlers.put(type, handler);
    }

   public EffectHandler get(int tipoId) {

    System.out.println("[GET HANDLER] tipoId=" + tipoId);

    EffectType type =
        EffectType.fromId(tipoId);

    System.out.println("[GET HANDLER] type=" + type);

    if (type == null) {
        return null;
    }

    EffectHandler handler = handlers.get(type);

    System.out.println("[GET HANDLER] handler=" + handler);

    return handler;
}

public enum EffectType {

    DAMAGE(3),
    STATUS(2),
    BUFF(4),
    SUMMON(5);

    private final int id;

    EffectType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EffectType fromId(int id) {

        for (EffectType t : values()) {
            if (t.id == id)
                return t;
        }

        return null;
    }
}

}

