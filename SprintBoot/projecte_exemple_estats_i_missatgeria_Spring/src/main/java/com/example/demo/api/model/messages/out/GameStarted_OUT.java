/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.messages.MessageBody;
import static com.example.demo.api.model.messages.out.GameInfo_OUT.TYPE;

/**
 *
 * @author Usuari
 */
public class GameStarted_OUT extends MessageBody {

    public static final String TYPE = "GameStarted";

    @Override
    public String getMessageType() {
        return TYPE;
    }

}
