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

public class ReadyStatus_OUT extends MessageBody {

    public static final String TYPE = "READY_STATUS";

    public int ready;
    public int total;

    public ReadyStatus_OUT(int ready, int total) {
        this.ready = ready;
        this.total = total;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}