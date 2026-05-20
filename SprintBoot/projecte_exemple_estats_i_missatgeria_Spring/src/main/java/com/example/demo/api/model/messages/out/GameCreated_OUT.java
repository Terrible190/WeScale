/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.out;

/**
 *
 * @author Usuari
 */
import com.example.demo.api.model.messages.MessageBody;

public class GameCreated_OUT extends MessageBody {

    public static final String TYPE = "GAME_CREATED";

    public String gameId;

    public GameCreated_OUT(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}