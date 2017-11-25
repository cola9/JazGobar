package com.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import java.util.Date;

/**
 * Created by nikolajcolic on 25/02/17.
 */

public class DataAll {
    public static final String LOKACIJA_ID = "lokacija_id";
    public static SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private User userMe;
    private GobaList gobaList;
    //private Hashtable<String, Goba> gobaList;
    private ArrayList<Lokacija> lokacijaList;
    //private Hashtable<String, Lokacija> lokacijaList;
    private ArrayList<LokacijaGoba> lokacijaGobaList;

    public static String getHtmlFormatedLocationTagList(ArrayList<LokacijaGoba> l) {
        StringBuffer sb= new StringBuffer();
        for (int i=0; i<l.size(); i++) {
            if(l.get(i).getGoba().isChecked()){
                sb.append(l.get(i).getHtmlFromat());
                if (i<(l.size()-1)) sb.append(", ");
            }
        }
        return sb.toString();
    }
    public Lokacija getLocationByID(String ID) {
        //return lokacijaList.get(ID);
        for(Lokacija l: lokacijaList){
            if(l.getId().equals(ID)){//equal namesto ==ID == primerja objekte ne vsebine
                return l;
            }
        }
        return null;
    }
    /*public Goba getGobaByID(String ID) {
        return gobaList.get(ID);
    }*/
    public DataAll() {
        userMe = new User("neznan@ne.ne","neznano","skrito");
        gobaList = new GobaList();
        //lokacijaList = new ArrayList<>();
        /*gobaList=new Hashtable<>();
        gobaList.put(UUID.randomUUID().toString().replaceAll("-", ""),new Goba("gozdni kukmak"));
        gobaList.put(UUID.randomUUID().toString().replaceAll("-", ""),new Goba("zeleni mesnatovec"));
        gobaList.put(UUID.randomUUID().toString().replaceAll("-", ""),new Goba("ovčji mesnatovec"));
        gobaList.put(UUID.randomUUID().toString().replaceAll("-", ""),new Goba("citronasta mušnica"));*/
        lokacijaList = new ArrayList<>();
        lokacijaGobaList = new ArrayList<>();
    }
    public Lokacija addLocation(String name, double x, double y, String im,boolean deli, String ikonaVremena, String vlaznost, String pritisk, String temp, int vlaznostH, int pritiskH, double tempH) {
        Lokacija tmp = new Lokacija(name, x,y,im,System.currentTimeMillis(),true,userMe.getIdUser(), ikonaVremena, vlaznost, pritisk, temp, vlaznostH, pritiskH, tempH);
        //lokacijaList.put(tmp.getId(),tmp);
        tmp.setDeli(deli);
        lokacijaList.add(tmp);
        return tmp;
    }
    public void addLocationTag(Lokacija l, Goba g) {
        lokacijaGobaList.add(new LokacijaGoba(l.getId(), g,System.currentTimeMillis(),userMe.getIdUser()));
    }
    public void addLocationScenarijA(Lokacija l) {
        for(int x=0;x<gobaList.velikost();x++){
            lokacijaGobaList.add(new LokacijaGoba(l.getId(), gobaList.getGoba(x),System.currentTimeMillis(),userMe.getIdUser()));
        }
    }
    @Override
    public String toString() {
        return "DataAll{" +
                "\nuserMe=" + userMe +
                ",\n gobaList=" + gobaList +
                ",\n lokacijaList=" + lokacijaList +
                ",\n lokacijaGobaList=" + lokacijaGobaList +
                '}';
    }
    public static DataAll scenarijA() {
        DataAll da = new DataAll();
        Date danes = new Date();
        da.userMe = new User("nikolaj.colic@student.um.si","NikolajC","incorrect");
        //da.userMe = new User("nikolaj.colic@student.um.si","NikolajC","incorrect");
        Lokacija tmp;
        tmp = da.addLocation("Pri kapeli", 46.412644,15.3816058, "goba2",true, "&#xf01e;", "53%", "1009 hPa", "5.3", 71, 1011, 15.2);
        da.addLocationScenarijA(tmp);
        tmp = da.addLocation("Zelo veliko gob", 46.362644,15.116058, "goba1",false, "&#xf01c;", "43%", "1019 hPa", "9.8", 59, 1009, 10.2);
        da.addLocationScenarijA(tmp);
        tmp = da.addLocation("Malo gob", 46.559644,15.639058, "goba3",false, "&#xf014;", "63%", "1003 hPa", "7.7", 69, 1000, 4.5);
        da.addLocationScenarijA(tmp);
        tmp = da.addLocation("Na prostem", 46.462644,15.216058, "goba4",false, "&#xf013;", "59%", "1012 hPa", "6.7", 55, 1011, 8.8);
        da.addLocationScenarijA(tmp);
        //da.userMe = new User("zdravko.colic@student.um.si","Zile","incorrect");
        tmp = da.addLocation("Globko v gozdu", 46.442644,15.396058, "goba5",false, "&#xf01b;", "49%", "1005 hPa", "8.2", 45, 1009, 9.9);
        da.addLocationScenarijA(tmp);
        tmp = da.addLocation("Blizu gozda", 46.449644,15.396058, "goba6",false, "&#xf019;", "51%", "1014 hPa", "9", 59, 1012, 12.2);
        da.addLocationScenarijA(tmp);
        da.gobaList.getGoba(1).setChecked(true);
        return da;
    }
    /*public List<String> getLocationIDs() {
        return  Collections.list(lokacijaList.keys());
    }*/
    public Lokacija getLocation(int i) {
        //return lokacijaList.get(lokacijaList.keys().nextElement());
        return lokacijaList.get(i);
    }
    public Lokacija getLocation(String ID) {
        //return lokacijaList.get(lokacijaList.keys().nextElement());
        for(int x=0;x<lokacijaList.size();x++){
            if(lokacijaList.get(x).getId().equals(ID)){
                return lokacijaList.get(x);
            }
        }
        return  null;
    }
    public List<Lokacija> getLokacijaAll() {
       // ArrayList<Lokacija> l = new ArrayList<>();
        //l.addAll(lokacijaList.values());
        //return  l;
        return lokacijaList;
    }
    public int getLocationSize() {
        return lokacijaList.size();
    }
    public Lokacija getNewLocation(double d1, double d2, String ikonaVremena, String vlaznost, String pritisk, String temp, int vlaznostH, int pritiskH, double tempH) {
        Lokacija l;
        l = addLocation("N/A", d1, d2, "",false, ikonaVremena, vlaznost, pritisk, temp, vlaznostH, pritiskH, tempH);
        Goba g;
        g = new Goba("N/A");
        gobaList.dodajGobo(g);
        addLocationTag(l,g);
        return l;
    }
    public Goba getLocGoba(Lokacija l) {
        Goba g;
        String idGoba = null;
        for (int x = 0; x < lokacijaGobaList.size(); x++) {
            LokacijaGoba lg;
            lg = lokacijaGobaList.get(x);
            if(lg.getIdLokacija() == l.getId()){
                idGoba=lg.getIdGoba();
                break;
            }
        }
        g = gobaList.getImeGobe(idGoba);
        return g;
    }
    public void addLocation(Lokacija l) {
        lokacijaList.add(l);

    }
    public User getUserMe() {
        return userMe;
    }

    public void getLokacijaUser(String s) {
        ArrayList<Lokacija> tmp = new ArrayList<>();
        for(Lokacija l: lokacijaList){
            if(l.getIdUser().equals(s) || l.getDeli()){//equal namesto ==ID == primerja objekte ne vsebine
                tmp.add(l);
            }
        }
        lokacijaList.clear();
        lokacijaList=tmp;
    }

    public void setLokacijaDeli(int i) {
        lokacijaList.get(i).setDeli(true);
    }
    /*
TODO Speed up this simple implementation!
 */
    public ArrayList<LokacijaGoba> getGobaList(String locationId) {
        ArrayList<LokacijaGoba> gobe = new ArrayList<>();
        for (LokacijaGoba lt:lokacijaGobaList) {
            if (lt.getIdLokacija().equals(locationId)) {
                gobe.add(lt);
            }
        }
        return  gobe;
    }

    public void removeFromTagList(String locationId) {
        for (int i=lokacijaGobaList.size()-1;i>=0; i--) {
            if (lokacijaGobaList.get(i).getIdLokacija().equals(locationId))
                lokacijaGobaList.remove(i);
        }
    }

    public ArrayList<LokacijaGoba> getDefultGobaLists(ArrayList<Goba> tags, Lokacija l) {
        ArrayList<LokacijaGoba> lt = new ArrayList<>();

        for (Goba t:tags){
            lt.add(new LokacijaGoba(l.getId(),t,System.currentTimeMillis(),userMe.getIdUser()));
        }
        return lt;
    }

    public void addNewLocationGoba(LokacijaGoba tag) {
        lokacijaGobaList.add(tag);
    }

    public GobaList getGobaListAll() {
        return gobaList;
    }

    public void getLokacijaGoba(int indexGoba) {
        ArrayList<String> lokacijaId=new ArrayList<>();
        for(LokacijaGoba lg: lokacijaGobaList){
            if(lg.getGoba().isChecked() && lg.getGoba().getIme().equals(gobaList.getGoba(indexGoba).getIme())){
                lokacijaId.add(lg.getIdLokacija());
            }
        }
        ArrayList<Lokacija> tmp = new ArrayList<>();
        for(Lokacija l: lokacijaList){
            for(String id:lokacijaId){
                if(l.getId().equals(id)){//equal namesto ==ID == primerja objekte ne vsebine
                    tmp.add(l);
                    break;
                }
            }
        }
        lokacijaList.clear();
        lokacijaList=tmp;
    }

    public void getLokacijaRazdalja(int razdalja,double lati, double longi) {
        ArrayList<Lokacija> tmp = new ArrayList<>();
        for(Lokacija l: lokacijaList){
            String razlika=getDistanceInString(lati, longi,l.getX(),l.getY());
            if(Integer.parseInt(razlika)<=razdalja){//equal namesto ==ID == primerja objekte ne vsebine
                tmp.add(l);
            }
        }
        lokacijaList.clear();
        lokacijaList=tmp;
    }
    public static final float DEG2RAD = (float) (Math.PI / 180.0);
    public static final int RADIUS_EARTH_METERS = 6378137;
    public static int distance(double lat1, double long1, double lat2, double long2) {
        final double a1 = DEG2RAD * lat1;
        final double a2 = DEG2RAD * long1;
        final double b1 = DEG2RAD * lat2;
        final double b2 = DEG2RAD * long2;
        final double cosa1 = Math.cos(a1);
        final double cosb1 = Math.cos(b1);
        final double t1 = cosa1 * Math.cos(a2) * cosb1 * Math.cos(b2);
        final double t2 = cosa1 * Math.sin(a2) * cosb1 * Math.sin(b2);
        final double t3 = Math.sin(a1) * Math.sin(b1);
        final double tt = Math.acos(t1 + t2 + t3);
        return (int) (RADIUS_EARTH_METERS * tt);
    }

    public static final String getDistanceInString(int meters) {
        //if (meters<1000) return Integer.toString(meters);
        return Integer.toString((int) (meters/1000));
    }

    public static final String getTimeDiff(long from, long t){
        int d = (Math.round((from-t))/1000/3600);
        if (d<48) return d+" h";
        return Math.round(d/24)+" d";
    }

    public static final String getDistanceInString(double lat1, double long1, double lat2, double long2) {
        return getDistanceInString(distance(lat1,long1,lat2,long2));
    }
}
