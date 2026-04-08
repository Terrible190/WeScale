package com.example.demo.components;

import org.springframework.web.socket.WebSocketSession;

public record GameMessage(WebSocketSession session, String payload) {}