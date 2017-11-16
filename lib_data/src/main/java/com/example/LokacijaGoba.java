package com.example;
import java.util.UUID;
/**
 * Created by nikolajcolic on 25/02/17.
 */

public class LokacijaGoba {
    private String id;
    Goba goba;
    private String  idLokacija;
    private String idGoba;
    private long datum;
    private String idUser;
    public String getHtmlFromat() {
        if (goba.isChecked()) {
            return "<b>"+goba.getIme()+"<//b>";
        } else {
            return goba.getIme();
        }
    }
    public LokacijaGoba(String idLokacija, Goba goba, long datum, String idUser) {
        this.id=UUID.randomUUID().toString().replaceAll("-", "");
        this.idLokacija = idLokacija;
        this.goba = goba;
        this.datum = datum;
        this.idUser = idUser;
    }

    public void setGoba(Goba goba) {
        this.goba = goba;
    }

    public Goba getGoba() {
        return goba;
    }

    @Override
    public String toString() {
        return "\nLokacijaGoba{" +
                "id='" + id + '\'' +
                ", idLokacija=" + idLokacija +
                ", idGoba=" + idGoba +
                ", datum=" + datum +
                ", idUser=" + idUser +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdLokacija() {
        return idLokacija;
    }

    public void setIdLokacija(String idLokacija) {
        this.idLokacija = idLokacija;
    }

    public String getIdGoba() {
        return idGoba;
    }

    public void setIdGoba(String idGoba) {
        this.idGoba = idGoba;
    }

    public long getDatum() {
        return datum;
    }

    public void setDatum(long datum) {
        this.datum = datum;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
