package com.example.demo.components;

import com.example.demo.api.model.Personaje;
import org.springframework.web.socket.WebSocketSession;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameInstance {

    private final ObjectMapper mapper = new ObjectMapper();

    private final List<WebSocketSession> players;
    private final ExecutorService executor;

    private final BlockingQueue<GameMessage> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean threadIsRunning = new AtomicBoolean(false);

    private volatile boolean gameActive = true;

    private List<Personaje> personajesDisponibles = new ArrayList<>();
    private Map<WebSocketSession, Personaje> personajesSeleccionados = new HashMap<>();

    private final String id;
    private boolean started = false;

    public GameInstance(List<WebSocketSession> players, ExecutorService executor) {
        this.players = players;
        this.executor = executor;
        this.id = UUID.randomUUID().toString();
    }

   public synchronized void start() {
        if (started) return;
        started = true;

        Object mapa = cargarMapa();

        broadcast(mapa);
    }

    public void setPersonajesDisponibles(List<Personaje> personajes) {
        this.personajesDisponibles = new ArrayList<>(personajes);
    }

    public synchronized boolean selectCharacter(WebSocketSession session, int id) {
        if (personajesSeleccionados.containsKey(session)) return false;

        Personaje p = personajesDisponibles.stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElse(null);

        if (p == null) return false;

        personajesDisponibles.remove(p);
        personajesSeleccionados.put(session, p);
        return true;
    }

    public synchronized boolean unselectCharacter(WebSocketSession session) {
        Personaje p = personajesSeleccionados.remove(session);
        if (p == null) return false;

        personajesDisponibles.add(p);
        return true;
    }

    public void sendCharactersToPlayer(WebSocketSession session) {
        send(session, new WSMessage("characters", personajesDisponibles));
    }

    public void removePlayer(WebSocketSession session) {
        Personaje p = personajesSeleccionados.remove(session);
        if (p != null) personajesDisponibles.add(p);
        players.remove(session);
    }

    public Personaje getCharacterOfPlayer(WebSocketSession session) {
        return personajesSeleccionados.get(session);
    }

    public void enqueue(GameMessage msg) {
        queue.offer(msg);
    }

    public String getId() { return id; }

    public List<Personaje> getPersonajesDisponibles() {
        return personajesDisponibles;
    }

    public List<WebSocketSession> getPlayers() {
        return players;
    }

    public void addPlayer(WebSocketSession session) {
        players.add(session);
    }

    public void broadcast(Object obj) {
        for (WebSocketSession player : players) {
            send(player, obj);
        }
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
    private Object cargarMapa() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("map_fixed.json");
            return mapper.readValue(is, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}