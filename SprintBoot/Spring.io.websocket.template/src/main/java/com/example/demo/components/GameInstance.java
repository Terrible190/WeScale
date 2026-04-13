package com.example.demo.components;

import com.example.demo.api.model.Personaje;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicBoolean;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe que representa una partida i tot el seu estat: - jugadors connectats -
 * cua de missatges - estat intern del joc (@TODO)
 */
public class GameInstance {

    /**
     * Les connexions dels jugadors de la partida
     */
    private final List<WebSocketSession> players;

    /**
     * Llista de missatges per gestionaro (INBOX)
     */
    private final BlockingQueue<GameMessage> queue = new LinkedBlockingQueue<>();

    /**
     * Executor de fils
     */
    private final ExecutorService executor;

    /**
     * Mutex per controlar que el joc només tingui 1 fil en execució
     */
    private final AtomicBoolean threadIsRunning = new AtomicBoolean(false);

    /**
     * Indica si la partida està en curs (true) o ha acabat (false)
     */
    private volatile boolean gameActive = true;

    /**
     * Id de la partida (UUID)
     */
    private List<Personaje> personajesDisponibles = new java.util.ArrayList<>();
    private Map<WebSocketSession, Personaje> personajesSeleccionados = new HashMap<>();

    private final String id;
    private boolean started = false;

    public synchronized void start() {
        if (started) {
            return;
        }
        started = true;

        System.out.println("Partida iniciada: " + id);

        // lógica del juego
    }

    public GameInstance(List<WebSocketSession> players, ExecutorService executor) {
        this.id = UUID.randomUUID().toString();
        this.players = players;
        this.executor = executor;

        System.out.println("Iniciando game instance.");
    }

    public void setPersonajesDisponibles(List<Personaje> personajes) {
        this.personajesDisponibles = new ArrayList<>(personajes);
    }

    public synchronized boolean selectCharacter(WebSocketSession session, int personajeId) {

        // ya eligió personaje
        if (personajesSeleccionados.containsKey(session)) {
            return false;
        }

        // buscar personaje disponible
        Personaje elegido = personajesDisponibles.stream()
                .filter(p -> p.getId() == personajeId)
                .findFirst()
                .orElse(null);

        if (elegido == null) {
            return false; // no existe o ya fue tomado
        }

        // bloquear selección
        personajesDisponibles.remove(elegido);
        personajesSeleccionados.put(session, elegido);

        return true;
    }

    public synchronized boolean unselectCharacter(WebSocketSession session) {

        // comprobar si el jugador tenía personaje
        Personaje actual = personajesSeleccionados.remove(session);

        if (actual == null) {
            return false; // no tenía ninguno seleccionado
        }

        // devolverlo a disponibles
        personajesDisponibles.add(actual);

        return true;
    }

    public void sendCharactersToPlayer(WebSocketSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("Personajes:\n");

        for (Personaje p : personajesDisponibles) {
            sb.append(p.getId())
                    .append(" - ")
                    .append(p.getNombre())
                    .append("\n");
        }

        send(session, sb.toString());
    }

    public String sendCharactersToPlayer() {
        StringBuilder sb = new StringBuilder();
        sb.append("Personajes:\n");

        for (Personaje p : personajesDisponibles) {
            sb.append(p.getId())
                    .append(" - ")
                    .append(p.getNombre())
                    .append("\n");
        }

        return sb.toString();
    }

    public void removePlayer(WebSocketSession session) {

        Personaje p = personajesSeleccionados.remove(session);

        if (p != null) {
            personajesDisponibles.add(p);
        }

        players.remove(session);
    }

    public Personaje getCharacterOfPlayer(WebSocketSession session) {
        return personajesSeleccionados.get(session);
    }

    /**
     * Encuar missatge d'un player
     */
    public void enqueue(GameMessage message) {
        queue.offer(message);
    }

    /**
     * retorna l'ID de la partida
     */
    public String getId() {
        return id;
    }

    private void processLoop() {
        try {
            while (gameActive) {

                GameMessage msg = queue.poll(15, TimeUnit.SECONDS);

                if (msg != null) {
                    String payload = msg.payload();
                    System.out.println("Game instance: Message received");
                    if ("ping".equalsIgnoreCase(payload)) {
                        send(msg.session(), "pong");
                    }
                } else {
                    System.out.println("Game instance: timeout in message reception.");
                    broadcast("Timeout occurred");
                }

            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Funció d'utilitat per enviar un missatge a una connexió concreta
     */
    private void send(WebSocketSession session, String text) {
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(text));
            }
        } catch (IOException e) {
            // handle disconnect
        }
    }

    public List<Personaje> getPersonajesDisponibles() {
        return personajesDisponibles;
    }

    /**
     * Funció d'utilitat per enviar un missatge a tots els ususaris de la
     * partida
     */
    public void broadcast(String message) {
        for (WebSocketSession player : players) {
            try {
                if (player.isOpen()) {
                    player.sendMessage(new org.springframework.web.socket.TextMessage(message));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<WebSocketSession> getPlayers() {
        return players;
    }

    public void addPlayer(WebSocketSession session) {
        players.add(session);
    }

    public void stop() {
        gameActive = false;
    }
}
