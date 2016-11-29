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


    public Pesem(long songID, String naslov, String izvajalec) {
        this.id = songID;
        this.naslov = naslov;
        this.izvajalec = izvajalec;
        this.priljubljena = false;
        this.stPredvajanj = 0;
    }

    public Pesem(long songID, String naslov, String izvajalec, boolean priljubljena, int stPredvajanj) {
        this.id = songID;
        this.naslov = naslov;
        this.izvajalec = izvajalec;
        this.priljubljena = priljubljena;
        this.stPredvajanj = stPredvajanj;
    }

    public long dobiID(){return id;}
    public String dobiNaslov(){return naslov;}
    public String dobiIzvajalca(){return izvajalec;}
    public boolean dobiPriljubljena(){return priljubljena;}


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

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
