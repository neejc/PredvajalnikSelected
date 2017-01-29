package com.example.nejc.predvajalnikselected;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nejc on 21.6.2016.
 */
public class Konfiguracija {
    private boolean Shuffle;
    private boolean Repeat;
    private boolean Favorite;

    public Konfiguracija() {
        Shuffle = false;
        Repeat = false;
        Favorite = false;
    }

    public Konfiguracija(boolean s, boolean r) {
        Shuffle = s;
        Repeat = r;
        Favorite = false;
    }

    public Konfiguracija(boolean s, boolean r, boolean f) {
        Shuffle = s;
        Repeat = r;
        Favorite = f;
    }

    public void setShuffle(boolean b) {
        Shuffle = b;
    }

    public void setRepeat(boolean b) {
        Repeat = b;
    }

    public void setFavorite(boolean b) {
        Favorite = b;
    }

    public boolean getSuffle() {
        return Shuffle;
    }

    public boolean getRepeat() {
        return Repeat;
    }

    public boolean getFavorite() {
        return Favorite;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("shuffle", Shuffle);
            jsonObject.put("repeat", Repeat);
            jsonObject.put("favorite", Favorite);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
