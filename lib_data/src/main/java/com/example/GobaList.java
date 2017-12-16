package com.example;

import java.util.ArrayList;

/**
 * Created by nikolajcolic on 25/02/17.
 */

public class GobaList {
    private ArrayList<Goba> list;

    public GobaList() {
        list=new ArrayList<>();
        list.add(new Goba("mušnica"));
        list.add(new Goba("lisička"));
        list.add(new Goba("golobica"));
        list.add(new Goba("rumeni ježek"));
        list.add(new Goba("jurček"));
        list.add(new Goba("betičasta prašnica"));
        list.add(new Goba("goban"));
        list.add(new Goba("trhlenka"));
        list.add(new Goba("žveplenjača"));
        list.add(new Goba("trdokožnica"));
        list.add(new Goba("zaviti širokolistar"));
        list.add(new Goba("orjaški dežnik"));
        list.add(new Goba("ledenka"));
        list.add(new Goba("slivarica"));
        list.add(new Goba("vijoličasta bledivka"));
        list.add(new Goba("poprovka"));
        list.add(new Goba("brezov ded"));
        list.add(new Goba("žametni podvihanec"));
        list.add(new Goba("podvihanka"));
        list.add(new Goba("brezova odpadljivka"));
        list.add(new Goba("mlečnica"));
        list.add(new Goba("ciganček"));
        list.add(new Goba("smrekova kresilača"));
        list.add(new Goba("pisana ploskocevka"));
        list.add(new Goba("polstenec"));
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
