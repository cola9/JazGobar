package com.example;

import java.util.Date;
import java.util.UUID;

/**
 * Created by nikolajcolic on 25/02/17.
 */

public class Lokacija {
    private String id;
    private String ime;
    private double x,y;
    private String slika;
    private long datum;
    private boolean stanje;
    private String idUser;
    private boolean deli;

    public boolean getDeli() {
        return deli;
    }

    public void setDeli(boolean deli) {
        this.deli = deli;
    }

    public Lokacija(String ime, double x, double y, String slika, long datum, boolean stanje, String idUser) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.ime = ime;
        this.x = x;
        this.y = y;
        this.slika = slika;
        this.datum = datum;
        this.stanje = stanje;
        this.idUser = idUser;
        this.deli=false;
    }

    @Override
    public String toString() {

        return ime;
    }
    public String toString2() {
        return "\nLokacija{" +
                "id='" + id + '\'' +
                ", ime='" + ime + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", slika='" + slika + '\'' +
                ", datum=" + datum +
                ", stanje=" + stanje +
                ", idUser='" + idUser + '\'' +
                '}';
    }
    public String getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getSlika() {
        return slika;
    }

    public long getDatum() {
        return datum;
    }

    public boolean isStanje() {
        return stanje;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setSlika(String slika) {
        this.slika = slika;
    }

    public void setDatum(long datum) {
        this.datum = datum;
    }

    public void setStanje(boolean stanje) {
        this.stanje = stanje;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

}
