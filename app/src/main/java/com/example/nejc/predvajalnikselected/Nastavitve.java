package com.example.nejc.predvajalnikselected;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Nejc on 30. 01. 2017.
 */

public class Nastavitve extends AppCompatActivity {

    NastavitveClass nastavitve;
    private CheckBox neznaniCheck;
    private CheckBox mp3Check;
    private CheckBox kratkeCheck;
    private CheckBox srednjeCheck;
    private CheckBox dolgeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nastavitve);

        nastavitve = new NastavitveClass();
        neznaniCheck = (CheckBox)findViewById(R.id.neznaneBox);
        mp3Check = (CheckBox)findViewById(R.id.mp3Box);
        kratkeCheck = (CheckBox)findViewById(R.id.kratkeBox);
        srednjeCheck = (CheckBox)findViewById(R.id.srednjeBox);
        dolgeCheck = (CheckBox)findViewById(R.id.dolgeBox);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        if(!preveriCeObstajaNastavitve())
            zapisiNastavitveVJSON(nastavitve);

        nastavitve = beriIzJSONNastavitve();

        inicializacija();

        neznaniCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(nastavitve.isNeznani()) {
                    nastavitve.setNeznani(false);
                    zapisiNastavitveVJSON(nastavitve);
                }
                else {
                    nastavitve.setNeznani(true);
                    zapisiNastavitveVJSON(nastavitve);
                }
            }
        }
        );

        mp3Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(nastavitve.isMp3()) {
                        nastavitve.setMp3(false);
                        zapisiNastavitveVJSON(nastavitve);
                    }
                    else {
                        nastavitve.setMp3(true);
                        zapisiNastavitveVJSON(nastavitve);
                    }
                }
            }
        );

        kratkeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                    if(nastavitve.isKratke()) {
                                                        nastavitve.setKratke(false);
                                                        zapisiNastavitveVJSON(nastavitve);
                                                    }
                                                    else {
                                                        nastavitve.setKratke(true);
                                                        zapisiNastavitveVJSON(nastavitve);
                                                    }
                                                }
                                            }
        );

        srednjeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                       if(nastavitve.isSrednje()) {
                                                           nastavitve.setSrednje(false);
                                                           zapisiNastavitveVJSON(nastavitve);
                                                       }
                                                       else {
                                                           nastavitve.setSrednje(true);
                                                           zapisiNastavitveVJSON(nastavitve);
                                                       }
                                                   }
                                               }
        );

        dolgeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                       if(nastavitve.isDolge()) {
                                                           nastavitve.setDolge(false);
                                                           zapisiNastavitveVJSON(nastavitve);
                                                       }
                                                       else {
                                                           nastavitve.setDolge(true);
                                                           zapisiNastavitveVJSON(nastavitve);
                                                       }
                                                   }
                                               }
        );
    }

    private NastavitveClass beriIzJSONNastavitve(){

        NastavitveClass n = new NastavitveClass();

        String jsonString = readFromFileNastavitve();
        Log.d("MYAPP", readFromFileNastavitve());

        try {
            JSONObject jsonobject = new JSONObject(jsonString);
            String ne = jsonobject.getString("neznani");
            boolean neznani = Boolean.parseBoolean(ne);
            String m = jsonobject.getString("mp3");
            boolean mp3 = Boolean.parseBoolean(m);
            String k = jsonobject.getString("kratke");
            boolean kratki = Boolean.parseBoolean(k);
            String s = jsonobject.getString("srednje");
            boolean srednji = Boolean.parseBoolean(s);
            String d = jsonobject.getString("dolge");
            boolean dolgi = Boolean.parseBoolean(d);

            n.setNeznani(neznani);
            n.setMp3(mp3);
            n.setKratke(kratki);
            n.setDolge(dolgi);
            n.setSrednje(srednji);


        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }

        return n;
    }

    private String readFromFileNastavitve() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("nastavitve.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void zapisiNastavitveVJSON(NastavitveClass n){
        JSONObject obj;

        obj = n.toJSON();

        String toJSON = obj.toString();

        writeToFileNastavitve(toJSON);

    }

    private void writeToFileNastavitve(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("nastavitve.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private Boolean preveriCeObstajaNastavitve(){
        try {
            InputStream inputStream = openFileInput("nastavitve.txt");

            if ( inputStream != null ) {
                return true;
            }
            else
                return false;
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return false;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            return false;
        }
    }

    private void inicializacija()
    {
        if(nastavitve.isNeznani())
            neznaniCheck.setChecked(true);
        else
            neznaniCheck.setChecked(false);

        if(nastavitve.isMp3())
            mp3Check.setChecked(true);
        else
            mp3Check.setChecked(false);

        if(nastavitve.isKratke())
            kratkeCheck.setChecked(true);
        else
            kratkeCheck.setChecked(false);

        if(nastavitve.isSrednje())
            srednjeCheck.setChecked(true);
        else
            srednjeCheck.setChecked(false);

        if(nastavitve.isDolge())
            dolgeCheck.setChecked(true);
        else
            dolgeCheck.setChecked(false);
    }
}
