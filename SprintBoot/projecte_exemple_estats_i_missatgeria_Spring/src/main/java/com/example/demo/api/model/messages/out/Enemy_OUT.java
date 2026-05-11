/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.out;

/**
 *
 * @author Usuari
 */
import com.example.demo.api.model.EnemyInstance;
import com.example.demo.api.model.messages.MessageBody;
import java.util.List;

public class Enemy_OUT extends MessageBody {

    public static final String TYPE = "Enemies";
    public int piso;
    public List<EnemyInstance> enemies;

    public Enemy_OUT(List<EnemyInstance> enemies, int piso) {
        this.enemies = enemies;
        this.piso = piso;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}