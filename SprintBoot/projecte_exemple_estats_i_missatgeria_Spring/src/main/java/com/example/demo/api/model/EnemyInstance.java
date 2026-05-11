package com.example.demo.api.model;

public class EnemyInstance {

    private static long NEXT_ID = 1;

    private long instanceId; // 👈 ID único de combate
    private Personaje base;

    private float hp;
    private float danyoFisico;
    private float danyoMagico;
    private float defensaFisica;
    private float defensaMagica;
    private float critico;

    public EnemyInstance(Personaje base, float scale) {

        this.instanceId = NEXT_ID++;

        this.base = base;

        this.hp = base.getHp() * scale;
        this.danyoFisico = base.getDanyoFisico() * scale;
        this.danyoMagico = base.getDanyoMagico() * scale;
        this.defensaFisica = base.getDefensaFisica() * scale;
        this.defensaMagica = base.getDefensaMagica() * scale;
        this.critico = base.getCritico() * scale;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public Personaje getBase() {
        return base;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}