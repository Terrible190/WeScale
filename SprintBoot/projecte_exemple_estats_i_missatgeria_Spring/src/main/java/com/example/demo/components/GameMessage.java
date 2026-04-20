package com.example.demo.components;

import com.example.demo.api.model.Player;

public record GameMessage(Player player, String payload) {}