/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.in;

import com.example.demo.api.model.messages.MessageBody;

/**
 *
 * @author Usuari
 */
public class GetGamesList_IN extends MessageBody {

    public static final String TYPE = "GET_GAMES_LIST";

    @Override
    public String getMessageType() {
        return TYPE;
    }
}