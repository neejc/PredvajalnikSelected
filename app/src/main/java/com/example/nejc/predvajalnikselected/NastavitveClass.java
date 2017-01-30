package com.example.nejc.predvajalnikselected;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nejc on 30. 01. 2017.
 */

public class NastavitveClass {
    private boolean Neznani;
    private boolean Mp3;
    private boolean Kratke;
    private boolean Srednje;
    private boolean Dolge;

    public NastavitveClass() {
        Neznani = true;
        Mp3 = true;
        Kratke = true;
        Srednje = true;
        Dolge = true;
    }

    public NastavitveClass(boolean neznani, boolean mp3, boolean kratke, boolean srednje, boolean dolge) {
        Neznani = neznani;
        Mp3 = mp3;
        Kratke = kratke;
        Srednje = srednje;
        Dolge = dolge;
    }

    public boolean isNeznani() {
        return Neznani;
    }

    public void setNeznani(boolean neznani) {
        Neznani = neznani;
    }

    public boolean isMp3() {
        return Mp3;
    }

    public void setMp3(boolean mp3) {
        Mp3 = mp3;
    }

    public boolean isKratke() {
        return Kratke;
    }

    public void setKratke(boolean kratke) {
        Kratke = kratke;
    }

    public boolean isSrednje() {
        return Srednje;
    }

    public void setSrednje(boolean srednje) {
        Srednje = srednje;
    }

    public boolean isDolge() {
        return Dolge;
    }

    public void setDolge(boolean dolge) {
        Dolge = dolge;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("neznani", Neznani);
            jsonObject.put("mp3", Mp3);
            jsonObject.put("kratke", Kratke);
            jsonObject.put("srednje", Srednje);
            jsonObject.put("dolge", Dolge);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
