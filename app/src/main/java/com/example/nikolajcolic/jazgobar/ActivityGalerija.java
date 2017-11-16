package com.example.nikolajcolic.jazgobar;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ActivityGalerija extends AppCompatActivity implements View.OnClickListener {
    private String imagesJSON;

    private static final String JSON_ARRAY ="result";
    private static final String IMAGE_URL = "url";

    private JSONArray arrayImages= null;

    private int TRACK = 0;

    private static final String IMAGES_URL = "https://jazgobar.000webhostapp.com/AndroidUploadImage/getAllImagesGalerija.php";

    //private Button buttonFetchImages;
    private Button buttonNaprej;
    private Button buttonNazaj;
    private ImageView imageView;
    private TextView tvSeznamGob;
    private TextView tvNapis;
    private ImageView ivLike;
    private ImageView ivDislike;
    private String ocena="";
    private String slika_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galerija);
        imageView = (ImageView) findViewById(R.id.imageView);
        ivDislike = (ImageView) findViewById(R.id.ivX);
        ivLike = (ImageView) findViewById(R.id.ivCheck);
        buttonNaprej = (Button) findViewById(R.id.btnNaprej);
        buttonNazaj = (Button) findViewById(R.id.btnNazaj);
        tvSeznamGob = (TextView) findViewById(R.id.tvSeznamGob);
        tvNapis = (TextView) findViewById(R.id.tvNapis);
        buttonNazaj.setOnClickListener(this);
        buttonNaprej.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        ivDislike.setOnClickListener(this);
        getAllImages();
    }
    @Override
    public void onClick(View v) {
        if(v == buttonNaprej){
            moveNext();
        }
        if(v== buttonNazaj){
            movePrevious();
        }
    }
    private void extractJSON(){
        try {
            JSONObject jsonObject = new JSONObject(imagesJSON);
            arrayImages = jsonObject.getJSONArray(JSON_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showImage(){
        try {
            JSONObject jsonObject = arrayImages.getJSONObject(TRACK);
            getImage(jsonObject.getString(IMAGE_URL));
            tvSeznamGob.setText(jsonObject.getString("seznamGob"));
            ocena = jsonObject.getString("ocena");
            slika_id = jsonObject.getString("id");
            if(ocena.equals("1")){
                ocenaCheck();
            }else{
                ocenaX();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void ocenaCheck(){
        ivDislike.setVisibility(View.GONE);
        ivLike.setVisibility(View.VISIBLE);
    }
    private void ocenaX(){
        ivDislike.setVisibility(View.VISIBLE);
        ivLike.setVisibility(View.GONE);
    }
    private void moveNext(){
        if(TRACK < arrayImages.length()){
            TRACK++;
            showImage();
        }
    }

    private void movePrevious(){
        if(TRACK>0){
            TRACK--;
            showImage();
        }
    }
    private void getAllImages() {
        class GetAllImages extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ActivityGalerija.this, "Fetching Data...","Please Wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                imagesJSON = s;
                if(imagesJSON.equals("{\"result\":[]}")){
                    imageView.setVisibility(View.GONE);
                    ivDislike.setVisibility(View.GONE);
                    ivLike.setVisibility(View.GONE);
                    buttonNaprej.setVisibility(View.GONE);
                    buttonNazaj.setVisibility(View.GONE);
                    tvSeznamGob.setVisibility(View.GONE);
                    tvNapis.setVisibility(View.VISIBLE);
                }else{
                    tvNapis.setVisibility(View.GONE);
                    extractJSON();
                    showImage();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final String username = sp.getString("et_full_name",null);
                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("username", username);
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
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }
        }
        GetAllImages gai = new GetAllImages();
        gai.execute(IMAGES_URL);
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
    private void getImage(String urlToImage){
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;
            @Override
            protected Bitmap doInBackground(String... params) {
                URL url = null;
                Bitmap image = null;

                String urlToImage = params[0];
                try {
                    url = new URL(urlToImage);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ActivityGalerija.this,"Downloading Image...","Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                loading.dismiss();
                imageView.setImageBitmap(bitmap);
            }
        }
        GetImage gi = new GetImage();
        gi.execute(urlToImage);
    }
}
