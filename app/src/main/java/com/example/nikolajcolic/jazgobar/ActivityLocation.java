package com.example.nikolajcolic.jazgobar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.DataAll;
import com.example.Goba;
import com.example.Lokacija;

import com.example.LokacijaGoba;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.path;

public class ActivityLocation extends AppCompatActivity {
    ApplicationMy app;
    ImageView ivSlika;
    EditText edName;
    TextView tvLatLag;
    TextView tvDatum;
    Button save;
    Lokacija l;
    Goba g;
    String ID;
    PermissionGranted permissionGranted;
    MagicalCamera magicalCamera;
    FlexboxLayout flexBoxLayout;
    ArrayList<TagTextView> gobe;
    boolean stateNew;
    public static String NEW_LOCATION_ID="NEW_LOCATION";
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 20;
    Location mLocation;
    MapView mMapView;
    DisplayMetrics dm;
    ArrayList<OverlayItem> items;
    private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
    CircleImageView circleImageView;
    @Subscribe
    public void onMessageEvent(MessageEventUpdateLocation event) {
        Log.i("ActivityLocation","MessageEventUpdateLocation ");
        mLocation = event.getM();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        app = (ApplicationMy) getApplication();
        gobe = new ArrayList<>();
        ivSlika =(ImageView) findViewById(R.id.imageViewMain);
        tvLatLag = (TextView) findViewById(R.id.textViewLanLat);
        edName = (EditText) findViewById(R.id.editTextName);
        tvDatum = (TextView) findViewById(R.id.textViewDatum);
        flexBoxLayout = (FlexboxLayout) findViewById(R.id.flexBoxLayout);
        circleImageView = (CircleImageView) findViewById(R.id.circleImageView);
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
        app.all=app.getAll();
    }

    void setLokacija(String ID) {
        l = app.getLocationByID(ID);
        //g = app.getGoba(l);
        setGobaViewList( app.getAll().getGobaList(ID));
        //LokacijaTag
        update(l);
    }
    void setGobaViewList(ArrayList<LokacijaGoba> lt) {
        gobe.clear();
        for (LokacijaGoba t:lt) { //save them all for update
            TagTextView tv = new TagTextView(this, t,true);
            //  flexBoxLayout.addView(tv);
            gobe.add(tv);
        }
    }
    /*void setGoba(String id){
        g = app.getTestLocGoba();
        update(g);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //CALL THIS METHOD EVER
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        //this is for rotate picture in this method
        //magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);

        //with this form you obtain the bitmap (in this example set this bitmap in image view)
        ivSlika.setImageBitmap(magicalCamera.getPhoto());

        //if you need save your bitmap in device use this method and return the path if you need this
        //You need to send, the bitmap picture, the photo name, the directory name, the picture type, and autoincrement photo name if           //you need this send true, else you have the posibility or realize your standard name for your pictures.
        String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),"myPhotoName", MagicalCamera.JPEG, true);

        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
        File imageFile = new File(path);
        Uri imageFileUri = Uri.fromFile(imageFile);
        if (RESULT_OK == resultCode) {

            // Decode it for real
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;

            //imageFilePath image path which you pass with intent
            Bitmap bmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);

            // Display it
            ivSlika.setImageBitmap(bmp);
        }*/
        if(path != null){
            //l = new Lokacija("Poimenuj ", magicalCamera.getPrivateInformation().getLatitude(), magicalCamera.getPrivateInformation().getLongitude(),path,System.currentTimeMillis(),true,app.getAll().getUserMe().getIdUser());
            if (mLocation!=null)
                l = new Lokacija("Poimenuj ", mLocation.getLatitude(), mLocation.getLongitude(),path,System.currentTimeMillis(),true,app.getAll().getUserMe().getIdUser());
            else
                l = new Lokacija("Poimenuj ", app.getLastLocation().getLatitude(),  app.getLastLocation().getLongitude(),path,System.currentTimeMillis(),true,app.getAll().getUserMe().getIdUser());

            setGobaViewList(app.getAll().getDefultGobaLists(app.getDefultTags(),l));
            update(l);
            Toast.makeText(this, "The photo is save in device, please check this path: " + path, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
        }
    }
    public void addNewLocation() {
        if (magicalCamera ==null) magicalCamera =  new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE,permissionGranted);
        magicalCamera.takePhoto();

    }
    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        startService(new Intent(app, GPSTracker.class));//start service

        Bundle extras = getIntent().getExtras();
        if( (extras !=null) && (!ID.equals(NEW_LOCATION_ID)))
        {
            ID = extras.getString(DataAll.LOKACIJA_ID);
            if (ID.equals(NEW_LOCATION_ID)) {
                stateNew = true;
                addNewLocation();
            }else {
                stateNew = false;
                setLokacija(extras.getString(DataAll.LOKACIJA_ID));
                circleImageView.setVisibility(View.VISIBLE);
            }
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
    public void save(){
        System.out.println("Prej:"+l);
        l.setIme(edName.getText().toString());
        System.out.println("Po:"+l);
        if(!stateNew){
            //app.all=app.getAll();
            app.all.getLocation(ID).setIme(edName.getText().toString());
        }
        app.save();
    }
    public void onSave(View v){
        if(!edName.getText().toString().equals("Poimenuj ") && stIzbranihGob(gobe)!=0){
            if(stateNew) {
                app.all = app.getAll();
            }
            for (TagTextView tv:gobe) {
                tv.updateObjectState(); //sets LogationTag
                if (stateNew) {
                    app.getAll2().addNewLocationGoba(tv.getTag());
                }
            }
            if (stateNew) {
                app.getAll2().addLocation(l);
                System.out.println("l:"+l);
            }
            save();
            finish();
        }else{
            Toast.makeText(ActivityLocation.this, "Nisi vpisal imena oz. izbral gob!", Toast.LENGTH_SHORT).show();
        }
    }
    public int stIzbranihGob(ArrayList<TagTextView> gobe){
        int st=0;
        for (TagTextView tv:gobe) {
            if(tv.isGuiCheckState()){
                st++;
            }
        }
        return st;
    }
    public void onClickMapDirection(View v){
        if(app.getLastLocation()!=null){
            if(app.getLastLocation().getLatitude()!=l.getX() && app.getLastLocation().getLongitude()!=l.getY()){
                String uri = "http://maps.google.com/maps?saddr=" + app.getLastLocation().getLatitude() + "," + app.getLastLocation().getLongitude() + "&daddr=" + l.getX() + "," + l.getY();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }else{
                Toast.makeText(ActivityLocation.this, "Ste ze na tej lokaciji!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ActivityLocation.this, "Nimate prižgane lokacije!", Toast.LENGTH_SHORT).show();
        }
    }

    public void update(Lokacija l) {
        tvLatLag.setText(l.getX()+" "+l.getY());
        edName.setText(l.getIme());
        tvDatum.setText(Util.dt.format(new Date(l.getDatum())));
        //ivSlika
        flexBoxLayout.removeAllViews();
        for (TagTextView t:gobe) {
            flexBoxLayout.addView(t);
        }
        IMapController mapController = mMapView.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(l.getX(),l.getY());


        mMyLocationOverlay.removeAllItems();
        OverlayItem olItem = new OverlayItem(l.getIme(),l.getX()+";"+l.getY(),startPoint);
        Drawable newMarker = this.getResources().getDrawable(R.drawable.marker_gobe_64);
        olItem.setMarker(newMarker);
        //items.add(olItem);
        mMyLocationOverlay.addItem(olItem);
        mapController.setCenter(startPoint);
        /*File imgFile = new  File(l.getSlika());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ivSlika.setImageBitmap(myBitmap);
        }*/
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

            //   Picasso.with(ac).load(trenutni.getFileName()).into(holder.iv);
            // holder.iv.setImageDrawable(this.ac.getDrawable(R.drawable.ic_airline_seat_recline_extra_black_24dp));
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
