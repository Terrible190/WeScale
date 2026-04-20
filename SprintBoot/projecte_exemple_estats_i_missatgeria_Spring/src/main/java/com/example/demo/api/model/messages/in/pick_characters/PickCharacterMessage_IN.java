package com.example.demo.api.model.messages.in.pick_characters;

import com.example.demo.api.model.messages.MessageBody;

public class PickCharacterMessage_IN extends MessageBody {

    public static final String TYPE = "PICK_CHARACTER";

    public int characterId;

    @Override
    public String getMessageType() {
        return TYPE;
    }
}