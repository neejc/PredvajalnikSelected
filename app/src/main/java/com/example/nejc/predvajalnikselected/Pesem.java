package com.example.nejc.predvajalnikselected;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by Nejc on 2.6.2016.
 */
public class Pesem {
    private long id;
    private String naslov;
    private String izvajalec;
    private boolean priljubljena;
    private int stPredvajanj;
    private int dolzina; //1 - kratka, 2 - srednja, 3 - dolga


    public Pesem(long songID, String naslov, String izvajalec) {
        this.id = songID;
        this.naslov = naslov;
        this.izvajalec = izvajalec;
        this.priljubljena = false;
        this.stPredvajanj = 0;
        this.dolzina = 0;
    }

    public Pesem(long songID, String naslov, String izvajalec, boolean priljubljena) {
        this.id = songID;
        this.naslov = naslov;
        this.izvajalec = izvajalec;
        this.priljubljena = priljubljena;
        this.stPredvajanj = 0;
        this.dolzina = 0;
    }

    public Pesem(long songID, String naslov, String izvajalec, boolean priljubljena, int stPredvajanj) {
        this.id = songID;
        this.naslov = naslov;
        this.izvajalec = izvajalec;
        this.priljubljena = priljubljena;
        this.stPredvajanj = stPredvajanj;
        this.dolzina = 0;
    }

    public Pesem(long songID, String naslov, String izvajalec, boolean priljubljena, int stPredvajanj, int dolzina) {
        this.id = songID;
        this.naslov = naslov;
        this.izvajalec = izvajalec;
        this.priljubljena = priljubljena;
        this.stPredvajanj = stPredvajanj;
        this.dolzina = dolzina;
    }

    public long dobiID(){return id;}
    public String dobiNaslov(){return naslov;}
    public String dobiIzvajalca(){return izvajalec;}
    public boolean dobiPriljubljena(){return priljubljena;}
    public void nastaviPriljubljena(){priljubljena = true; }
    public void odstraniPriljubljena(){priljubljena = false; }
    public int dobiDolzino(){return dolzina;}
    public void nastaviDolzino(int d){dolzina = d;}

    public void dodajPredvajanje(){stPredvajanj++;}
    @Override
    public String toString() {
        return "Pesem{" +
                "id='" + id + '\'' +
                ", naslov='" + naslov + '\'' +
                ", izvajalec='" + izvajalec + '\'' +
                ", priljubljena='" + priljubljena + '\'' +
                ", stPredvajanj='" + stPredvajanj + '\'' +
                '}';
    }

    public JSONObject toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("naslov", naslov);
            jsonObject.put("izvajalec", izvajalec);
            jsonObject.put("priljubljena", priljubljena);
            jsonObject.put("stPredvajanj", stPredvajanj);
            jsonObject.put("dolzina", dolzina);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
