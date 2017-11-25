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
    private String ikonaVremena;
    private String vlaznost;
    private String pritisk;
    private String temp;
    private int vlaznostH;
    private int pritiskH;
    private double tempH;

    public boolean getDeli() {
        return deli;
    }

    public void setDeli(boolean deli) {
        this.deli = deli;
    }

    public Lokacija(String ime, double x, double y, String slika, long datum, boolean stanje, String idUser, String ikonaVremena, String vlaznost, String pritisk, String temp, int vlaznostH, int pritiskH, double tempH) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.ime = ime;
        this.x = x;
        this.y = y;
        this.slika = slika;
        this.datum = datum;
        this.stanje = stanje;
        this.idUser = idUser;
        this.deli=false;
        this.ikonaVremena = ikonaVremena;
        this.vlaznost = vlaznost;
        this.pritisk = pritisk;
        this.temp = temp;
        this.vlaznostH = vlaznostH;
        this.pritiskH = pritiskH;
        this.tempH = tempH;
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


    public int getVlaznostH() {
        return vlaznostH;
    }

    public void setVlaznostH(int vlaznostH) {
        this.vlaznostH = vlaznostH;
    }

    public int getPritiskH() {
        return pritiskH;
    }

    public void setPritiskH(int pritiskH) {
        this.pritiskH = pritiskH;
    }

    public double getTempH() {
        return tempH;
    }

    public void setTempH(double tempH) {
        this.tempH = tempH;
    }


    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
    public String getIkonaVremena() {
        return ikonaVremena;
    }

    public void setIkonaVremena(String ikonaVremena) {
        this.ikonaVremena = ikonaVremena;
    }

    public String getVlaznost() {
        return vlaznost;
    }

    public void setVlaznost(String vlaznost) {
        this.vlaznost = vlaznost;
    }

    public String getPritisk() {
        return pritisk;
    }

    public void setPritisk(String pritisk) {
        this.pritisk = pritisk;
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
