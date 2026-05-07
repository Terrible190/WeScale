/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.states;

import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.Player;
import java.util.concurrent.TimeUnit;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
import com.example.demo.api.model.messages.out.GameStarted_OUT;

import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;
import tools.jackson.databind.ObjectMapper;

/**
 *
 * @author Usuari
 */
public class StateInGame extends State {

    private int currentTurn = 0;

    private final ObjectMapper mapper = new ObjectMapper();

    public StateInGame(GameInstance game) {
        super(game);

        game.broadcast(new JSONMessage(
                game.getId(),
                new GameStarted_OUT()
        ));
        System.out.println("siuuu");

        //startTurn();
    }

    @Override
    public void tick() {

        GameMessage msg = game.pollMessage(1, TimeUnit.SECONDS);

        if (msg == null) {
            return;
        }

        JSONMessage json = mapper.readValue(msg.payload(), JSONMessage.class);

        switch (json.messageType) {

            case UseActionMessage_IN.TYPE:
                UseActionMessage_IN data
                        = mapper.treeToValue(json.data, UseActionMessage_IN.class);

                Player player = msg.player();
                
                System.out.println(
                        "[ACTION DEBUG] Player: "
                        + player.getName()
                        + " (ID: " + player.getId() + ") "
                        + " ha usado acción: " + data.getActionId() 
                        + " Contra " + data.getTargetId()
                        + " Personaje " + game.getSeleccionados().get(player.getId()).getDanyoFisico()
                        
                );
                System.out.println("siu");
                break;

        }
    }
}
