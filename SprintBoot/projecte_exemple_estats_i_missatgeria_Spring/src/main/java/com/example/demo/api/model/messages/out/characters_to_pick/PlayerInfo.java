package com.example.demo.api.model.messages.out.characters_to_pick;

import com.example.demo.api.model.Personaje;

public class PlayerInfo {

    public long id;
    public String name;
    public Personaje personajeSeleccionado;
    public PlayerInfo(long id, String name, Personaje p) {
        this.id = id;
        this.name = name;
        this.personajeSeleccionado = p;
    }
    public PlayerInfo() {
    }
    
}
