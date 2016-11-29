package com.example.nejc.predvajalnikselected;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by Nejc on 2.6.2016.
 */
public class DataAll {
    private ArrayList<Pesem> pesmi;
    private ArrayList<Pesem> priljubljene;

    public DataAll() {
        pesmi = new ArrayList<Pesem>();
        priljubljene = new ArrayList<Pesem>();
    }

    public void dodaj(Pesem p){
        pesmi.add(p);
    }

    public ArrayList<Pesem> vrniSeznamPesmi(){ return pesmi; }

    public int dobiSteviloPesmi(){
        return pesmi.size();
    }

    public void beri(){

    }


    @Override
    public String toString() {
        return "DataAll{" +
                "pesmi=" + pesmi +
                '}';
    }

    public ArrayList<Pesem> isciPriljublene() {
        ArrayList<Pesem> najdeni = new ArrayList<Pesem>();
        for (int i=0; i<pesmi.size(); i++) {
            if (pesmi.get(i).dobiPriljubljena())
                najdeni.add(pesmi.get(i));
        }
        return najdeni;
    }

}
