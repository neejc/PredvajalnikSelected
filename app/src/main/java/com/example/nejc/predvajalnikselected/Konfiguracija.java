package com.example.nejc.predvajalnikselected;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nejc on 21.6.2016.
 */
public class Konfiguracija {
    private boolean Shuffle;
    private boolean Repeat;

    public Konfiguracija() {
        Shuffle = false;
        Repeat = false;
    }

    public Konfiguracija(boolean s, boolean r) {
        Shuffle = s;
        Repeat = r;
    }

    public void setShuffle(boolean b) {
        Shuffle = b;
    }

    public void setRepeat(boolean b) {
        Repeat = b;
    }

    public boolean getSuffle() {
        return Shuffle;
    }

    public boolean getRepeat() {
        return Repeat;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("shuffle", Shuffle);
            jsonObject.put("repeat", Repeat);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
