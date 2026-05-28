package com.example.demo.api.model;

public class ActiveEffect {

    private Efecto efecto;
    private int turns;

    public ActiveEffect(
            Efecto efecto,
            int turns)
    {
        this.efecto = efecto;
        this.turns = turns;
    }

    public Efecto getEfecto() {
        return efecto;
    }

    public int getTurns() {
        return turns;
    }

    public void reduceTurn() {
        turns--;
    }
}