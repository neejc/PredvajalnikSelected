package com.example.nejc.predvajalnikselected;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.VideoView;

import java.util.Timer;

public class Splash extends Activity {

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        video = (VideoView)findViewById(R.id.videoView);

        String uriPath = "android.resource://com.example.nejc.predvajalnikselected/" + R.raw.intro;
        Uri uri = Uri.parse(uriPath);
        video.setVideoURI(uri);
        video.requestFocus();
        video.setVisibility(View.VISIBLE);
        video.start();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(6000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
