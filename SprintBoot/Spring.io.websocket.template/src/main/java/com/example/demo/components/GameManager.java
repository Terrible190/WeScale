package com.example.demo.components;

import com.example.demo.api.model.Personaje;
import com.example.demo.api.repository.PersonajeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * El gestor de joc es responsabilitza de gestionar les noves connexions WS i
 * crear les partides quan hi ha prous jugadors. Un cop establert el WS amb cada
 * jugador, també s'encarrega de recepcionar els missatges dels WS ja
 * connectats, redirigint-los a les partides pertinents.
 */
@Component
public class GameManager {

    /**
     * Executor de fils
     */
    private ExecutorService executor;

    /**
     * Cua de jugadors pendents d'assignar a partida
     */
    private final Queue<WebSocketSession> waitingPlayers = new ConcurrentLinkedQueue<>();

    /**
     * mapa que relaciona Ids de sessió de websocket(String) amb partides
     * actuals (GameInstance). Un websocket session id pertany a un únic player
     * connectat.
     */
    private final Map<WebSocketSession, GameInstance> sessionToGame = new ConcurrentHashMap<>();

    /**
     * Índex de GameInstance per game_id
     */
    private final Map<String, GameInstance> games = new ConcurrentHashMap<>();

    @Autowired
    private PersonajeRepository personajeRepository;

    public GameManager() {
        executor = Executors.newFixedThreadPool(10);
    }

    public void onConnect(WebSocketSession session) {
        System.out.println("Player connected: " + session.getId());

    }

    public void leaveGame(WebSocketSession session) {
        GameInstance game = sessionToGame.get(session);
        if (game == null) {
            try {
                session.sendMessage(new org.springframework.web.socket.TextMessage("You are not in any game."));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        game.removePlayer(session);
        sessionToGame.remove(session);

        // Notificar a los jugadores restantes
        game.broadcast("Un jugador ha salido: " + game.getPlayers().size());
        game.broadcast("Personajes disponibles:" + game.sendCharactersToPlayer());

        // Si la sala queda vacía, eliminar el juego
        if (game.getPlayers().isEmpty()) {
            games.remove(game.getId());
            System.out.println("Game removed: " + game.getId());
        }
    }

    public void handleIncoming(WebSocketSession session, String message) {
        String msg = message.trim();

        if (msg.equalsIgnoreCase("startgame")) {
            createGame(session);
            return;
        }

        if (msg.startsWith("join")) {
            String[] parts = msg.split(" ");
            if (parts.length == 2) {
                GameInstance game = sessionToGame.get(session);
                joinGame(session, parts[1]);
                if (game != null) {
                    game.sendCharactersToPlayer(session);
                }
            }

            return;
        }
        if (msg.equalsIgnoreCase("unselect")) {

            GameInstance game = sessionToGame.get(session);

            if (game != null) {
                boolean ok = game.unselectCharacter(session);

                try {
                    if (ok) {
                        session.sendMessage(new TextMessage("Character unselected."));
                    } else {
                        session.sendMessage(new TextMessage("You don't have a character selected."));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return;
        }
        if (msg.equalsIgnoreCase("start")) {
            GameInstance game = sessionToGame.get(session);
            if (game != null) {
                game.start();
            }
            return;
        }

        if (msg.equalsIgnoreCase("listgames")) {
            listGames(session);
            return;
        }
        if (msg.equalsIgnoreCase("leave")) {
            leaveGame(session);
            return;
        }
        if (msg.equalsIgnoreCase("characters")) {

            GameInstance game = sessionToGame.get(session);

            if (game != null) {
                game.sendCharactersToPlayer(session);
            }

            return;
        }

        if (msg.equalsIgnoreCase("mycharacter")) {

            GameInstance game = sessionToGame.get(session);

            if (game != null) {
                Personaje p = game.getCharacterOfPlayer(session);

                if (p != null) {
                    try {
                        session.sendMessage(new TextMessage(
                                "Your character: " + p.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        session.sendMessage(new TextMessage("You have not selected a character yet."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return;
        }

        if (msg.startsWith("pick")) {

            String[] parts = msg.split(" ");

            if (parts.length == 2) {
                GameInstance game = sessionToGame.get(session);

                if (game != null) {
                    boolean ok = game.selectCharacter(session, Integer.parseInt(parts[1]));

                    if (ok) {
                        game.broadcast("Character selected. Remaining characters: "
                                + game.getPersonajesDisponibles().size());
                    } else {
                        try {
                            session.sendMessage(new TextMessage("Character already taken or invalid ID."));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return;
        }

        GameInstance game = sessionToGame.get(session);
        if (game != null) {
            game.enqueue(new GameMessage(session, message));
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

        String gameId = game.getId();

        sessionToGame.put(session, game);
        games.put(gameId, game);

        game.broadcast("Game created: " + gameId);

        // enviar personajes al creador
        game.sendCharactersToPlayer(session);
    }

    private void joinGame(WebSocketSession session, String gameId) {
        // ✅ Verificar que no esté en otra partida
        if (sessionToGame.containsKey(session)) {
            try {
                session.sendMessage(new TextMessage("You are already in a game! Leave it first."));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        GameInstance game = games.get(gameId);
        if (game == null) {
            try {
                session.sendMessage(new TextMessage("Game not found: " + gameId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (game.getPlayers().size() >= 5) {
            try {
                session.sendMessage(new TextMessage("Game is full"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        game.addPlayer(session);
        sessionToGame.put(session, game);

        game.broadcast("Player joined! Total players: " + game.getPlayers().size());

        System.out.println("Player joined game: " + gameId + " (total: " + game.getPlayers().size() + ")");
    }

    private void tryStartGame() {
        System.out.println("Number of waiting players:" + waitingPlayers.size());

        int minPlayers = 1;
        int maxPlayers = 5;

        if (waitingPlayers.size() >= minPlayers) {

            int numPlayers = Math.min(waitingPlayers.size(), maxPlayers);

            List<WebSocketSession> players = new java.util.ArrayList<>();

            for (int i = 0; i < numPlayers; i++) {
                WebSocketSession player = waitingPlayers.poll();
                if (player != null) {
                    players.add(player);
                }
            }

            GameInstance game = new GameInstance(players, executor);
            String gameId = game.getId();

            // Asociar sesiones al juego
            players.forEach(s -> sessionToGame.put(s, game));

            // Guardar juego
            games.put(gameId, game);

            // Iniciar partida
            game.start();
        }
    }

    private void listGames(WebSocketSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("Active games:\n");

        for (Map.Entry<String, GameInstance> entry : games.entrySet()) {
            String gameId = entry.getKey();
            GameInstance game = entry.getValue();
            int currentPlayers = game.getPlayers().size();
            int maxPlayers = 5; // máximo definido
            sb.append(gameId).append(": ").append(currentPlayers).append("/").append(maxPlayers).append("\n");
        }

        try {
            session.sendMessage(new org.springframework.web.socket.TextMessage(sb.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
