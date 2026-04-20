package com.example.demo.components;

import com.example.demo.api.model.Personaje;
import com.example.demo.api.repository.PersonajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GameManager {

    private final ObjectMapper mapper = new ObjectMapper();
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private final Map<WebSocketSession, GameInstance> sessionToGame = new ConcurrentHashMap<>();
    private final Map<String, GameInstance> games = new ConcurrentHashMap<>();

    @Autowired
    private PersonajeRepository personajeRepository;

    public void onConnect(WebSocketSession session) {
        System.out.println("Jugador conectado: " + session.getId());
    }

    public void handleIncoming(WebSocketSession session, String message) {
        try {
            WSMessage msg = mapper.readValue(message, WSMessage.class);

           String type = msg.getType(); // 🔥 cambio clave
            GameInstance game = sessionToGame.get(session);

            switch (type) {

                case "START_GAME":
                    createGame(session);
                    break;

                case "JOIN":
                    joinGame(session, (String) msg.getData());
                    break;

                case "LEAVE":
                    leaveGame(session);
                    break;

                case "CHARACTERS":
                    if (game != null) {
                        game.sendCharactersToPlayer(session);
                    }
                    break;

                case "PICK":
                    if (game != null) {
                        int id = ((Number) msg.getData()).intValue(); // 🔥 fix
                        boolean ok = game.selectCharacter(session, id);

                        if (ok) {
                            game.broadcast(new WSMessage(
                                    "CHARACTER_SELECTED",
                                    game.getPersonajesDisponibles().size()
                            ));
                        } else {
                            send(session, new WSMessage("ERROR", "Personaje inválido"));
                        }
                    }
                    break;

                case "UNSELECT":
                    if (game != null) {
                        boolean ok = game.unselectCharacter(session);
                        send(session, new WSMessage(
                                ok ? "OK" : "ERROR",
                                ok ? "Deseleccionado" : "No habías elegido"
                        ));
                    }
                    break;

                case "START":
                    if (game != null) {
                        game.start();
                    }
                    break;

                case "MY_CHARACTER":
                    if (game != null) {
                        Personaje p = game.getCharacterOfPlayer(session);
                        send(session, new WSMessage(
                                p != null ? "MY_CHARACTER" : "ERROR",
                                p != null ? p : "No seleccionado"
                        ));
                    }
                    break;

                case "LIST_GAMES":
                    listGames(session);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGame(WebSocketSession session) {
        if (sessionToGame.containsKey(session)) {
            return;
        }

        List<WebSocketSession> players = new ArrayList<>();
        players.add(session);

        List<Personaje> personajes = personajeRepository.findBySeleccionableTrue();

        GameInstance game = new GameInstance(players, executor);
        game.setPersonajesDisponibles(personajes);

        sessionToGame.put(session, game);
        games.put(game.getId(), game);

        game.broadcast(new WSMessage("game_created", game.getId()));
        game.broadcast(new WSMessage("INFO", "Game created"));
        game.sendCharactersToPlayer(session);
    }

    private void joinGame(WebSocketSession session, String gameId) {
        if (sessionToGame.containsKey(session)) {
            send(session, new WSMessage("error", "Ya estás en una partida"));
            return;
        }

        GameInstance game = games.get(gameId);
        if (game == null) {
            send(session, new WSMessage("error", "Partida no encontrada"));
            return;
        }

        if (game.getPlayers().size() >= 5) {
            send(session, new WSMessage("error", "Partida llena"));
            return;
        }

        game.addPlayer(session);
        sessionToGame.put(session, game);

        game.broadcast(new WSMessage(
                "player_joined",
                game.getPlayers().size()
        ));
    }

    public void leaveGame(WebSocketSession session) {
        GameInstance game = sessionToGame.get(session);
        if (game == null) {
            send(session, new WSMessage("error", "No estás en ninguna partida"));
            return;
        }

        game.removePlayer(session);
        sessionToGame.remove(session);

        game.broadcast(new WSMessage("player_left", game.getPlayers().size()));
        game.broadcast(new WSMessage("characters", game.getPersonajesDisponibles()));

        if (game.getPlayers().isEmpty()) {
            games.remove(game.getId());
        }
    }

    private void listGames(WebSocketSession session) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Map.Entry<String, GameInstance> entry : games.entrySet()) {
            Map<String, Object> g = new HashMap<>();
            g.put("id", entry.getKey());
            g.put("players", entry.getValue().getPlayers().size());
            g.put("max", 4);
            list.add(g);
        }

        send(session, new WSMessage("games", list));
    }

    private void send(WebSocketSession session, Object obj) {
        try {
            String json = mapper.writeValueAsString(obj);
            synchronized (session) {
                session.sendMessage(new org.springframework.web.socket.TextMessage(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
