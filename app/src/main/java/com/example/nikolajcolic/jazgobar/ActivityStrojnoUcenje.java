package com.example.nikolajcolic.jazgobar;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nikolajcolic.jazgobar.eventbus.MessageEventUpdateLocation;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ActivityStrojnoUcenje extends AppCompatActivity {

    TextView tvRezultat;
    Button btnZazeni;
    Spinner sIzbira;
    Double temp;
    Integer vlaznost, pritisk;
    Location mLocation;
    ApplicationMy app;
    String mesto;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventUpdateLocation event) {
        Log.i("ActivityStrojnoUcenje", "MessageEventUpdateLocation ");
        mLocation = event.getM();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strojno_ucenje);

        app = (ApplicationMy)getApplication();
        tvRezultat = (TextView) findViewById(R.id.tvRezultat);
        btnZazeni = (Button) findViewById(R.id.btnZazeni);
        sIzbira = (Spinner) findViewById(R.id.spinner);
        btnZazeni.setClickable(false);
        podatkiOVremenu();
        btnZazeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer index = sIzbira.getSelectedItemPosition() ;
                String[] gobeArray = getResources().getStringArray(R.array.ArrayGobe);
                String[] letniCasNabiranjaArray = getResources().getStringArray(R.array.ArrayLetniCasNabiranja);
                Date datum = new Date();
                int month_day = datum.getMonth() * 100 + datum.getDay();
                String letniCasTrenutno;
                if (month_day <= 315) {
                    letniCasTrenutno="zima";
                }
                else if (month_day <= 615) {
                    letniCasTrenutno="spomlad";
                }
                else if (month_day <= 915) {
                    letniCasTrenutno="poletje";
                }
                else if (month_day <= 1215) {
                    letniCasTrenutno="jesen";
                }
                else {
                    letniCasTrenutno="zima";
                }
                try {
                    InputStream is = getBaseContext().getAssets().open("datoteka.arff");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    Instances podatki = new Instances(reader);
                    reader.close();
                    podatki.setClassIndex(podatki.numAttributes() - 1);
                    int number_of_atributes=7;
                    int number_of_instances=1;
                    FastVector temperatura = new FastVector();
                    temperatura.addElement("'\\'(-inf-10]\\''");
                    temperatura.addElement("'\\'(10-20]\\''");
                    temperatura.addElement("'\\'(20-inf)\\''");
                    Attribute att1 = new Attribute("temperatura", temperatura);
                    FastVector vlaz = new FastVector();
                    vlaz.addElement("'\\'(-inf-74]\\''");
                    vlaz.addElement("'\\'(74-inf)\\''");
                    Attribute att2 = new Attribute("vlaznost",vlaz);
                    FastVector prit = new FastVector();
                    prit.addElement("'\\'(-inf-1010]\\''");
                    prit.addElement("'\\'(1010-1020]\\''");
                    prit.addElement("'\\'(1020-inf)\\''");
                    Attribute att3 = new Attribute("pritisk", prit);
                    FastVector letniCasNabiranja = new FastVector();
                    letniCasNabiranja.addElement("spomlad-jesen");
                    letniCasNabiranja.addElement("spomlad-zima");
                    letniCasNabiranja.addElement("celoLeto");
                    letniCasNabiranja.addElement("jesen");
                    letniCasNabiranja.addElement("poletje-jesen");
                    letniCasNabiranja.addElement("poletje-zima");
                    Attribute att4 = new Attribute("letniCasNabiranja", letniCasNabiranja);
                    FastVector tipGobe = new FastVector();
                    tipGobe.addElement("mušnica");
                    tipGobe.addElement("lisička");
                    tipGobe.addElement("golobica");
                    tipGobe.addElement("ježek");
                    tipGobe.addElement("jurček");
                    tipGobe.addElement("prašnica");
                    tipGobe.addElement("goban");
                    tipGobe.addElement("trhlenka");
                    tipGobe.addElement("žveplenjača");
                    tipGobe.addElement("trdokožnica");
                    tipGobe.addElement("širokolistar");
                    tipGobe.addElement("dežnik");
                    tipGobe.addElement("ledenka");
                    tipGobe.addElement("slivarica");
                    tipGobe.addElement("bledivka");
                    tipGobe.addElement("poprovka");
                    tipGobe.addElement("podvihanec");
                    tipGobe.addElement("podvihanka");
                    tipGobe.addElement("odpadljivka");
                    tipGobe.addElement("mlečnica");
                    tipGobe.addElement("ciganček");
                    tipGobe.addElement("kresilača");
                    tipGobe.addElement("ploskocevka");
                    tipGobe.addElement("polstenec");
                    Attribute att5 = new Attribute("tipGobe", tipGobe);
                    FastVector letniCas = new FastVector();
                    letniCas.addElement("spomlad");
                    letniCas.addElement("poletje");
                    letniCas.addElement("jesen");
                    letniCas.addElement("zima");
                    Attribute att6 = new Attribute("letniCas", letniCas);
                    FastVector Class = new FastVector();
                    Class.addElement("grem");
                    Class.addElement("neGrem");
                    Attribute attClass = new Attribute("Class", Class);

                    FastVector wekaAtt = new FastVector(number_of_atributes);
                    wekaAtt.addElement(att1);
                    wekaAtt.addElement(att2);
                    wekaAtt.addElement(att3);
                    wekaAtt.addElement(att4);
                    wekaAtt.addElement(att5);
                    wekaAtt.addElement(att6);
                    wekaAtt.addElement(attClass);

                    Instances trainingSet = new Instances("Rel", wekaAtt, 1);
                    trainingSet.setClassIndex(number_of_atributes-1);

                    String tempDis, vlaznostDis, pritiskDisk;

                    if(temp<=10){
                        tempDis="'\\'(-inf-10]\\''";
                    }else if(temp>10 && temp <=20){
                        tempDis="'\\'(10-20]\\''";
                    }else{
                        tempDis="'\\'(20-inf)\\''";
                    }

                    if(vlaznost<=74){
                        vlaznostDis="'\\'(-inf-74]\\''";
                    }else{
                        vlaznostDis="'\\'(74-inf)\\''";
                    }

                    if(pritisk<=1010){
                        pritiskDisk="'\\'(-inf-1010]\\''";
                    }else if(pritisk>10 && pritisk <=20){
                        pritiskDisk="'\\'(1010-1020]\\''";
                    }else{
                        pritiskDisk="'\\'(1020-inf)\\''";
                    }

                    Instance example = new DenseInstance(7);
                    example.setValue(att1,tempDis);
                    example.setValue(att2,vlaznostDis);
                    example.setValue(att3,pritiskDisk);
                    example.setValue(att4,letniCasNabiranjaArray[index]);
                    example.setValue(att5,gobeArray[index]);
                    example.setValue(att6,letniCasTrenutno);
                    //example.setValue(attClass,"grem");

                    trainingSet.add(example);



                    J48 tree = new J48();
                    tree.buildClassifier(podatki);
                    Double value = tree.classifyInstance(trainingSet.instance(0));
                    trainingSet.instance(0).setClassValue(value);
                    Double doubleClassValue = trainingSet.instance(0).classValue();
                    Integer classValue = doubleClassValue.intValue();
                    tvRezultat.setText("Rezultat: \n\nPodatki o vremenu: \nMesto: "+mesto+"\nTemperatura: "+temp+"\nVlažnost: "+vlaznost+"\nPritisk: "
                            +pritisk+"\n\nIzbrana goba: "+gobeArray[index]+"\nLetni čas nabiranja: "+
                            letniCasNabiranjaArray[index]+"\nTrenutni letni čas: "+letniCasTrenutno
                            +"\n\nOdločitev: "+trainingSet.classAttribute().value(classValue));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void podatkiOVremenu(){
        WeatherFunction.placeIdTask asyncTask =new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                vlaznost = Integer.parseInt(weather_humidity.replaceAll("\\D+",""));
                pritisk = Integer.parseInt(weather_pressure.replaceAll("\\D+",""));
                Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(weather_temperature);
                m.find();
                temp = Double.parseDouble(m.group(1));
                btnZazeni.setClickable(true);
                mesto=weather_city;
            }
        });

        if (mLocation!=null){
            asyncTask.execute(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
        }else{
            asyncTask.execute(String.valueOf(app.getLastLocation().getLatitude()), String.valueOf(app.getLastLocation().getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
        }
    }
}
