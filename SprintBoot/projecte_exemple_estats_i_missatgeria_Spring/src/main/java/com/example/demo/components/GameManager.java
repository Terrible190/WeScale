package com.example.demo.components;

import java.util.*;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.api.model.Player;
import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.*;
import com.example.demo.api.model.messages.out.CharactersList_OUT;
import com.example.demo.api.model.messages.out.GameFastInfo;
import com.example.demo.api.model.messages.out.GamesList_OUT;
import com.example.demo.api.model.messages.out.PlayerJoined_OUT;
import com.example.demo.api.model.messages.out.characters_to_pick.PlayerInfo;
import com.example.demo.api.model.messages.out.generic.ActionResult_OUT;
import com.example.demo.api.model.states.StateLobby;
import com.example.demo.repository.PersonajeRepository;
import java.io.IOException;
import org.springframework.web.socket.TextMessage;
import tools.jackson.databind.ObjectMapper;

@Component
public class GameManager {

    private long lastId = 0;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private final Map<WebSocketSession, GameInstance> sessionToGame = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Player> sessionToPlayer = new ConcurrentHashMap<>();
    private final Map<String, GameInstance> games = new ConcurrentHashMap<>();

    @Autowired
    private PersonajeRepository repo;

    public void onConnect(WebSocketSession session) {
        Player p = new Player(lastId++, session);
        sessionToPlayer.put(session, p);
    }

    public void handleIncoming(WebSocketSession session, String payload) throws IOException {

        JSONMessage json = new ObjectMapper().readValue(payload, JSONMessage.class);
        GameInstance game = sessionToGame.get(session);

        switch (json.messageType) {

            case CreateGameMessage_IN.TYPE:
                createGame(session);

                break;

            case JoinGameMessage_IN.TYPE:
                joinGame(session, json);
                break;
            case LeaveGame_IN.TYPE:

                if (game != null) {

                    Player p = sessionToPlayer.get(session);

                    game.removePlayer(p);

                    sessionToGame.remove(session);   // 👈 AQUÍ

                    game.broadcast(new JSONMessage(
                            game.getId(),
                            new PlayerJoined_OUT(
                                    p.getId(),
                                    p.getName(),
                                    game.getPlayers().size()
                            )
                    ));
                    
                    if (game.getPlayers().isEmpty()) {
                        games.remove(game.getId());
                    }
                }
                break;
            case GetGamesList_IN.TYPE:
                sendGamesList(session);
                break;
            case GameInfo_IN.TYPE: {
                if (game == null) {
                    session.sendMessage(new TextMessage(
                            new ObjectMapper().writeValueAsString(
                                    new JSONMessage(null,
                                            new ActionResult_OUT(false, 5) // no estás en sala
                                    )
                            )
                    ));
                    return;
                }

                List<PlayerInfo> players = game.getPlayers()
                        .stream()
                        .map(p -> new PlayerInfo(p.getId(), p.getName(), null))
                        .toList();

                GameFastInfo info = new GameFastInfo(game.getId(), players);

                game.send(session,
                        new JSONMessage(game.getId(), info)
                );

                break;
            }
            default:
                game = sessionToGame.get(session);
                if (game != null) {
                    game.enqueue(new GameMessage(sessionToPlayer.get(session), payload));
                }
                break;
        }
    }

    private void createGame(WebSocketSession session) {

        // 🔥 SI YA ESTÁ EN UNA PARTIDA → BLOQUEAR
        if (sessionToGame.containsKey(session)) {
            GameInstance game = sessionToGame.get(session);
            game.send(session,
                    new JSONMessage(
                            game.getId(),
                            new ActionResult_OUT(false, 4)
                    )
            );
            return;
        }

        List<Player> players = new ArrayList<>();
        players.add(sessionToPlayer.get(session));

        List<Personaje> personajes = repo.findBySeleccionableTrue();

        GameInstance game = new GameInstance(players, executor, personajes);

        sessionToGame.put(session, game);
        games.put(game.getId(), game);

        game.start();
    }

    private void joinGame(WebSocketSession session, JSONMessage json) {
        JoinGameMessage_IN data
                = new ObjectMapper().treeToValue(json.data, JoinGameMessage_IN.class
                );

        GameInstance game = games.get(data.gameId);

        if (sessionToGame.containsKey(session)) {
            game.send(session,
                    new JSONMessage(
                            game.getId(),
                            new ActionResult_OUT(false, 4)
                    )
            );
            return;
        }

        if (game == null) {
            return;
        }
        if (game.isFull()) {

            game.send(session,
                    new JSONMessage(
                            game.getId(),
                            new ActionResult_OUT(false, 3)
                    )
            );

            return; // ❌ NO entra
        }
        Player player = sessionToPlayer.get(session);

        // 🔥 añadir jugador
        game.getPlayers().add(player);

        // 🔥 guardar relación sesión -> game
        sessionToGame.put(session, game);

        // =========================
        // 📢 1. BROADCAST a todos
        // =========================
        game.broadcast(new JSONMessage(
                game.getId(),
                new PlayerJoined_OUT(
                        player.getId(),
                        player.getName(),
                        game.getPlayers().size()
                )
        ));

        // =========================
        // 🎮 2. ENVIAR personajes SOLO al nuevo jugador
        // =========================
        game.send(session,
                new JSONMessage(
                        game.getId(),
                        new CharactersList_OUT(game.getPersonajesDisponibles())
                )
        );
    }

    private void sendGamesList(WebSocketSession session) {

        List<GameFastInfo> list = new ArrayList<>();

        for (GameInstance game : games.values()) {

            List<PlayerInfo> players = new ArrayList<>();

            for (Player p : game.getPlayers()) {
                players.add(new PlayerInfo(p.getId(), p.getName(), null));
            }

            list.add(new GameFastInfo(game.getId(), players));
        }

        JSONMessage msg = new JSONMessage(
                null,
                new GamesList_OUT(list)
        );

        try {
            session.sendMessage(new org.springframework.web.socket.TextMessage(
                    new ObjectMapper().writeValueAsString(msg)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
