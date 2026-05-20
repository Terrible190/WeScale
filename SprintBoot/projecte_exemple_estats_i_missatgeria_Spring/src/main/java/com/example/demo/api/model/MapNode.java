package com.example.demo.api.model;

import java.util.List;

public class MapNode {

    public int pis;
    public String tipus;
    public boolean completat;
    public boolean desbloquejat;
    public List<EnemyInstance> enemics;



    public MapNode(int pis, String tipus, List<EnemyInstance> enemics) {
        this.pis = pis;
        this.tipus = tipus;
        this.enemics = enemics;
    }
}