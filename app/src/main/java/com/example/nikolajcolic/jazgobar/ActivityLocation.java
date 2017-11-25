package com.example.nikolajcolic.jazgobar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityLocation extends AppCompatActivity {
    //weather
    public static final String UPLOAD_URL = "https://jazgobar.000webhostapp.com/AndroidUploadImage/uploadImage.php";
    public static final String UPLOAD_KEY = "image";
    TextView  vlaznost_field, pritisk_field, weatherIcon;
    String ikonaVreme, vlaznost, pritisk, temp;
    int vlaznostH, pritiskH;
    double tempH;
    Typeface weatherFont;
    PowerManager.WakeLock wakeLock;
    private static final String TAG = ActivityLocation.class.getSimpleName();
    private String SERVER_URL = "https://jazgobar.000webhostapp.com/UploadToServer.php";
    private static final String getVremeUrl = "https://jazgobar.000webhostapp.com/getVreme.php";
    private String selectedFilePath;
    ProgressDialog dialog;
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
    ShareDialog shareDialog;;
    CallbackManager callbackManager;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    /*ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Moja goba")
                    .setImageUrl(Uri.parse("www.google.si"))
                    .setContentDescription(
                            "test jazgobar")
                    .setContentUrl(Uri.parse("www.velenje.com"))
                    .build();;*/
                    //shareDialog.show(linkContent);  // Show facebook ShareDialog
                    File imgFile = new  File(l.getSlika());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    BitmapDrawable drawable = (BitmapDrawable) ivSlika.getDrawable();
                    //Bitmap bitmap = drawable.getBitmap();
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(myBitmap)
                            .setCaption("StudyTutorial")
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    shareDialog.show(content);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
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
        //share
        //FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);  // intialize facebook shareDialog.

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

        ivSlika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File imgFile = new  File(l.getSlika());
                Bitmap bitmap=null;
                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }else{
                    BitmapDrawable drawable = (BitmapDrawable) ivSlika.getDrawable();
                    bitmap = drawable.getBitmap();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();

                Intent intent = new Intent(getBaseContext(), ActivitySlika.class);
                intent.putExtra("picture", b);
                startActivity(intent);
            }
        });
        //weather
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        vlaznost_field = (TextView)findViewById(R.id.weather_vlaznostTemp);
        pritisk_field = (TextView)findViewById(R.id.weather_pritisk);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


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
        Bundle extras = getIntent().getExtras();
        if( (extras !=null) && (!ID.equals(NEW_LOCATION_ID))) {
            //callbackManager.onActivityResult(requestCode, resultCode, data);

        }else{
            //CALL THIS METHOD EVER
            magicalCamera.resultPhoto(requestCode, resultCode, data);

            //this is for rotate picture in this method
            //magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);

            //with this form you obtain the bitmap (in this example set this bitmap in image view)
            ivSlika.setImageBitmap(magicalCamera.getPhoto());

            //if you need save your bitmap in device use this method and return the path if you need this
            //You need to send, the bitmap picture, the photo name, the directory name, the picture type, and autoincrement photo name if           //you need this send true, else you have the posibility or realize your standard name for your pictures.
            String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(), "myPhotoName", MagicalCamera.JPEG, true);

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
            if (path != null) {
                //l = new Lokacija("Poimenuj ", magicalCamera.getPrivateInformation().getLatitude(), magicalCamera.getPrivateInformation().getLongitude(),path,System.currentTimeMillis(),true,app.getAll().getUserMe().getIdUser());

                if (mLocation != null) {
                    //asyncTask.execute(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
                    l = new Lokacija("Poimenuj ", mLocation.getLatitude(), mLocation.getLongitude(), path, System.currentTimeMillis(), true, app.getAll().getUserMe().getIdUser(), ikonaVreme, vlaznost, pritisk, temp, vlaznostH, pritiskH, tempH);

                } else {
                    //asyncTask.execute(String.valueOf(app.getLastLocation().getLatitude()), String.valueOf(app.getLastLocation().getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
                    l = new Lokacija("Poimenuj ", app.getLastLocation().getLatitude(), app.getLastLocation().getLongitude(), path, System.currentTimeMillis(), true, app.getAll().getUserMe().getIdUser(), ikonaVreme, vlaznost, pritisk, temp, vlaznostH, pritiskH, tempH);
                }
                setGobaViewList(app.getAll().getDefultGobaLists(app.getDefultTags(), l));
                update(l);
                Toast.makeText(this, "The photo is save in device, please check this path: " + path, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void addNewLocation() {
        if (magicalCamera ==null) magicalCamera =  new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE,permissionGranted);
        magicalCamera.takePhoto();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //to sm moral zakomentirati da dela share
        EventBus.getDefault().register(this);
        startService(new Intent(app, GPSTracker.class));//start service

        Bundle extras = getIntent().getExtras();
        if( (extras !=null) && (!ID.equals(NEW_LOCATION_ID)))
        {
            ID = extras.getString(DataAll.LOKACIJA_ID);
            if (ID.equals(NEW_LOCATION_ID)) {
                WeatherFunction.placeIdTask asyncTask =new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
                    public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                        vlaznost_field.setText("Vlažnost: "+weather_humidity+" temp: "+weather_temperature);
                        pritisk_field.setText("Pritisk: "+weather_pressure);
                        //temp_field.setText("Temp: "+weather_temperature);
                        weatherIcon.setText(Html.fromHtml(weather_iconText));
                        ikonaVreme = weather_iconText;
                        vlaznost = weather_humidity;
                        pritisk = weather_pressure;
                        temp = weather_temperature;
                    }
                });

                if (mLocation!=null){
                    asyncTask.execute(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
                }else{
                    asyncTask.execute(String.valueOf(app.getLastLocation().getLatitude()), String.valueOf(app.getLastLocation().getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
                }
                getVreme(getVremeUrl);
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
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    private void getVreme(String url){
        class GetVreme extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            @Override
            protected String doInBackground(String... params) {

                BufferedReader bufferedReader = null;
                String uri = params[0];
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final String username = sp.getString("et_full_name", null);
                    JSONObject postDataParams = new JSONObject();
                    if (mLocation!=null){
                        postDataParams.put("lat", String.valueOf(mLocation.getLatitude()));
                        postDataParams.put("lon", String.valueOf(mLocation.getLongitude()));
                    }else{
                        postDataParams.put("lat", String.valueOf(app.getLastLocation().getLatitude()));
                        postDataParams.put("lon", String.valueOf(app.getLastLocation().getLongitude()));
                    }
                    con.setReadTimeout(15000 /* milliseconds */);
                    con.setConnectTimeout(15000 /* milliseconds */);
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);

                    OutputStream os = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    //return sb.toString().trim();
                    return sb.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ActivityLocation.this,"Dobivam podatke...","Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                String[] parts = s.split(",");
                vlaznostH = Integer.parseInt(parts[0].replaceAll("\\D+",""));
                tempH = Double.valueOf(parts[1].replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
                pritiskH = Integer.parseInt(parts[2].replaceAll("\\D+",""));


            }
        }
        GetVreme gv = new GetVreme();
        gv.execute(url);
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(dialog != null){
            dialog.dismiss();
        }
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
        //save on server
        String DATA_MAP = "jazgobardatamap";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sp.getString("user_id",null);
        String FILE_NAME="jazgobar_"+user+".json";
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""+ FILE_NAME);
        //selectedFilePath = "/storage/sdcard/Android/data/com.example.nikolajcolic.jazgobar/files/jazgobardatamap/jazgobar.json";
        selectedFilePath = file.getAbsolutePath();
        dialog = ProgressDialog.show(ActivityLocation.this, "", "Uploading File...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //creating new thread to handle Http Operations
                    uploadFile(selectedFilePath);
                } catch (OutOfMemoryError e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityLocation.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.dismiss();
                }

            }
        }).start();

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
                uploadimage();
            }
            save();
            finish();
        }else{
            Toast.makeText(ActivityLocation.this, "Nisi vpisal imena oz. izbral gob!", Toast.LENGTH_SHORT).show();
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void uploadimage(){
        //getting name for the image
        //String name = editText.getText().toString().trim();
        String path = l.getSlika();

        class UploadImage extends AsyncTask<Bitmap,Void,String>{

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ActivityLocation.this, "Uploading Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String username = sp.getString("et_full_name",null);
                final String seznamGob = sp.getString("seznamGob",null);

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("username", username);
                data.put("seznamGob", seznamGob);

                String result = rh.sendPostRequest(UPLOAD_URL,data);

                return result;
            }
        }

        Bitmap bitmap2=null;
        File f= new File(l.getSlika());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            bitmap2 = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String seznamGob="";
        for (TagTextView tv:gobe) {
              tv.updateObjectState(); //sets LogationTag
              if(tv.isGuiCheckState()){
                seznamGob+=tv.getGoba().getGoba().getIme()+",";
              }
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("seznamGob",seznamGob);
        editor.commit();
        UploadImage ui = new UploadImage();
        ui.execute(bitmap2);
        /*//Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("username", username) //Adding text parameter to the request
                    .addParameter("seznamGob",seznamGob)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
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
        //weather
        vlaznost_field.setText("Vlažnost:" + l.getVlaznost()+" temp: "+l.getTemp());
        pritisk_field.setText("Pritisk:" + l.getPritisk());
        //temp_field.setText("Temp:" + l.getTemp());
        weatherIcon.setText(Html.fromHtml(l.getIkonaVremena()));
    }
    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {

                    try {

                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(ActivityLocation.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                try{
                    serverResponseCode = connection.getResponseCode();
                }catch (OutOfMemoryError e){
                    Toast.makeText(ActivityLocation.this, "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                }
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/" + fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

                /*if (wakeLock.isHeld()) {

                    wakeLock.release();
                }*/


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityLocation.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityLocation.this, "URL Error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityLocation.this, "Cannot Read/Write File", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }
}
