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

public class PlayerJoined_OUT extends MessageBody {

    public static final String TYPE = "PLAYER_JOINED";

    public int totalPlayers;

    public PlayerJoined_OUT(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}