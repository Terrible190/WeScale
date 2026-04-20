package com.example.demo.components;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.WebSocketSession;

import com.example.demo.api.model.Player;

import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.states.State;
import com.example.demo.api.model.states.StatePickCharacter;

import tools.jackson.databind.ObjectMapper;

import org.springframework.web.socket.TextMessage;


import java.io.IOException;

import java.util.concurrent.atomic.AtomicBoolean;

import java.util.UUID;

/**
 * Classe que representa una partida i tot el seu estat:
 *  - jugadors connectats
 *  - cua de missatges
 *  - estat intern del joc (@TODO)
 */
public class GameInstance {

    /** Les connexions dels jugadors de la partida  */
    private final List<Player> players;

    /** Llista de missatges per gestionaro (INBOX) */
    private final BlockingQueue<GameMessage> queue = new LinkedBlockingQueue<>();

    /** Executor de fils */
    private final ExecutorService executor;

    /** Mutex per controlar que el joc només tingui 1 fil en execució */
    private final AtomicBoolean threadIsRunning = new AtomicBoolean(false);

    /** Indica si la partida està en curs (true) o ha acabat (false) */
    private volatile boolean gameActive = true;
    
    /** Id de la partida (UUID) */
    private final String id;

    /**
     * Estat del joc
     */
    private State currentState;


    public List<Player> getPlayers(){
        return this.players;
    }

    /**
     * Permet canviar l'estat actual de la partida
     * @param newState el nou estat
     */
    public void setState(State newState){
        if(newState==null) throw new RuntimeException("No podem posar un estat null");
        currentState = newState;
    }

    /**
     * Constructor de la instància de joc
     * @param players
     * @param executor
     */
    public GameInstance(List<Player> players, ExecutorService executor) {
        this.id = UUID.randomUUID().toString();
        this.players = players;
        this.executor = executor;
        System.out.println("Starting the game instance.");
        // Assignem l'estat actual
        currentState = new StatePickCharacter(this);
    }

    /** Iniciar fil de la partida */
    public void start(){
        if(threadIsRunning.compareAndSet(false, true)){
            System.out.println("The thread has been started.");
            executor.submit(this::processLoop);
        }
    }

    /** Encuar missatge d'un player */
    public void enqueue(GameMessage message) {
        queue.offer(message); 
    }
    /** retorna l'ID de la partida */
    public String getId() {
        return id;
    }

    /**
     * mètode per llegir missatge de la cua de missatges entrants de joc
     * @param time de timeout
     * @param tu
     * @return el missatge, o null si no hi ha pendents durant tot el temps estipulat.
     */
    public GameMessage pollMessage(int time, TimeUnit tu){
        try {

            return queue.poll(time, tu);

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

     /**
     * mètode per llegir missatge de la cua de missatges entrants de joc (sense bloqueig)
     * @return el missatge, o null si no hi ha pendents.
     */
    public GameMessage pollMessage(){
        return queue.poll();
    }



    /** 
     * THE GAME LOOP () () () () () () () () () () () ()
     */
    private void processLoop() {
   
        while (gameActive) {
            try {
                currentState.tick();
            } catch (Exception e) {
                System.out.println("Error en el game loop:");
                e.printStackTrace();
            }
        }
    }

    /** 
     * Funció d'utilitat per enviar un missatge  genèric (que es serialitza
     *  a JSON) a una connexió concreta */
    public void send(WebSocketSession session, JSONMessage jsonMsg) {
        ObjectMapper mapper = new ObjectMapper();
        send( session, mapper.writeValueAsString(jsonMsg));
    }

    /** Funció d'utilitat per enviar un missatge a una connexió concreta */
    private void send(WebSocketSession session, String text) {
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(text));
            }
        } catch (IOException e) {
            // handle disconnect
        }
    }

    /** Permet enviar a tots els players un missatge genèric (que es serialitza a JSON) */
    public void broadcast(JSONMessage jsonMsg){
        ObjectMapper mapper = new ObjectMapper();
        broadcast(mapper.writeValueAsString(jsonMsg));
    }

    /** Funció d'utilitat per enviar un missatge de text a tots els ususaris de la partida */
    private void broadcast(String text) {
        for (Player player : players) {
            send(player.getSession(), text);
        }
    }

    public void stop() {
        gameActive = false;
    }
}