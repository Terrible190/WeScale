package com.example.demo.api.model.messages.out.characters_to_pick;

import java.util.List;

import com.example.demo.api.model.messages.MessageBody;

public class Players2SelectMessage_OUT extends MessageBody {

    public final static String TYPE = "PLAYERS_2_CHOOSE";
    
    @Override
    public String getMessageType(){
        return TYPE;
    }

    public List<PlayerInfo> players;
    public List<CharacterInfo> characters;
}
