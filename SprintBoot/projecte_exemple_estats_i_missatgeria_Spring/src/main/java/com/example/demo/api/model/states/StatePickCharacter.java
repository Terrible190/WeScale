package com.example.demo.api.model.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.Player;
import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.pick_characters.PickCharacterMessage_IN;
import com.example.demo.api.model.messages.out.characters_to_pick.CharacterInfo;
import com.example.demo.api.model.messages.out.characters_to_pick.PlayerInfo;
import com.example.demo.api.model.messages.out.characters_to_pick.Players2SelectMessage_OUT;
import com.example.demo.api.model.messages.out.generic.ActionResult_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;

public class StatePickCharacter extends State {

    private ObjectMapper mapper;
    private Players2SelectMessage_OUT m;

    public StatePickCharacter(GameInstance game) {
        super(game);
        mapper = new ObjectMapper();

        // 🔥 PLAYERS
        List<PlayerInfo> players = new ArrayList<>();
        for (Player p : game.getPlayers()) {
            players.add(new PlayerInfo(p.getId(), "Player " + p.getId()));
        }

        // 🔥 PERSONAJES DESDE GAME (BD)
        List<CharacterInfo> characters = new ArrayList<>();

        for (Personaje p : game.getPersonajesDisponibles()) {
            characters.add(new CharacterInfo(
                p.getId(),
                p.getNombre(),
                "", // image opcional
                -1,
                false
            ));
        }

        m = new Players2SelectMessage_OUT();
        m.players = players;
        m.characters = characters;

        game.broadcast(new JSONMessage(game.getId(), m));
    }

    @Override
    public void tick() {

        GameMessage message = game.pollMessage(5, TimeUnit.SECONDS);

        if (message == null) return;

        JSONMessage gm = mapper.readValue(message.payload(), JSONMessage.class);

        switch (gm.messageType) {
            case PickCharacterMessage_IN.TYPE:
                pickCharacter(message.player(), gm);
                break;
        }
    }

    private void pickCharacter(Player p, JSONMessage jsonMsg) {

        PickCharacterMessage_IN msg =
            mapper.treeToValue(jsonMsg.data, PickCharacterMessage_IN.class);

        System.out.println("Pick: " + msg.characterId);

        // 🔥 BUSCAR PERSONAJE REAL
        Optional<Personaje> op = game.getPersonajesDisponibles()
                .stream()
                .filter(x -> x.getId() == msg.characterId)
                .findFirst();

        if (!op.isPresent()) {
            game.send(p.getSession(),
                new JSONMessage(game.getId(), new ActionResult_OUT(false, 2)));
            return;
        }

        Personaje personaje = op.get();

        // 🔥 EVITAR DOBLE SELECCIÓN
        if (game.getSeleccionados().containsValue(personaje)) {
            game.send(p.getSession(),
                new JSONMessage(game.getId(), new ActionResult_OUT(false, 3)));
            return;
        }

        // 🔥 GUARDAR SELECCIÓN
        game.getSeleccionados().put(p.getId(), personaje);

        // 🔥 ACTUALIZAR STRUCT m
        for (CharacterInfo ci : m.characters) {
            if (ci.characterId == personaje.getId()) {
                ci.isSelected = true;
                ci.selectedPlayerId = (int) p.getId();
            }
        }

        // 🔥 RESPUESTA OK
        game.send(p.getSession(),
            new JSONMessage(game.getId(), new ActionResult_OUT(true, 0)));

        // 🔥 BROADCAST
        game.broadcast(new JSONMessage(game.getId(), m));

        // 🔥 SI TODOS HAN ELEGIDO → SIGUIENTE ESTADO
        if (game.getSeleccionados().size() == game.getPlayers().size()) {
            game.setState(new StateMap(game));
        }
    }
}