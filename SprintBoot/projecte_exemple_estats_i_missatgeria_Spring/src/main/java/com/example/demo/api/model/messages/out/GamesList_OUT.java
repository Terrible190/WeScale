/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.messages.MessageBody;
import java.util.List;

/**
 *
 * @author Usuari
 */
public class GamesList_OUT extends MessageBody {

    public static final String TYPE = "GAMES_LIST";

    public List<GameFastInfo> games;

    public GamesList_OUT(List<GameFastInfo> games) {
        this.games = games;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}