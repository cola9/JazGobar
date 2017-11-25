package com.example.nikolajcolic.jazgobar;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.URLUtil;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.DataAll;
import com.example.Goba;
import com.example.GobaList;
import com.example.Lokacija;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;

import org.apache.commons.validator.routines.UrlValidator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ApplicationMy extends Application {
    public static SharedPreferences preferences;
    DataAll all;
    private static final String DATA_MAP = "jazgobardatamap";
    //private static String FILE_NAME = "jazgobar.json";
    private static String FILE_NAME = "";
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
        setFileName();
        if (!load()){
            setFileName();
            /*all = DataAll.scenarijA();
            save();*/
            /*final String dwnload_file_path = "https://jazgobar.000webhostapp.com/uploads/"+FILE_NAME;
            boolean exist  = exists(dwnload_file_path);
            if(exist) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadFile(dwnload_file_path);
                    }
                });

                t.start(); // spawn thread
                try {
                    t.join();  // wait for thread to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{*/
                all = DataAll.scenarijA();
                save();
            //}
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
    public Lokacija getNewLocation(double x, double y, String ikonaVremena, String vlaznost, String pritisk, String temp, int vlaznostH, int pritiskH, double tempH) {

        return all.getNewLocation(x,y, ikonaVremena, vlaznost, pritisk, temp, vlaznostH, pritiskH, tempH);
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
        /*setFileName();
        new Thread(new Runnable() {
            public void run() {
                downloadFile();
            }
        }).start();*/
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
    void downloadFile(String dwnload_file_path){

        try {
            int totalSize = 0;
            int downloadedSize = 0;
            //String dwnload_file_path = "https://jazgobar.000webhostapp.com/uploads/jazgobar_niko.json";

                URL url = new URL(dwnload_file_path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //connect
                urlConnection.connect();

                //set the path where we want to save the file
                //File SDCardRoot = Environthisment.getExternalStorageDirectory();
                //create a new file, to save the downloaded file
                //File file = new File(SDCardRoot,"downloaded_file.png");

                File file = new File(this.getExternalFilesDir(DATA_MAP), ""+ FILE_NAME);
                FileOutputStream fileOutput = new FileOutputStream(file);

                //Stream used for reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file which we are downloading
                totalSize = urlConnection.getContentLength();


                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                }
                //close the output stream when complete //
                fileOutput.close();


        } catch (final MalformedURLException e) {
            //showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            //showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            //showError("Error : Please check your internet connection " + e);
        }
    }

    public DataAll getAllUser(String s, int indexGoba, int razdalja, double lati, double longi) {
        setFileName();
        /*Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFile();
            }});

        t.start(); // spawn thread
        try {
            t.join();  // wait for thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        if(!load()){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Boolean fileExist = sp.getBoolean("fileExist",false);
            final String dwnload_file_path = "https://jazgobar.000webhostapp.com/uploads/"+FILE_NAME;
            if(fileExist) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadFile(dwnload_file_path);
                    }
                });

                t.start(); // spawn thread
                try {
                    t.join();  // wait for thread to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                all = DataAll.scenarijA();
                save();
            }
        }
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
    public static boolean exists(String URLName){
        try {
            //HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            int asd = con.getResponseCode();
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            //return true;
            /*String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
            UrlValidator urlValidator = new UrlValidator(schemes);
            if (urlValidator.isValid(URLName)) {
                return true;
            } else {
                return false;
            }
            if(!URLUtil.isValidUrl(URLName)){
               return true;
            }else{
                return false;
            }
            try
            {
                URL url = new URL(URLName);
                url.toURI();
                return true;
            } catch (Exception exception)
            {
                return false;
            }*/
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
    public void Odjava(){
        all=null;
        gobe = new GobaList(); //also sets default tags
        mLastLocation=null;
        FILE_NAME="";
    }
    private void setFileName(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sp.getString("user_id",null);
        FILE_NAME="jazgobar_"+user+".json";
    }
}
