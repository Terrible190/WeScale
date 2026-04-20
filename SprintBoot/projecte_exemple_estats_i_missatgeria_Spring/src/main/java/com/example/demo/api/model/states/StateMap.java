package com.example.demo.api.model.states;

import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.out.show_map.ShowMapMessage_OUT;
import com.example.demo.components.GameInstance;

public class StateMap extends State
{

    public StateMap(GameInstance game) {
        super(game);
        game.broadcast(new JSONMessage(game.getId(), new ShowMapMessage_OUT()));
    }


    @Override
    public void tick() {
        game.pollMessage(1, TimeUnit.MILLISECONDS);

        //TO DO
    }



}