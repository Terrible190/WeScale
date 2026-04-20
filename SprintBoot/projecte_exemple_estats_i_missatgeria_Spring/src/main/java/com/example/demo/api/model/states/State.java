package com.example.demo.api.model.states;

import com.example.demo.components.GameInstance;

public abstract class State {

    protected GameInstance game;

    public State(GameInstance game){
        this.game = game;    
    }
    
    public abstract void tick();
 
    
}
