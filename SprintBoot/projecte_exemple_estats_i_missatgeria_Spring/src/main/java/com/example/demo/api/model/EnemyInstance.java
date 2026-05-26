package com.example.demo.api.model;

public class EnemyInstance {

    private static long NEXT_ID = 1;

    private long instanceId; // 👈 ID único de combate
    private Personaje base;
    private float velocidad;
    public EnemyInstance(Personaje base, float scale) {

        this.instanceId = NEXT_ID++;

        this.base = base;

        float scaledHp = base.getCurrentHp() * scale;

        this.base.setHp(scaledHp);
        this.base.setCurrentHp(scaledHp);
        this.base.setDanyoFisico(base.getDanyoFisico() * scale);
        this.base.setDanyoMagico(base.getDanyoMagico() * scale);
        this.base.setDefensaFisica(base.getDefensaFisica() * scale);
        this.base.setDefensaMagica(base.getDefensaMagica() * scale);
        this.base.setCritico(base.getCritico() * scale);
    }
    public float getVelocidad()
    {
        return velocidad;
    }
    public long getInstanceId() {
        return instanceId;
    }

    public Personaje getBase() {
        return base;
    }
    public static void resetIds()
    {
        NEXT_ID = 1;
    }
}
