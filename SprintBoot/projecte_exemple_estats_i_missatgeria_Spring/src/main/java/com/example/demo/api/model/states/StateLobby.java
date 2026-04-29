package com.example.demo.api.model.states;

import com.example.demo.api.model.Player;
import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.PlayerReadyMessage_IN;
import com.example.demo.api.model.messages.in.pick_characters.PickCharacterMessage_IN;
import com.example.demo.api.model.messages.out.CharactersList_OUT;
import com.example.demo.api.model.messages.out.PlayerJoined_OUT;
import com.example.demo.api.model.messages.out.ReadyStatus_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;
// sin uso
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

        if (msg == null) {
            return;
        }

        JSONMessage json = mapper.readValue(msg.payload(), JSONMessage.class);

        switch (json.messageType) {

            case PickCharacterMessage_IN.TYPE:
                game.setState(new StatePickCharacter(game));       
                break;

            case PlayerReadyMessage_IN.TYPE:
                handleReady(msg, json);
                break;
            /*
                {
                    "messageType": "PLAYER_READY",
                    "data": {
                      "ready": true
                }
}
             */
        }
    }

    private void handleReady(GameMessage msg, JSONMessage json) {

        PlayerReadyMessage_IN data
                = mapper.treeToValue(json.data, PlayerReadyMessage_IN.class);

        // marcar jugador
        msg.player().setReady(data.ready);

        // contar listos
        int readyCount = (int) game.getPlayers()
                .stream()
                .filter(Player::isReady)
                .count();

        int total = game.getPlayers().size();

        // 🔥 enviar a todos el estado
        game.broadcast(new JSONMessage(
                game.getId(),
                new ReadyStatus_OUT(readyCount, total)
        ));

        // si todos listos → empezar
        if (readyCount == total) {
            game.start();
        }
    }
}
