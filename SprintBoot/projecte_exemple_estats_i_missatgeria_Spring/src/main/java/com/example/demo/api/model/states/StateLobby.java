package com.example.demo.api.model.states;

import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.pick_characters.PickCharacterMessage_IN;
import com.example.demo.api.model.messages.out.CharactersList_OUT;
import com.example.demo.api.model.messages.out.PlayerJoined_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;

public class StateLobby extends State {

    private ObjectMapper mapper = new ObjectMapper();

    public StateLobby(GameInstance game) {
        super(game);

        game.broadcast(new JSONMessage(
            game.getId(),
            new CharactersList_OUT(game.getPersonajesDisponibles())
        ));
    }

    @Override
    public void tick() {
        GameMessage msg = game.pollMessage(5, TimeUnit.SECONDS);

        if (msg == null) return;

        JSONMessage json = mapper.readValue(msg.payload(), JSONMessage.class);

        switch (json.messageType) {

            case PickCharacterMessage_IN.TYPE:
                handlePick(msg, json);
                break;
        }
    }

    private void handlePick(GameMessage msg, JSONMessage json) {

        PickCharacterMessage_IN data =
            mapper.treeToValue(json.data, PickCharacterMessage_IN.class);

        var personaje = game.getPersonajesDisponibles()
            .stream()
            .filter(p -> p.getId() == data.characterId)
            .findFirst()
            .orElse(null);

        if (personaje == null) return;

        game.getPersonajesDisponibles().remove(personaje);
        game.getSeleccionados().put(msg.player().getId(), personaje);

        game.broadcast(new JSONMessage(
            game.getId(),
            new CharactersList_OUT(game.getPersonajesDisponibles())
        ));
    }
}