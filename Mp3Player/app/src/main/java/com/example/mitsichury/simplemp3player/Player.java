package com.example.mitsichury.simplemp3player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class Player extends Service {

    MediaPlayer mp;

    public Player() {
        mp = new MediaPlayer();
    }

    /**
     * Inner class which allow us to get a binder connect to the app
     */
    public class MyBinder extends Binder{
        public Player getPlayer(){return Player.this;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    void setTrackChange(){
        // TODO
    }

    void getInformationAboutTrack(){
        // TODO
    }

    void getCurrentPosition(){
        // TODO
    }

    void setPause(){
        //TODO
    }

    void setPlay(){
        // TODO
    }

    void saveSharedPreferences(){
        // TODO
    }

    void getSharedPreferences(){
        // TODO
    }

    void seekTo(){
        // TODO
    }
}
