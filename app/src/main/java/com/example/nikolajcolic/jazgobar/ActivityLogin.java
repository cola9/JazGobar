package com.example.nikolajcolic.jazgobar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ActivityLogin extends AppCompatActivity {
    LoginButton loginButton;
    //TextView textView;
    CallbackManager callbackManager;
    private static final String url_fb_login = "https://jazgobar.000webhostapp.com/LoginFb.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        loginButton=(LoginButton)findViewById(R.id.fb_login_bn);
        //textView=(TextView) findViewById(R.id.textView);
        callbackManager=CallbackManager.Factory.create();
        loginButton.setReadPermissions("email,publish_actions");

        final EditText etUserName = (EditText) findViewById(R.id.etUserNameR);
        final EditText etPassword = (EditText) findViewById(R.id.etPasswordR);
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final TextView tvRegister = (TextView) findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUserName.getText().toString();
                final String password = etPassword.getText().toString();

                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                //int sifra = jsonResponse.getInt("sifra");
                                String user = jsonResponse.getString("username");
                                String mail = jsonResponse.getString("email");
                                String score = jsonResponse.getString("score");
                                Boolean fileExist = jsonResponse.getBoolean("fileExist");

                                //app.getAll().getUserMe().setIdUser(etUsername.getText().toString());

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor =  sharedPreferences.edit();
                                editor.putString("user_id",user);
                                editor.putString("et_full_name",user);
                                editor.putString("et_email_address",mail);
                                editor.putString("et_score",score);
                                editor.putBoolean("basicLogin",true);
                                editor.putBoolean("fileExist",fileExist);
                                editor.commit();
                                Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
                                i.putExtra("USER_ID", user);
                                i.putExtra("basicLogin",true);
                                startActivity(i);
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                                builder.setMessage("Napaka pri prijavi.")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RequestLogin loginRequest = new RequestLogin(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ActivityLogin.this);
                queue.add(loginRequest);
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sp.getString("token",null);
        Long expire = sp.getLong("expires",0);
        Boolean basic = sp.getBoolean("basicLogin",false);
        if(token != null && expire != 0){
            String user_id = sp.getString("user_id",null);
            Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
            i.putExtra("USER_ID",user_id);
            i.putExtra("basicLogin",false);
            startActivity(i);
            finish();
        }else if(basic){
            String user_id = sp.getString("user_id",null);
            Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
            i.putExtra("USER_ID",user_id);
            i.putExtra("basicLogin",true);
            startActivity(i);
            finish();
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //textView.setText("Login Sucess \n" +
                  //      loginResult.getAccessToken().getUserId()+"\n"+
                    //    loginResult.getAccessToken().getToken());
                //String email;
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {

                                // Getting FB User Data
                                //Bundle facebookData = getFacebookData(jsonObject);
                                getFacebookData(jsonObject);

                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                String username = sp.getString("et_full_name",null);
                                String email = sp.getString("et_email_address",null);
                                //final String user_id = sp.getString("idFacebook",null);

                                // Response received from the server
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");

                                            if (success) {
                                            } else {
                                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityLogin.this);
                                                builder.setMessage("Napaka pri prijavi.")
                                                        .setNegativeButton("Retry", null)
                                                        .create()
                                                        .show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                RequestFbLogin loginFbRequest = new RequestFbLogin(username, email, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ActivityLogin.this);
                                queue.add(loginFbRequest);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                //request.executeAsync();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        request.executeAsync();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                editor.putString("token",loginResult.getAccessToken().getToken());
                editor.putString("user_id",loginResult.getAccessToken().getUserId());
                editor.putLong("expires",loginResult.getAccessToken().getExpires().getTime());
                editor.commit();

                setUser(url_fb_login);

            }

            @Override
            public void onCancel() {
                //textView.setText("Login Cancelled!");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
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
    private void setUser(String url){
        class SetUser extends AsyncTask<String,Void,String> {
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
                    String user=username;
                    while (user==null){
                        user = sp.getString("et_full_name", null);
                    }
                    final String email = sp.getString("et_email_address", "");
                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("username", user);
                    postDataParams.put("email", email);
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
                loading = ProgressDialog.show(ActivityLogin.this,"Dobivam podatke...","Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //imageView.setImageBitmap(bitmap);
                String[] split = s.split("-");
                if(split[0].equals("\"1")){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String user_id = sp.getString("user_id", null);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    String score = split[1].replaceAll("\\D+","");
                    editor.putString("et_score",score);
                    Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
                    i.putExtra("USER_ID",user_id);
                    startActivity(i);
                    finish();
                }
            }
        }
        SetUser su = new SetUser();
        su.execute(url);
    }
    private void getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor =  sharedPreferences.edit();
            String name=null, email=null;
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                name=object.getString("first_name");
            if (object.has("last_name"))
                name=name+" "+object.getString("last_name");
            if (object.has("email"))
                email = object.getString("email");
            //if (object.has("gender"))
                //bundle.putString("gender", object.getString("gender"));

            editor.putString("et_full_name","");
            editor.putString("et_email_address","");
            editor.putString("et_full_name",name);
            editor.putString("et_email_address",email);
            editor.commit();

        } catch (Exception e) {
            Log.d("tag", "BUNDLE Exception : "+e.toString());
        }

        //return bundle;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
    public void register(View v){
        Intent i = new Intent(getBaseContext(), ActivityRegister.class);
        startActivity(i);
    }
}
