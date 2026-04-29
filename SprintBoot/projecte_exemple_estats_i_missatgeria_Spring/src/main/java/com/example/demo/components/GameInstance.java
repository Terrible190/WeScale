package com.example.demo.components;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.web.socket.WebSocketSession;

import com.example.demo.api.model.Player;
import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.states.State;
import com.example.demo.api.model.states.StateLobby;

public class GameInstance {

    private final List<Player> players;
    private final BlockingQueue<GameMessage> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile boolean active = true;

    private final String id;
    private State currentState;

    // 🔥 LÓGICA NUEVA
    private List<Personaje> personajesDisponibles = new ArrayList<>();
    private Map<Long, Personaje> seleccionados = new HashMap<>();

    public GameInstance(List<Player> players, ExecutorService executor, List<Personaje> personajes) {
        this.players = players;
        this.executor = executor;
        this.id = UUID.randomUUID().toString();
        this.personajesDisponibles = personajes;

        currentState = new StateLobby(this);
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            executor.submit(this::loop);
        }
    }

    private void loop() {
        while (active) {
            try {
                currentState.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public GameMessage pollMessage(int time, TimeUnit unit) {
        try {
            return queue.poll(time, unit);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void enqueue(GameMessage msg) {
        queue.offer(msg);
    }

    public void broadcast(JSONMessage msg) {
        for (Player p : players) {
            send(p.getSession(), msg);
        }
    }

    public void send(WebSocketSession session, JSONMessage msg) {
        try {
            session.sendMessage(new org.springframework.web.socket.TextMessage(
                    new tools.jackson.databind.ObjectMapper().writeValueAsString(msg)
            ));
        } catch (Exception e) {
        }
    }

    public synchronized boolean pickCharacter(Player player, long characterId) {

        // ya tiene personaje
        if (seleccionados.containsKey(player.getId())) {
            return false;
        }

        // buscar personaje en disponibles
        Personaje character = personajesDisponibles.stream()
                .filter(x -> x.getId() == characterId)
                .findFirst()
                .orElse(null);

        if (character == null) {
            return false;
        }

        // ya elegido por otro jugador (redundante pero seguro)
        if (seleccionados.containsValue(character)) {
            return false;
        }

        // 🔥 reservar personaje
        seleccionados.put(player.getId(), character);

        // 🔥 quitar de disponibles
        personajesDisponibles.remove(character);

        return true;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }

    public List<Personaje> getPersonajesDisponibles() {
        return personajesDisponibles;
    }

    public Map<Long, Personaje> getSeleccionados() {
        return seleccionados;
    }
}
