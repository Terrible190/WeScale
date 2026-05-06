/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.states;

import com.example.demo.components.GameInstance;

/**
 *
 * @author Usuari
 */
public class StateInGame extends State{

    public StateInGame(GameInstance game) {
        super(game);
        
        System.out.println("Siuuu");
    }

    @Override
    public void tick() {
        //
    }
    
}
