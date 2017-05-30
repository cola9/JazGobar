package com.example.nikolajcolic.jazgobar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.DataAll;
import com.example.Goba;
import com.example.Lokacija;
import com.example.LokacijaGoba;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;
import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ActivityLocationReadOnly extends AppCompatActivity {
    ApplicationMy app;
    ImageView ivSlika;
    TextView edName;
    TextView tvLatLag;
    TextView tvDatum;
    Lokacija l;
    String ID;
    PermissionGranted permissionGranted;
    FlexboxLayout flexBoxLayout;
    ArrayList<TagTextViewReadOnly> gobe;
    boolean stateNew;
    public static String NEW_LOCATION_ID="NEW_LOCATION";
    Location mLocation;
    MapView mMapView;
    ArrayList<OverlayItem> items;
    private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
    @Subscribe
    public void onMessageEvent(MessageEventUpdateLocation event) {
        Log.i("ActivityLocationRO","MessageEventUpdateLocation ");
        mLocation = event.getM();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_read_only);
        app = (ApplicationMy) getApplication();
        gobe = new ArrayList<>();
        ivSlika =(ImageView) findViewById(R.id.imageViewMain);
        tvLatLag = (TextView) findViewById(R.id.textViewLanLat);
        edName = (TextView) findViewById(R.id.editTextName);
        tvDatum = (TextView) findViewById(R.id.textViewDatum);
        flexBoxLayout = (FlexboxLayout) findViewById(R.id.flexBoxLayout);
        stateNew = false;
        permissionGranted = new PermissionGranted(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(false);

        items = new ArrayList<OverlayItem>();
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent i = new Intent(getBaseContext(), ActivityMapa.class);
                i.putExtra(DataAll.LOKACIJA_ID, l.getId());
                startActivity(i);
                return true;
            }
        });
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
                        Intent i = new Intent(getBaseContext(), ActivityMapa.class);
                        i.putExtra(DataAll.LOKACIJA_ID, l.getId());
                        startActivity(i);
                        return false;
                    }
                }, this);
        mMyLocationOverlay.setFocusItemsOnTap(true);

        mMapView.getOverlays().add(mMyLocationOverlay);
        stateNew = false;

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            permissionGranted.checkAllMagicalCameraPermission();
        }else{
            permissionGranted.checkCameraPermission();
            permissionGranted.checkReadExternalPermission();
            permissionGranted.checkWriteExternalPermission();
            permissionGranted.checkLocationPermission();
        }
        ID ="";
    }

    void setLokacija(String ID) {
        l = app.getLocationByID(ID);
        setGobaViewList( app.getAll().getGobaList(ID));
        update(l);
    }
    void setGobaViewList(ArrayList<LokacijaGoba> lt) {
        gobe.clear();
        for (LokacijaGoba t:lt) { //save them all for update
            TagTextViewReadOnly tv = new TagTextViewReadOnly(this, t,true);
            //  flexBoxLayout.addView(tv);
            gobe.add(tv);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        startService(new Intent(app, GPSTracker.class));//start service

        Bundle extras = getIntent().getExtras();
        if( (extras !=null) && (!ID.equals(NEW_LOCATION_ID)))
        {
            stateNew = false;
            setLokacija(extras.getString(DataAll.LOKACIJA_ID));
        } else {
            System.out.println("Nič ni v extras!");
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
    public void onClickMapDirection(View v){
        if(app.getLastLocation()!=null){
            if(app.getLastLocation().getLatitude()!=l.getX() && app.getLastLocation().getLongitude()!=l.getY()){
                String uri = "http://maps.google.com/maps?saddr=" + app.getLastLocation().getLatitude() + "," + app.getLastLocation().getLongitude() + "&daddr=" + l.getX() + "," + l.getY();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }else{
                Toast.makeText(ActivityLocationReadOnly.this, "Ste ze na tej lokaciji!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ActivityLocationReadOnly.this, "Nimate prižgane lokacije!", Toast.LENGTH_SHORT).show();
        }
    }
    public void update(Lokacija l) {
        tvLatLag.setText(l.getX()+" "+l.getY());
        edName.setText(l.getIme());
        tvDatum.setText(Util.dt.format(new Date(l.getDatum())));
        //ivSlika
        flexBoxLayout.removeAllViews();
        for (TagTextViewReadOnly t:gobe) {
            flexBoxLayout.addView(t);
        }
        IMapController mapController = mMapView.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(l.getX(),l.getY());


        mMyLocationOverlay.removeAllItems();
        mMyLocationOverlay.addItem(new OverlayItem(l.getIme(),l.getX()+";"+l.getY(),startPoint));
        mapController.setCenter(startPoint);

        File imgFile = new  File(l.getSlika());
        if (imgFile.exists()) {
            //"http://i.imgur.com/DvpvklR.png"
            System.out.println("Picasso: "+l.getSlika());
            Picasso.with(this.getApplicationContext())
                    .load(imgFile) //URL
                    .placeholder(R.drawable.ic_cloud_download_black_124dp)
                    .error(R.drawable.ic_error_black_124dp)
                    // To fit image into imageView
                    .fit()
                    // To prevent fade animation
                    .noFade()
                    .into(ivSlika);
        }else{
            if(l.getSlika().equals("goba1")){
                ivSlika.setImageResource(R.drawable.goba1);
            }else if(l.getSlika().equals("goba2")){
                ivSlika.setImageResource(R.drawable.goba2);
            }else if(l.getSlika().equals("goba3")){
                ivSlika.setImageResource(R.drawable.goba3);
            }else if(l.getSlika().equals("goba4")){
                ivSlika.setImageResource(R.drawable.goba4);
            }else if(l.getSlika().equals("goba5")){
                ivSlika.setImageResource(R.drawable.goba5);
            }else if(l.getSlika().equals("goba6")){
                ivSlika.setImageResource(R.drawable.goba6);
            }else{
                ivSlika.setImageResource(R.drawable.goba);
            }
        }
    }
}
