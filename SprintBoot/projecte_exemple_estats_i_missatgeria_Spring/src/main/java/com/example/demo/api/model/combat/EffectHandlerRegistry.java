/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.combat;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Anas
 */
public class EffectHandlerRegistry {

    private final Map<Integer, EffectHandler> handlers = new HashMap<>();

    public EffectHandlerRegistry() {
        register(2, new DefenseBuffHandler());
        register(1, new DamageEffectHandler());
    }

    public void register(int tipo, EffectHandler handler) {
        handlers.put(tipo, handler);
    }

    public EffectHandler get(int tipo) {
        return handlers.get(tipo);
    }
}