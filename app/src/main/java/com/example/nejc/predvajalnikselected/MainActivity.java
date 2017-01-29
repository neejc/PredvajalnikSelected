package com.example.nejc.predvajalnikselected;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.widget.MediaController.MediaPlayerControl;

import java.io.File;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements MediaPlayerControl {

    private ArrayList<Pesem> songList; //dobi vse, ki se trenutno nahajajo na sd kartici
    private ArrayList<Pesem> shranjene; //dobi vse shranjene iz jsona
    private ArrayList<Pesem> dataSeznam;
    private ListView songView;
    private DataAll data;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController controller;

    Konfiguracija konfig;


    private boolean repeat = false;
    private boolean shuffle = false;

    MenuItem shuffleItem;
    MenuItem repeatItem;

    private boolean paused=false, playbackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songView = (ListView) findViewById(R.id.song_list);
        registerForContextMenu(songView);
        songList = new ArrayList<Pesem>();
        shranjene = new ArrayList<Pesem>();
        data = new DataAll();

        dataSeznam = data.vrniSeznamPesmi();
        getSongList();

       // Collections.sort(songList, new Comparator<Pesem>(){

        Collections.sort(songList, new Comparator<Pesem>(){
           public int compare(Pesem a, Pesem b){
                return a.dobiNaslov().compareTo(b.dobiNaslov());
           }
        });

        if(!preveriCeObstajaKonfiguracija())
            zapisiKonfiguracijoVJSON(new Konfiguracija());

        konfig = beriIzJSONKonfiguracija();


        if(!preveriCeObstaja()) //dodano
            zapisiVJSON(songList);

        shranjene = beriIzJSON();

        //data.preveri(songList); preveri ce je kak nov oz. ce je kaksen manj

        //Collections.sort(songList, new Comparator<Pesem>(){

        //Collections.sort(shranjene, new Comparator<Pesem>(){
         //   public int compare(Pesem a, Pesem b){
        //        return a.dobiNaslov().compareTo(b.dobiNaslov());
         //   }
        //});

        SongAdapter songAdt = new SongAdapter(this, shranjene);
        songView.setAdapter(songAdt);

        setController();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            paused=false;
        }

        // Set up receiver for media player onPrepared broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(onPrepareReceiver,
                new IntentFilter("MEDIA_PLAYER_PREPARED"));
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    //play next
    private void playNext(){
        musicSrv.playNext();

        if(playbackPaused){
          //  setController();
            playbackPaused=false;
        }
       // setController();
       // controller.show(0);
    }

    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
           // setController();
            playbackPaused=false;
        }
       // setController();
       // controller.show(0);
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public ArrayList<Pesem> dodajPredvajanje(int idPesmi) {
        ArrayList<Pesem> najdeni = new ArrayList<Pesem>();
        for (int i=0; i<shranjene.size(); i++) {
            if (shranjene.get(i).dobiID() == idPesmi)
                shranjene.get(i).dodajPredvajanje();
        }
        return najdeni;
    }

    public void songPicked(View view){
       // view.setSelected(true);
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.dodajVZgodovino(Integer.parseInt(view.getTag().toString()));
        dodajPredvajanje(Integer.parseInt(view.getTag().toString()));
        zapisiVJSON(shranjene);
    //    View v;
    //    v = songView.findViewById(Integer.parseInt(view.getTag().toString()));
    //    v.getBackground().setColorFilter(Color.parseColor("#0072BB"), PorterDuff.Mode.DARKEN);
     //   v.invalidate();


       // toast(view.getTag().toString());
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        setController();
        controller.show(0);

    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        shuffleItem = menu.findItem(R.id.action_shuffle);
        repeatItem = menu.findItem(R.id.action_repeat);

        procesirajKonfiguracijo();

        return super.onCreateOptionsMenu(menu);
    }

    private void procesirajKonfiguracijo(){
        if(konfig.getSuffle()){
            shuffleItem.setIcon(getResources().getDrawable(R.drawable.shuffleon));
            musicSrv.setShuffle();
            shuffle = true;
            repeat = false;
        }

        else if(konfig.getRepeat()){
            repeatItem.setIcon(getResources().getDrawable(R.drawable.repeaton));
            musicSrv.setRepeat();
            repeat = true;
            shuffle = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicSrv.setShuffle();

                if(repeat) {
                    musicSrv.setRepeat();
                    repeatItem.setIcon(getResources().getDrawable(R.drawable.repeat));
                    repeat = !repeat;
                }

                if(shuffle) {
                    zapisiKonfiguracijoVJSON(new Konfiguracija(false,false));
                    item.setIcon(getResources().getDrawable(R.drawable.shuffle));
                }
                else {
                    zapisiKonfiguracijoVJSON(new Konfiguracija(true,false));
                    item.setIcon(getResources().getDrawable(R.drawable.shuffleon));
                }


                shuffle = !shuffle;
                break;
            case R.id.action_repeat:
             //   stopService(playIntent);
             //   musicSrv=null;
             //   System.exit(0);
                musicSrv.setRepeat();

                if(shuffle) {
                    musicSrv.setShuffle();
                    shuffleItem.setIcon(getResources().getDrawable(R.drawable.shuffle));
                    shuffle = !shuffle;
                }

                if(repeat) {
                    zapisiKonfiguracijoVJSON(new Konfiguracija(false,false));
                    item.setIcon(getResources().getDrawable(R.drawable.repeat));
                }
                else {
                    zapisiKonfiguracijoVJSON(new Konfiguracija(false,true));
                    item.setIcon(getResources().getDrawable(R.drawable.repeaton));
                }


                repeat = !repeat;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    private void setController(){
        //set the controller up
        if (controller == null)
            controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;

        String sortOrder = null;

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");

        String[] selectionArgsMp3 = new String[]{ mimeType };

        Cursor musicCursor = musicResolver.query(musicUri, projection, selectionMimeType, selectionArgsMp3, sortOrder);;

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Pesem(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToFileConfig(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("konfiguracija.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("config.txt");

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

    private String readFromFileKonfiguracija() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("konfiguracija.txt");

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

    private void zapisiKonfiguracijoVJSON(Konfiguracija k){
        JSONObject obj;

        obj = k.toJSON();

        String toJSON = obj.toString();

        writeToFileConfig(toJSON);

    }

    private Boolean preveriCeObstaja(){
        try {
            InputStream inputStream = openFileInput("config.txt");

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

    private Boolean preveriCeObstajaKonfiguracija(){
        try {
            InputStream inputStream = openFileInput("konfiguracija.txt");

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

    private ArrayList<Pesem> beriIzJSON(){
        ArrayList<Pesem> rez = new ArrayList<Pesem>();

        String jsonString = readFromFile();
        Log.d("MYAPP", readFromFile());

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
                String stPredvajanj = jsonobject.getString("stPredvajanj");
                int stP = Integer.parseInt(stPredvajanj);
                rez.add(new Pesem(idParsan, naslov, izvajalec,priljubljena1,stP));
            }
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }

        return rez;
    }

    private Konfiguracija beriIzJSONKonfiguracija(){

        Konfiguracija k = new Konfiguracija();

        String jsonString = readFromFileKonfiguracija();
        Log.d("MYAPP", readFromFile());

        try {
            JSONObject jsonobject = new JSONObject(jsonString);
            String s = jsonobject.getString("shuffle");
            boolean shuffle = Boolean.parseBoolean(s);
            String r = jsonobject.getString("repeat");
            boolean repeat = Boolean.parseBoolean(r);

            k.setShuffle(shuffle);
            k.setRepeat(repeat);


        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }

        return k;
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    private BroadcastReceiver onPrepareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been prepared, show controller
            controller.show(0);
        }
    };

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


}
