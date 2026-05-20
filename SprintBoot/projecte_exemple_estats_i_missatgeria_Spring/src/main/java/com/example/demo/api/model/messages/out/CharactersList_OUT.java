/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.out;

/**
 *
 * @author Usuari
 */

import java.util.List;
import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.messages.MessageBody;

public class CharactersList_OUT extends MessageBody {

    public static final String TYPE = "CHARACTERS";

    public List<Personaje> characters;

    public CharactersList_OUT(List<Personaje> characters) {
        this.characters = characters;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}