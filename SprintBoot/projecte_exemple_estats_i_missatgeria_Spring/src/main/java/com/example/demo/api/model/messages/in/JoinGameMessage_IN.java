/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.in;

/**
 *
 * @author Usuari
 */
import com.example.demo.api.model.messages.MessageBody;

public class JoinGameMessage_IN extends MessageBody {

    public static final String TYPE = "JOIN_GAME";

    public String gameId;

    @Override
    public String getMessageType() {
        return TYPE;
    }
}