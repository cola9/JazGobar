package com.example.nikolajcolic.jazgobar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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

public class ActivityLogin extends AppCompatActivity {
    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        loginButton=(LoginButton)findViewById(R.id.fb_login_bn);
        textView=(TextView) findViewById(R.id.textView);
        callbackManager=CallbackManager.Factory.create();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sp.getString("token",null);
        Long expire = sp.getLong("expires",0);
        if(token != null && expire != 0){
            String user_id = sp.getString("user_id",null);
            Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
            i.putExtra("USER_ID",user_id);
            startActivity(i);
            finish();
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("Login Sucess \n" +
                        loginResult.getAccessToken().getUserId()+"\n"+
                        loginResult.getAccessToken().getToken());
                String email;
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {

                                // Getting FB User Data
                                //Bundle facebookData = getFacebookData(jsonObject);
                                getFacebookData(jsonObject);

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                editor.putString("token",loginResult.getAccessToken().getToken());
                editor.putString("user_id",loginResult.getAccessToken().getUserId());
                editor.putLong("expires",loginResult.getAccessToken().getExpires().getTime());
                editor.commit();
                Intent i = new Intent(getBaseContext(), ActivityZacetna.class);
                i.putExtra("USER_ID",loginResult.getAccessToken().getUserId());
                startActivity(i);
                finish();
            }

            @Override
            public void onCancel() {
                textView.setText("Login Cancelled!");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
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
}
