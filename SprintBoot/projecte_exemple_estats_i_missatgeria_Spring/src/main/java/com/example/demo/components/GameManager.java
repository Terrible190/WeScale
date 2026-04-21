package com.example.demo.components;

import java.util.*;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.api.model.Player;
import com.example.demo.api.model.Personaje;
import com.example.demo.repository.PersonajeRepository;
import com.example.demo.repository.PersonajeRepository;

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

    public void handleIncoming(WebSocketSession session, String payload) {
        GameInstance game = sessionToGame.get(session);

        if (game != null) {
            game.enqueue(new GameMessage(sessionToPlayer.get(session), payload));
        } else {
            createGame(session);
        }
    }

    private void createGame(WebSocketSession session) {

        List<Player> players = new ArrayList<>();
        players.add(sessionToPlayer.get(session));

        List<Personaje> personajes = repo.findBySeleccionableTrue();

        GameInstance game = new GameInstance(players, executor, personajes);

        sessionToGame.put(session, game);
        games.put(game.getId(), game);

        game.start();
    }
}