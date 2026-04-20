package com.example.demo.websocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.components.GameManager;

/**
 * La classe gestiona a baix nivell qualsevol connexió nova de WebSocket i desconnexió. Així mateix,
 * tots els missatges rebuts des de qualsevol WebSocket que estigui connectat passen per handleTextMessage().
 * 
 * La classe delega tota la feina a la classe GameManager per gestionar les peticions i dirigir-les a les partides
 * corresponents. 
 */
@Component
public class WSHandler extends TextWebSocketHandler {

    private final GameManager gameManager;

    public WSHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        gameManager.handleIncoming(session, message.getPayload());
	}

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
    {
        System.out.println("new connection stablished "+session.getId());
        gameManager.onConnect(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
         System.out.println("connection closed "+session.getId());
    }
    
}