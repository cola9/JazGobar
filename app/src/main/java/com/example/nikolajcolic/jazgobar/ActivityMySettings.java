package com.example.nikolajcolic.jazgobar;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewDebug;

import com.example.Goba;
import com.example.GobaList;
import com.example.Lokacija;
import com.example.nikolajcolic.jazgobar.eventbus.MessageEventSettingsLocationUpdateInterval;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ActivityMySettings extends AppCompatPreferenceActivity {
    private static final String TAG = ActivityMySettings.class.getSimpleName();
    public static final String LOCATION_INTERVAL_KEY="lp_location_interval";
    static ApplicationMy app;

    private static final String url = "https://jazgobar.000webhostapp.com/getScore.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences); depricated
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            addPreferencesFromResource(R.xml.preferences); //TODO if this test and implement other logic like fragment has
//        }else {
           // PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

            getScore(url);
//        }


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
    private void getScore(String url){
        class GetScore extends AsyncTask<String,Void,String> {
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
                loading = ProgressDialog.show(ActivityMySettings.this,"Dobivam podatke...","Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //imageView.setImageBitmap(bitmap);
                String score = s.replaceAll("\\D+","");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                editor.putString("et_score",score);
                editor.commit();
                getFragmentManager().beginTransaction().replace(android.R.id.content,  new PrefsFragment()).commit();

                app = (ApplicationMy)getApplication();//dodano
            }
        }
        GetScore gi = new GetScore();
        gi.execute(url);
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        private static final String TAG = PrefsFragment.class.getSimpleName();
        // ActivityMySettings activityMySetting;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(),R.xml.preferences, false);
            addPreferencesFromResource(R.xml.preferences);
            updateALLSummary(getPreferenceManager().getSharedPreferences());
            final ListPreference listPreference = (ListPreference) findPreference("lp_moje_gobe");

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceData(listPreference);
            listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    setListPreferenceData(listPreference);
                    return false;
                }
            });
        }
        protected static void setListPreferenceData(ListPreference lp) {
            List<String> listItems = new ArrayList<String>();
            List<String> listItemsValues = new ArrayList<String>();
            GobaList list=app.getGobaList();
            listItems.add("Vse gobe");
            listItemsValues.add("0");
            for(int x=0;x<list.velikost();x++){
                listItems.add(list.getGoba(x).getIme());
                listItemsValues.add(Integer.toString(x+1));
            }
            CharSequence[] entries = listItems.toArray(new CharSequence[listItems.size()]);
            //CharSequence[] entries = new CharSequence[]{};
            CharSequence[] entryValues = listItemsValues.toArray(new CharSequence[listItemsValues.size()]);
            //CharSequence[] entryValues = {"1" , "2"};
            lp.setEntries(entries);
            //lp.setDefaultValue("1");
            lp.setEntryValues(entryValues);
        }
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        //For nicer summary info
        private void updateALLSummary(SharedPreferences sharedPreferences) {
           Set<String> keys= sharedPreferences.getAll().keySet();
            Preference connectionPref;
            for (String key:keys) {
                connectionPref= findPreference(key);
                setSummary(sharedPreferences,connectionPref,key);
            }
        }

        //Helper for updating settings summary
        private void setSummary(SharedPreferences sharedPreferences, Preference connectionPref, String key) {
            if (connectionPref==null) return;
            Log.i(TAG, "sharedPreferences key:"+" "+key);
            if (connectionPref instanceof EditTextPreference) {
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
            } else {
                if (connectionPref instanceof CheckBoxPreference) {
                    if (sharedPreferences.getBoolean(key, true))
                        connectionPref.setSummary("True");
                    else
                        connectionPref.setSummary("False");
                }
            }
        }

        //Settings has changed! What to do?
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference connectionPref = findPreference(key);
            if (connectionPref == null) {
                Log.e(TAG, "connectionPref is null");
                return;
            }
            setSummary(sharedPreferences,connectionPref,key);
            if (key.equals(LOCATION_INTERVAL_KEY)) {
                EventBus.getDefault().post(new MessageEventSettingsLocationUpdateInterval(Integer.parseInt(sharedPreferences.getString(key,"1001"))));

               // connectionPref.shouldCommit();
               // getActivity().stopService(new Intent(getActivity(), GPSTracker.class));
            }

        }


    }
}
