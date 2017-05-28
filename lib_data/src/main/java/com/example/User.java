package com.example;

/**
 * Created by nikolajcolic on 25/02/17.
 */

public class User {
    private String idUser;
    private String vzdevek;
    private String geslo;


    public User(String idUser, String vzdevek, String geslo) {

        this.idUser = idUser;
        this.vzdevek = vzdevek;
        this.geslo = geslo;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setVzdevek(String vzdevek) {
        this.vzdevek = vzdevek;
    }

    public void setGeslo(String geslo) {
        this.geslo = geslo;
    }

    public String getIdUser() {

        return idUser;
    }

    public String getVzdevek() {
        return vzdevek;
    }

    public String getGeslo() {
        return geslo;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser='" + idUser + '\'' +
                ", vzdevek='" + vzdevek + '\'' +
                ", geslo='" + geslo + '\'' +
                '}';
    }
}
