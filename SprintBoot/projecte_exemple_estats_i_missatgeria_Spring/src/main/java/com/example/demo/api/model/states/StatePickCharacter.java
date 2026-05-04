package com.example.demo.api.model.states;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.Player;
import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.UnselectCharacterMessage_IN;
import com.example.demo.api.model.messages.in.pick_characters.PickCharacterMessage_IN;
import com.example.demo.api.model.messages.out.characters_to_pick.CharacterInfo;
import com.example.demo.api.model.messages.out.characters_to_pick.PlayerInfo;
import com.example.demo.api.model.messages.out.characters_to_pick.Players2SelectMessage_OUT;
import com.example.demo.api.model.messages.out.generic.ActionResult_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;

public class StatePickCharacter extends State {

    private final ObjectMapper mapper = new ObjectMapper();

    public StatePickCharacter(GameInstance game) {
        super(game);
        broadcastState();
    }

    @Override
    public void tick() {

        GameMessage message = game.pollMessage(5, TimeUnit.SECONDS);
        if (message == null) {
            return;
        }

        JSONMessage gm = mapper.readValue(message.payload(), JSONMessage.class);

        switch (gm.messageType) {

            case PickCharacterMessage_IN.TYPE: {

                PickCharacterMessage_IN data
                        = mapper.treeToValue(gm.data, PickCharacterMessage_IN.class);

                boolean ok = game.pickCharacter(message.player(), data.characterId);

                if (ok) {
                    broadcastState();
                } else {
                    game.send(message.player().getSession(),
                            new JSONMessage(game.getId(),
                                    new ActionResult_OUT(false, 1)));
                }
                break;

            }
            case UnselectCharacterMessage_IN.TYPE: {

                boolean ok = game.unselectCharacter(message.player());

                if (ok) {
                    broadcastState(); // 🔥 actualizar a todos
                } else {
                    game.send(message.player().getSession(),
                            new JSONMessage(game.getId(),
                                    new ActionResult_OUT(false, 2)));
                }

                break;
            }
        }
    }

    private void broadcastState() {

        List<PlayerInfo> players = new ArrayList<>();

        for (Player p : game.getPlayers()) {

            players.add(new PlayerInfo(
                    p.getId(),
                    p.getName(),
                    game.getSeleccionados().get(p.getId()) // puede ser null
            ));
        }

        List<CharacterInfo> characters = new ArrayList<>();

        for (Personaje p : game.getPersonajesDisponibles()) {
            characters.add(new CharacterInfo(
                    p.getId(),
                    p.getNombre(),
                    "",
                    -1,
                    false
            ));
        }

        Players2SelectMessage_OUT out = new Players2SelectMessage_OUT();
        out.players = players;
        out.characters = characters;

        game.broadcast(new JSONMessage(game.getId(), out));
    }
}
