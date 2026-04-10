/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author Anas
 */

public class GamePlayer {

    private WebSocketSession session;
    private Jugador jugador;

    public GamePlayer(WebSocketSession session, Jugador jugador) {
        this.session = session;
        this.jugador = jugador;
    }

    public WebSocketSession getSession() { return session; }
    public Jugador getJugador() { return jugador; }
}