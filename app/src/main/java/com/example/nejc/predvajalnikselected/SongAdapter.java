package com.example.nejc.predvajalnikselected;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nejc on 2.6.2016.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Pesem> songs;
    private LayoutInflater songInf;
    private ArrayList<Pesem> shranjene;

    public SongAdapter(Context c, ArrayList<Pesem> theSongs){
        songs=theSongs;
        shranjene = new ArrayList<Pesem>();
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.song, parent, false);
        //get title and artist views
        final TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        CheckBox favorite = (CheckBox)songLay.findViewById(R.id.favbox);
        //get song using position
        final Pesem currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.dobiNaslov());
        artistView.setText(currSong.dobiIzvajalca());

        if(currSong.dobiPriljubljena())
            favorite.setChecked(true);

        else
            favorite.setChecked(false);

        favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton v,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                CheckBox checkBox=(CheckBox)v;
                if (isChecked) {
                    dodajPriljubljeno((int)currSong.dobiID());
                }
                else
                    odstraniPriljubljeno((int)currSong.dobiID());
            }
        });

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

    public void dodajPriljubljeno(int idPesmi) {

        ArrayList<Pesem> nova;
        nova = beriIzJSON();
        for (int i=0; i < nova.size(); i++) {
            if (nova.get(i).dobiID() == idPesmi)
                nova.set(i, new Pesem(nova.get(i).dobiID(), nova.get(i).dobiNaslov(), nova.get(i).dobiIzvajalca(),true));
        }
        zapisiVJSON(nova);
    }

    public void odstraniPriljubljeno(int idPesmi) {

        ArrayList<Pesem> nova;
        nova = beriIzJSON();
        for (int i=0; i < nova.size(); i++) {
            if (nova.get(i).dobiID() == idPesmi)
                nova.set(i, new Pesem(nova.get(i).dobiID(), nova.get(i).dobiNaslov(), nova.get(i).dobiIzvajalca(),false));
        }
        zapisiVJSON(nova);
    }

    private ArrayList<Pesem> beriIzJSON(){
        ArrayList<Pesem> rez = new ArrayList<Pesem>();

        String jsonString = readFromFile();
        Log.d("MYAPP", readFromFile());
        Random r = new Random();
        int i1;

        try {
            JSONArray jsonarray = new JSONArray(jsonString);

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String id = jsonobject.getString("id");
                long idParsan = Long.parseLong(id);
                String naslov = jsonobject.getString("naslov");
                String izvajalec = jsonobject.getString("izvajalec");
                String priljubljena = jsonobject.getString("priljubljena");
                boolean priljubljena1 = Boolean.parseBoolean(priljubljena);
                /*
                i1 = r.nextInt(10 - 1) + 1;
                if(i1 > 2)
                    priljubljena1 = false;
                else
                    priljubljena1 = true;
                */

                String stPredvajanj = jsonobject.getString("stPredvajanj");
                int stP = Integer.parseInt(stPredvajanj);
                rez.add(new Pesem(idParsan, naslov, izvajalec,priljubljena1,stP));
            }
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }

        return rez;
    }

    private void zapisiVJSON(ArrayList<Pesem> pesmi){
        JSONArray jarray = new JSONArray();
        JSONObject curr;

        for(int i = 0; i < pesmi.size(); i++)
        {
            curr = pesmi.get(i).toJSON();
            jarray.put(curr);
        }

        // toJSON = songList.get(0).toJSON();
        String toJSON = jarray.toString();

        writeToFile(toJSON);
    }

    private String readFromFile() {

        String ret = "";

        try(BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            ret = sb.toString();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        return ret;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("config.txt",false));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
