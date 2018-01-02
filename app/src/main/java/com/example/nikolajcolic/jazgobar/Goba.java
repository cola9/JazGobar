package com.example.nikolajcolic.jazgobar;

/**
 * Created by nikolajcolic on 02/01/2018.
 */

public class Goba {
    private int vlaznost;
    private double temperatura;
    private int pritisk;
    private boolean nabral;

    public Goba(int vlaznost, double temperatura, int pritisk, boolean nabral) {
        this.vlaznost = vlaznost;
        this.temperatura = temperatura;
        this.pritisk = pritisk;
        this.nabral = nabral;
    }

    public int getVlaznost() {
        return vlaznost;
    }

    public void setVlaznost(int vlaznost) {
        this.vlaznost = vlaznost;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public int getPritisk() {
        return pritisk;
    }

    public void setPritisk(int pritisk) {
        this.pritisk = pritisk;
    }

    public boolean isNabral() {
        return nabral;
    }

    public void setNabral(boolean nabral) {
        this.nabral = nabral;
    }
}
