package com.example.demo.api.model.messages.out;

import tools.jackson.databind.JsonNode;
import com.example.demo.api.model.messages.MessageBody;

public class GameInfo_OUT extends MessageBody {

    public static final String TYPE = "GameInfo_OUT";
    
    public CharactersList_OUT cr;
    public JsonNode mapa;

    public GameInfo_OUT(CharactersList_OUT cr, JsonNode mapa) {
        this.cr = cr;
        this.mapa = mapa;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}