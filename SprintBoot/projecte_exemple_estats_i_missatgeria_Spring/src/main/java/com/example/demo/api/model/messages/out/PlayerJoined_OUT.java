package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.messages.MessageBody;

public class PlayerJoined_OUT extends MessageBody {

    public static final String TYPE = "PLAYER_JOINED";

    public long playerId;
    public String playerName;
    public int totalPlayers;

    public PlayerJoined_OUT(long playerId, String playerName, int totalPlayers) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.totalPlayers = totalPlayers;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}