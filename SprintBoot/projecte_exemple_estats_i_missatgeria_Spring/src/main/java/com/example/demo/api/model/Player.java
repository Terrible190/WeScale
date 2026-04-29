package com.example.demo.api.model;

import org.springframework.web.socket.WebSocketSession;

public class Player {

    long id;
    WebSocketSession session;
    String name;

    private boolean ready = false;

    public Player(long id, WebSocketSession session) {
        this.id = id;
        this.session = session;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

}
