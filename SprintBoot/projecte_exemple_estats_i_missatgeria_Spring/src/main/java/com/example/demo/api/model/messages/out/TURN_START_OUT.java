package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.messages.MessageBody;

public class TURN_START_OUT extends MessageBody {

    public static final String TYPE = "TURN_START";

    @Override
    public String getMessageType() {
        return TYPE;
    }
}