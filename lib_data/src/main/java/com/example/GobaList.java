package com.example;

import java.util.ArrayList;

/**
 * Created by nikolajcolic on 25/02/17.
 */

public class GobaList {
    private ArrayList<Goba> list;

    public GobaList() {
        list=new ArrayList<>();
        list.add(new Goba("gozdni kukmak"));
        list.add(new Goba("zeleni mesnatovec"));
        list.add(new Goba("ovčji mesnatovec"));
        list.add(new Goba("citronasta mušnica"));
    }

    @Override
    public String toString() {
        return "GobaList{" +
                "list=" + list +
                '}';
    }
    public void dodajGobo(Goba g){
        list.add(g);
    }
    public Goba getImeGobe(String idGoba){
        Goba g = null;
        for(int x=0;x< list.size();x++){
            if(idGoba == list.get(x).getId()){
               g= list.get(x);
                break;
            }
        }
        return g;
    }
    public  ArrayList<Goba> getClone() {
        ArrayList<Goba> l = new ArrayList<>();
        for (Goba t:list) {
            l.add(new Goba(t));
        }
        return l;
    }
    public Goba getPrvi(){
        return list.get(0);
    }
    public Goba getGoba(int index){
        return list.get(index);
    }

    public int velikost() {
        return list.size();
    }
}
