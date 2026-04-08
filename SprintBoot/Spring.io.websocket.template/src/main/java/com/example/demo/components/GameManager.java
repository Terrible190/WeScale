package com.example.demo.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        game.broadcast("Player left. Total players: " + game.getPlayers().size());

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
                joinGame(session, parts[1]);
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

        // ✅ Nuevo comando
        if (msg.equalsIgnoreCase("listgames")) {
            listGames(session);
            return;
        }
        if (msg.equalsIgnoreCase("leave")) {
            leaveGame(session);
            return;
        }

        // Mensajes normales del juego
        GameInstance game = sessionToGame.get(session);
        if (game != null) {
            game.enqueue(new GameMessage(session, message));
        }

    }

    private void createGame(WebSocketSession session) {
        // ✅ Verificar que no esté en otra partida
        if (sessionToGame.containsKey(session)) {
            try {
                session.sendMessage(new TextMessage("You are already in a game! Leave it first."));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        List<WebSocketSession> players = new ArrayList<>();
        players.add(session);

        GameInstance game = new GameInstance(players, executor);
        String gameId = game.getId();

        sessionToGame.put(session, game);
        games.put(gameId, game);

        game.broadcast("Game created with ID: " + gameId + ". Total players: " + game.getPlayers().size());

        System.out.println("Game created with ID: " + gameId);
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
