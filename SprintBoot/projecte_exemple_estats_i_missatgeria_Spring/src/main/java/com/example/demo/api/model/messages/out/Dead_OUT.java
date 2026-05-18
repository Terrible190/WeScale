package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.messages.MessageBody;

public class Dead_OUT extends MessageBody {

    public static final String TYPE = "Dead_OUT";

    public long id;
    public String name;
    public boolean isAlive = false;

    public Dead_OUT(long id, String name) {

        this.id = id;
        this.name = name;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}