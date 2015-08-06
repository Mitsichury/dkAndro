package com.example.mitsichury.mp3player;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Player extends Service {

    private MediaPlayer mp;
    private String absolutePath;
    private ArrayList<String> trackPath; // Store the actual directory in order to play without activity
    private int actualTrack;
    private boolean replay;
    private boolean random;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private Random r;
    private LocalBroadcastManager broadcaster;
    Notification notification;
    int tmpPositionBegin;

    /**
     * Create a new MediaPlayer
     * Charge the previous configuration
     */
    void init() {
        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
        broadcaster = LocalBroadcastManager.getInstance(this); // Allow to contact activity
        trackPath = new ArrayList<>();
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                sendResult("aze");
                if (!replay) {
                    next();
                } else {
                    setTrackChange(actualTrack);
                }
            }
        });

        // The proccessor cannot go to sleep mode
        //mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        prefs = getSharedPreferences("Mp3PlayerPref", 0);
        loadSharedPreferences();
        r = new Random();
        //next();
        setTrackChange(actualTrack);
        mp.seekTo(tmpPositionBegin);
        pause();
        activeNotification();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void activeNotification(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("media player")
                .setSmallIcon(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"))
                .setContentTitle("Media Player")
                .setContentText(trackPath.get(actualTrack).substring(trackPath.get(actualTrack).lastIndexOf("/") + 1, trackPath.get(actualTrack).length()))
                .setContentIntent(pi);
                /*.addAction(Resources.getSystem().getIdentifier("ic_media_play", "drawable", "android"), "Play", PendingIntent.getActivity(getApplicationContext(), 0,
                        intent, 0, null))
                .addAction(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"), "Pause",
                        PendingIntent.getActivity(getApplicationContext(), 0,
                                intent, 0, null));*/

        notification = builder.build();
        startForeground(1, notification);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void updateNotif(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("media player")
                .setSmallIcon(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"))
                .setContentTitle("Media Player")
                .setContentText(trackPath.get(actualTrack).substring(trackPath.get(actualTrack).lastIndexOf("/") + 1, trackPath.get(actualTrack).length()));
        mNotificationManager.notify(
                1,
                builder.build());
    }

    static final public String PLAYER_RESULT = "result";

    static final public String PLAYER_MESSAGE = "msg";

    public void sendResult(String message) {
        Intent intent = new Intent(PLAYER_RESULT);
        if(message != null)
            intent.putExtra(PLAYER_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
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

    /**
     * Method which play if possible the track at the id given in the track list
     * @param id
     *      Position of the track in the list
     */
    void setTrackChange(int id){
        try {
            mp.stop();
            mp.reset();
            actualTrack = id;
            mp.setDataSource(getApplicationContext(), Uri.parse(trackPath.get(id)));
            mp.prepare();
            mp.start();
            updateNotif();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Cannot load the data", Toast.LENGTH_SHORT).show();
        }
    }

    int getActualTrack(){
        return actualTrack;
    }

    boolean isReplay(){return replay;}

    void setReplay(boolean replay){
        this.replay = replay;
    }

    boolean isRandom(){return random;}

    void setRandom(boolean random){
        this.random = random;
    }

    int getDuration(){
        return mp.getDuration();
    }

    int getCurrentPosition(){
        return mp.getCurrentPosition();
    }

    void pause(){
        mp.pause();
    }

    void play(){
        mp.start();
    }

    void stop(){
        mp.stop();
    }

    String getCurrentPath(){
        return absolutePath;
    }

    void saveSharedPreferences(){
        editor = prefs.edit();
        editor.putString("path", absolutePath);
        editor.putInt("trackId", actualTrack);
        editor.putInt("time", mp.getCurrentPosition());
        editor.putBoolean("replay", replay);
        editor.putBoolean("random", random);
        editor.commit();
    }

    void loadSharedPreferences(){
        absolutePath = prefs.getString("path", Environment.getExternalStorageDirectory().getAbsolutePath());
        tmpPositionBegin = prefs.getInt("time", 0);
        parseActualDirectory();
        int tmp = prefs.getInt("trackId", 0);
        if(tmp==0 && new File(trackPath.get(0)).isDirectory()){
            next();
        }else {
            actualTrack = tmp;
        }
        replay = prefs.getBoolean("replay", false);
        random = prefs.getBoolean("random", false);
    }

     void parseActualDirectory() {
         trackPath.clear();

        File file = new File(absolutePath);
        for (int i=0; i<file.listFiles().length;i++){
            File tmp = file.listFiles()[i];
            String nom = tmp.getName();

            if(tmp.isFile()){
                if(nom.contains(".")) {
                    if (nom.substring(nom.lastIndexOf('.'), nom.length()).equals(".mp3") ||
                            nom.substring(nom.lastIndexOf('.'), nom.length()).equals(".m4a") ||
                            nom.substring(nom.lastIndexOf('.'), nom.length()).equals(".flac")) {
                        trackPath.add(String.valueOf(tmp.getAbsolutePath()));
                    }
                }
            }else{
                trackPath.add(String.valueOf(tmp.getAbsolutePath()));
            }
        }
    }

    void seekTo(int time){
        mp.seekTo(time);
    }

    void setTrackPath(ArrayList<String> array, String path){
        trackPath = array;
        absolutePath = path;
    }

    void next(){
        if(random){actualTrack+=r.nextInt(trackPath.size());}
        do{
            actualTrack+=1;
            if (actualTrack >= trackPath.size()) {
                actualTrack = 0;
            }
        }while(new File(trackPath.get(actualTrack)).isDirectory());

        setTrackChange(actualTrack);
    }

    void previous(){
        do{
            actualTrack-=1;
            if (actualTrack < 0) {
                actualTrack = trackPath.size()-1;
            }
        }while(new File(trackPath.get(actualTrack)).isDirectory());

        setTrackChange(actualTrack);
    }

    boolean isPlaying(){
        return mp.isPlaying();
    }

    @Override
    public void onDestroy() {
        saveSharedPreferences();
        mp.stop();
        mp.release();
        super.onDestroy();
    }
}
