package com.example.demo.api.model.messages.in;

import com.example.demo.api.model.messages.MessageBody;

public class CLIENT_READY  extends MessageBody{
       public static final String TYPE = "CLIENT_READY";

    @Override
    public String getMessageType() {
        return TYPE;
    }
}


