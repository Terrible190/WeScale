package com.example.demo.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name = "EFECTE")
public class Efecto {

    @Id
    @Column(name = "id_efecte")
    private int id;

    @Column(name = "id_tipus_efecte")
    private int tipo;

    @Column(name = "tipus_dany")
    private Integer tipoDanyo;

    @Column(name = "rang")
    private Integer rango;

    @Column(name = "duracio")
    private Integer duracion;

    @Column(name = "valor")
    private Float valor;

    @Column(name = "operacio")
    private Integer operacion;

    @Column(name = "nom_stat")
    private Integer stat;
    public Efecto()
    {

    }

    public Efecto(
            int id,
            int tipo,
            Integer tipoDanyo,
            Integer rango,
            Integer duracion
    ) {
        this.id = id;
        this.tipo = tipo;
        this.tipoDanyo = tipoDanyo;
        this.rango = rango;
        this.duracion = duracion;
    }

    public Integer getTipoDanyo() {
        return tipoDanyo;
    }

    public int getId() {
        return id;
    }

    public int getTipo() {
        return tipo;
    }

    public Integer getRango() {
        return rango;
    }
    public Float getValor() {
        return valor;
    }

    public Integer getOperacion() {
        return operacion;
    }

    public Integer getStat() {
        return stat;
    }
    public Integer getDuracion() {
        return duracion;
    }
}