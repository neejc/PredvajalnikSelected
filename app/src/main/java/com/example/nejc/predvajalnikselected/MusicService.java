package com.example.nejc.predvajalnikselected;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by Nejc on 7.6.2016.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Pesem> songs;
    //current position
    private int songPosn;

    private boolean shuffle=false;
    private boolean repeat=false;

    private ArrayList<Integer> zgodovina = new ArrayList<Integer>();
    int zgodovinaCurrent = 0;

    private ArrayList<Integer> shuffleZgodovina = new ArrayList<Integer>();

    private Random rand;


    private final IBinder musicBind = new MusicBinder();

    private String songTitle="";;
    private String songArtist="";;
    private static final int NOTIFY_ID=1;

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();

        initMusicPlayer();

        rand=new Random();

    }

    public void setRepeat(){
        if (repeat) {
            repeat = false;
          //  toast("Nacin ponavljanja deaktiviran.");
        } else if (!repeat) {
            repeat = true;
          //  toast("Nacin ponavljanja aktiviran.");
        }

    }

    public void setShuffle() {
        if (shuffle) {
            shuffle = false;
          //  toast("Shuffle nacin deaktiviran.");
        } else if (!shuffle) {
            shuffle = true;
          //  toast("Shuffle nacin aktiviran.");
        }

    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void setList(ArrayList<Pesem> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
    }

    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            this.resumePlayer();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            this.pausePlayer();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            this.playPrev();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            this.playNext();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        //super.onUnbind(intent);
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_PLAY)) {
                    this.go();
                }else if(action.equals(ACTION_PAUSE)) {
                    this.pausePlayer();
                }else if(action.equals(ACTION_NEXT)) {
                    this.playNext();
                }else if(action.equals(ACTION_PREVIOUS)) {
                    this.playPrev();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        Intent intent = null;
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.playing)
                .setTicker(songTitle)
                .setWhen(0)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(songArtist);

        intent = new Intent(ACTION_PREVIOUS);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_previous, "", pendInt);

        intent = new Intent(ACTION_PAUSE);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_pause, "", pendInt);

        intent = new Intent(ACTION_NEXT);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_next, "", pendInt);

        builder.setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);

        // Broadcast intent to activity to let it know the media player has been prepared
        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
    }

    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MusicService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction(ACTION_PLAY);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(songTitle)
                .setContentText(songArtist)
                .setDeleteIntent(pendingIntent)
                .setStyle(style);
        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    /*
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(YES_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed YES");
        } else if(MAYBE_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed NO");
        } else if(NO_ACTION.equals(action)) {
            Log.v("shuffTest","Pressed MAYBE");
        }
    }
    */

    public void dodajVZgodovino(int id){
        zgodovina.add(id);
    }

    public void playSong(){
        //play a song
        player.reset();
        //get song
        Pesem playSong = songs.get(songPosn);
        songTitle=playSong.dobiNaslov();
        songArtist=playSong.dobiIzvajalca();
        //get id
        long currSong = playSong.dobiID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void resumePlayer(){
        player.release();
    }

    public void pausePlayer(){
        player.pause();
        Intent intent = null;
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.paused)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(songArtist)
                .setWhen(0)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(songTitle);

        intent = new Intent(ACTION_PREVIOUS);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_previous, "", pendInt);

        intent = new Intent(ACTION_PLAY);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_play, "", pendInt);

        intent = new Intent(ACTION_NEXT);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_next, "", pendInt);

        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);

    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
        Intent intent = null;
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.playing)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(songArtist)
                .setWhen(0)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(songTitle);

        intent = new Intent(ACTION_PREVIOUS);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_previous, "", pendInt);

        intent = new Intent(ACTION_PAUSE);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_pause, "", pendInt);

        intent = new Intent(ACTION_NEXT);
        pendInt = PendingIntent.getService(getApplicationContext(),
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_media_next, "", pendInt);

        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void playPrev(){

        if(((zgodovina.size() - 2) - zgodovinaCurrent) < 0 && !repeat) {
           // toast("premala");
            songPosn--;
            if(songPosn<0) songPosn=songs.size()-1;
        }
        else  {
            if(!repeat) {
                zgodovinaCurrent++;
                songPosn = zgodovina.get((zgodovina.size() - 1) - zgodovinaCurrent);
            }
           // toast("Zgodovin nazaj: " + zgodovinaCurrent);
        }

        playSong();
    }

    //skip to next
    public void playNext(){
        boolean zeObstaja = true;
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn || zeObstaja){

                zeObstaja = false;

                newSong=rand.nextInt(songs.size());

                if(shuffleZgodovina.size() == songs.size())
                    shuffleZgodovina.clear();

                if(shuffleZgodovina.size() > 0)
                {
                   // toast("ShuffleZgodovina size: " + shuffleZgodovina.size());
                    for(int i = 0; i < shuffleZgodovina.size(); i++)
                    {
                        if(newSong == shuffleZgodovina.get(i).shortValue())
                            zeObstaja = true;
                    }
                }

            }

            shuffleZgodovina.add(newSong);
            songPosn=newSong;
        }
        else if(repeat){
            //song ostane enak
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }

        if(zgodovinaCurrent > 0 && !repeat) {
            while(songPosn != zgodovina.get((zgodovina.size() - 1) - zgodovinaCurrent))
            {
                zgodovinaCurrent--;
                songPosn = zgodovina.get((zgodovina.size() - 1) - zgodovinaCurrent);
            }

           // toast("Zgodovin nazaj: " + zgodovinaCurrent);
        }
        else{
            if(songPosn != zgodovina.get(zgodovina.size() - 1))
                zgodovina.add(songPosn);
        }


        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

}
