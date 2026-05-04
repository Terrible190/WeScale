/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.messages.out.characters_to_pick.PlayerInfo;
import java.util.List;

/**
 *
 * @author Usuari
 */
public class GameFastInfo {

    public String id;
    public List<PlayerInfo> players;

    public GameFastInfo(String id, List<PlayerInfo> players) {
        this.id = id;
        this.players = players;
    }
}