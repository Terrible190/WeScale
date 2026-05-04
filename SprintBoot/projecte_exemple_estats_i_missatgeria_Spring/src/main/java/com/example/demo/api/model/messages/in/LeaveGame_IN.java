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

public class LeaveGame_IN extends MessageBody {
    public static final String TYPE = "LEAVE_GAME";

    @Override
    public String getMessageType() {
        return TYPE;
    }
}
