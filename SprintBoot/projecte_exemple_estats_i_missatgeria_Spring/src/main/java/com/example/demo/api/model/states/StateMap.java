package com.example.demo.api.model.states;

import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.selectPis_IN;
import com.example.demo.api.model.messages.out.show_map.ShowMapMessage_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

public class StateMap extends State {

    private final ObjectMapper mapper
            = new ObjectMapper();

    private Integer selectedPis = null;

    public StateMap(GameInstance game) {
        super(game);

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new ShowMapMessage_OUT()
                )
        );
    }

    @Override
    public void tick() {

        GameMessage msg
                = game.pollMessage(
                        1,
                        TimeUnit.MILLISECONDS
                );

        if (msg == null) {
            return;
        }

        try {

            JSONMessage json
                    = mapper.readValue(
                            msg.payload(),
                            JSONMessage.class
                    );

            int pis
                    = json.data.asInt();

            selectedPis = pis;

            System.out.println(
                    "[MAP] piso seleccionado: "
                    + selectedPis
            );
            
            game.setState(new StateInGame(game, selectedPis));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
