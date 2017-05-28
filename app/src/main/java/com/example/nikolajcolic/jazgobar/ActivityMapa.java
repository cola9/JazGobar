package com.example.nikolajcolic.jazgobar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.DataAll;
import com.example.Lokacija;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

public class ActivityMapa extends AppCompatActivity {
    ApplicationMy app;
    MapView mMapView;
    Lokacija l;
    String ID;
    ArrayList<OverlayItem> items;
    private String id_user="nikolaj.colic@student.um.si";
    private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
    Location mLocation;
    @Subscribe
    public void onMessageEvent(MessageEventUpdateLocation event) {
        Log.i("ActivityMapa","MessageEventUpdateLocation ");
        mLocation = event.getM();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_mapa);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mLocation=null;
        items = new ArrayList<OverlayItem>();

        mMyLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        IMapController mapController = mMapView.getController();
                        mapController.setCenter(item.getPoint());
                        mapController.zoomTo(mMapView.getMaxZoomLevel());
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        String lokacijaId = app.getLokacijIDByIme(item.getTitle());
                        Intent i = new Intent(getBaseContext(), ActivityLocationReadOnly.class);
                        i.putExtra(DataAll.LOKACIJA_ID, lokacijaId);
                        startActivity(i);
                        return false;
                    }
                }, this);
        mMyLocationOverlay.setFocusItemsOnTap(true);

        mMapView.getOverlays().add(mMyLocationOverlay);
        app = (ApplicationMy) getApplication();

    }
    void setLokacija(String ID) {
        l = app.getLocationByID(ID);
        IMapController mapController = mMapView.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(l.getX(), l.getY());

        mMyLocationOverlay.removeAllItems();
        mMyLocationOverlay.addItem(new OverlayItem(l.getIme(),l.getX()+";"+l.getY(),startPoint));
        mapController.setCenter(startPoint);

        //update(l);
    }
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        startService(new Intent(app, GPSTracker.class));//start service
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Bundle extras = getIntent().getExtras();
        if( (extras !=null))
        {
            ID = extras.getString(DataAll.LOKACIJA_ID);
            setLokacija(extras.getString(DataAll.LOKACIJA_ID));

        } else {

            items = new ArrayList<OverlayItem>();

            List<Lokacija> lokacijaList = app.getAllUserList(id_user);
            for(int x=0;x<lokacijaList.size();x++){
                items.add(new OverlayItem(lokacijaList.get(x).getIme(), "opis", new GeoPoint(lokacijaList.get(x).getX(),lokacijaList.get(x).getY())));
            }
            GeoPoint startPoint=null;
            if(mLocation!=null){
                items.add(new OverlayItem("Tu si", "opis", new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude())));
                startPoint = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            }else{
                startPoint = new GeoPoint(46.25139,15.2568);
            }
            IMapController mapController = mMapView.getController();
            mapController.setZoom(10);
            mapController.setCenter(startPoint);
            mMyLocationOverlay.removeAllItems();
            mMyLocationOverlay.addItems(items);
            mapController.setCenter(startPoint);
        }

    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        stopService(new Intent(app, GPSTracker.class));//start service
        super.onPause();
    }
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_mapa);
        app = (ApplicationMy) getApplication();

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        IMapController mapController = mMapView.getController();

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your applicatio
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        items = new ArrayList<OverlayItem>();

        List<Lokacija> lokacijaList = app.getAllUserList(id_user);
        for(int x=0;x<lokacijaList.size();x++){
            items.add(new OverlayItem(lokacijaList.get(x).getIme(), "opis", new GeoPoint(lokacijaList.get(x).getX(),lokacijaList.get(x).getY())));
        }

        mapController.setZoom(10);
        GeoPoint startPoint = new GeoPoint(46.25139,15.2568);
        mapController.setCenter(startPoint);

        mMyLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        IMapController mapController = mMapView.getController();
                        mapController.setCenter(item.getPoint());
                        mapController.zoomTo(mMapView.getMaxZoomLevel());
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(ActivityMapa.this
                                ,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got long pressed", Toast.LENGTH_LONG).show();
                        String lokacijaId = app.getLokacijIDByIme(item.getTitle());
                        ActivityMapa.startDView(lokacijaId,ActivityMapa.this);
                        return false;
                    }
                }, this);


        mMyLocationOverlay.setFocusItemsOnTap(true);
        mMapView.getOverlays().add(mMyLocationOverlay);
    }
    private static void startDView(String lokacijaID, Activity ac) {
        //  System.out.println(name+":"+position);
        //Intent i = new Intent(ac.getBaseContext(), ActivityLocation.class);
        Intent i = new Intent(ac.getBaseContext(), ActivityLocationReadOnly.class);
        i.putExtra(DataAll.LOKACIJA_ID,  lokacijaID);
        ac.startActivity(i);

    }
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }*/
}
