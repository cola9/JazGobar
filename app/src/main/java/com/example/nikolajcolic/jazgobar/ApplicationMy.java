package com.example.nikolajcolic.jazgobar;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.DataAll;
import com.example.Goba;
import com.example.GobaList;
import com.example.Lokacija;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ApplicationMy extends Application {
    public static SharedPreferences preferences;
    DataAll all;
    private static final String DATA_MAP = "jazgobardatamap";
    private static final String FILE_NAME = "jazgobar.json";
    private Location mLastLocation;
    private GobaList gobe;
    private static final int SORT_BY_DATE=0;
    private static final int SORT_BY_DISTACE=1;
    int sortType = SORT_BY_DATE;
    public ArrayList<Goba> getDefultTags() {
        return gobe.getClone();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        gobe = new GobaList(); //also sets default tags
        EventBus.getDefault().register(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLastLocation=null;
        if (!load()){
            all = DataAll.scenarijA();
            save();
        }
    }
    @Subscribe
    public void onMessageEvent(MessageEventUpdateLocation event) {
        Log.i("ApplicationMy","MessageEventUpdateLocation ");
        mLastLocation = event.getM();
    }
    @Override
    public void onTerminate() {
        EventBus.getDefault().unregister(this);
        super.onTerminate();

    }
    public Location getLastLocation() {
        return mLastLocation;
    }

    public void setLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }
    public boolean hasLocation() {
        if (mLastLocation==null) return false;
        return true;
    }
    /*public Goba getTestLocGoba() {
        return all.getLocGoba(getTestLocation());
    }*/
    public Lokacija getTestLocation() {
        return all.getLocation(0);
    }
    public Lokacija getLocationByID(String id) {
        return all.getLocationByID(id);
    }
    public Goba getGoba(Lokacija l) {
        return all.getLocGoba(l);
    }
    /*public Goba getGobaByID(String id) {

        return all.getGobaByID(id);
    }
    public List<String> getListLocationID() {

        return all.getLocationIDs();
    }*/
    public List<Lokacija> getLokacijaAll() {

        return all.getLokacijaAll();
    }
    public GobaList getGobaList(){
        return all.getGobaListAll();
    }
    public Lokacija getNewLocation(double x, double y) {

        return all.getNewLocation(x,y);
    }

    public DataAll getAll() {
        //return all;
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""
                + FILE_NAME);
        DataAll tmp = ApplicationJson.load(file);
        if (tmp!=null) {
            all = tmp;
        }
        return all;
    }
    public DataAll getAll2() {
        return all;
    }
    public boolean save() {
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""
                + FILE_NAME);

        return ApplicationJson.save(all,file);
    }
    public boolean load(){
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""
                + FILE_NAME);
        DataAll tmp = ApplicationJson.load(file);
        if (tmp!=null) {
            all = tmp;
        }
        else {
            return false;
        }
        return true;
    }

    public DataAll getAllUser(String s, int indexGoba, int razdalja, double lati, double longi) {
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""
                + FILE_NAME);
        DataAll tmp = ApplicationJson.load(file);
        if (tmp!=null) {
            all = tmp;
        }
        all.getLokacijaUser(s);
        if(indexGoba!=0){
            all.getLokacijaGoba(indexGoba-1);
        }
        if(razdalja!=11 && lati!=-1&&longi!=-1){
            all.getLokacijaRazdalja(razdalja,lati,longi);
        }
        return all;
    }
    public List<Lokacija> getAllUserList(String s) {
        all.getLokacijaUser(s);
        return all.getLokacijaAll();
    }

    public void removeLocationByPosition(int adapterPosition) {
        all.getLokacijaAll().remove(adapterPosition);
    }

    public String getLokacijIDByIme(String title) {
        List<Lokacija> list= all.getLokacijaAll();
        String id="";
        for(int x=0;x<list.size();x++){
            if(list.get(x).getIme().equals(title)){
                id= list.get(x).getId();
                break;
            }
        }
        return id;
    }
    public void sortUpdate() {
        //sortType= (sortType+1) / 2;
        switch (sortType) {
            case SORT_BY_DATE:{
                Collections.sort(all.getLokacijaAll(), new Comparator<Lokacija>() {
                    @Override
                    public int compare(Lokacija l1, Lokacija l2) {
                        if (l1.getDatum()==l2.getDatum()) return 0;
                        if (l1.getDatum()>l2.getDatum()) return -1;
                        return 1;
                    }
                });
            }
            break;
            case SORT_BY_DISTACE:{
                if (mLastLocation==null) return;
                Collections.sort(all.getLokacijaAll(), new Comparator<Lokacija>() {
                    @Override
                    public int compare(Lokacija l1, Lokacija l2) {
                        int d1 = Util.distance(mLastLocation.getLatitude(),mLastLocation.getLongitude(),l1.getX(),l1.getY());
                        int d2 = Util.distance(mLastLocation.getLatitude(),mLastLocation.getLongitude(),l2.getX(),l2.getY());
                        if (d1==d2) return 0;
                        if (d1>d2) return 1;
                        return -1;
                    }
                });

            }
            break;
        }

    }
    public void sortChangeAndUpdate() {
        sortType= (sortType+1) % 2;
        sortUpdate();
    }


}
