package com.example.mitsichury.simplemp3player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity {

    // variable de classe
    TextView tv_title;
    TextView tv_position;
    TextView tv_duration;
    SeekBar seekbar;
    ImageView imgView;
    ImageButton imgBt_backward;
    ImageButton imgBt_forward;
    ImageButton imgBt_pause;
    ImageButton imgBt_random;
    ImageButton imgBt_replay;
    ArrayList<String> listTrackPath;
    ArrayList<String> listTrack;
    int idTrack;
    Handler handler;
    Runnable time;
    MediaPlayer mp;
    ListView lv_musicTrackList;
    File file;
    String actualPath;
    TextView tv_actualPath;
    ArrayAdapter<String> mAdapter;
    Boolean randomTrack;
    Boolean replay;
    Random r;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    InputStream is;
    Bitmap bm;

    String pathImageThumb;
    String title;

    float xTouchDown;
    float xTouchUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listTrack = new ArrayList<String>();
        listTrackPath = new ArrayList<String>();
        randomTrack = false;
        replay = false;
        r = new Random();

        // Link to views in layout
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_position = (TextView)findViewById(R.id.tv_position);
        tv_duration = (TextView)findViewById(R.id.tv_duration);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        imgView = (ImageView)findViewById(R.id.imageView);
        imgBt_backward = (ImageButton)findViewById(R.id.imgBt_backward);
        imgBt_forward = (ImageButton)findViewById(R.id.imgBt_forward);
        imgBt_pause = (ImageButton)findViewById(R.id.imgBt_pause);
        imgBt_random = (ImageButton)findViewById(R.id.imgBt_random);
        imgBt_replay = (ImageButton)findViewById(R.id.imgBt_replay);
        lv_musicTrackList = (ListView)findViewById(R.id.lv_listMusicTrack);

        View header = (View)getLayoutInflater().inflate(R.layout.header,null);

        header.findViewById(R.id.bt_revert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File tmp = file.getParentFile();
                if(tmp != null){
                    actualPath = tmp.getAbsolutePath();
                }
                printDirectory();
            }
        });

        tv_actualPath=(TextView)header.findViewById(R.id.tv_actualPath);

        lv_musicTrackList.addHeaderView(header);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listTrack);
        lv_musicTrackList.setAdapter(mAdapter);

        lv_musicTrackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position -1;
                if (new File(listTrackPath.get(position)).isDirectory()) {
                    actualPath = listTrackPath.get(position);
                    printDirectory();
                } else {
                    idTrack = position;
                    playMusic();
                }
            }
        });

        // Explorer
        actualPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        printDirectory();

        idTrack = 0;

        // Make Player
        mp = null;//new MediaPlayer();

        // Listeners
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){mp.seekTo(seekBar.getProgress());tv_position.setText(m2s(progress));}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(time);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
                handler.postDelayed(time, 100);
            }
        });


        imgBt_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        });

        imgBt_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        imgBt_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        imgBt_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(randomTrack){
                    randomTrack=false;
                    imgBt_random.setBackgroundColor(0xFFD6D7D7);
                }
                else{
                    randomTrack=true;
                    imgBt_random.setBackgroundColor(0xFF000000);
                }
            }
        });

        imgBt_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replay){
                    replay=false;
                    imgBt_replay.setBackgroundColor(0xFFD6D7D7);
                }
                else {
                    replay=true;
                    imgBt_replay.setBackgroundColor(0xFF000000);
                }
            }
        });

        imgView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    xTouchDown =event.getX();
                    Log.i("EVENT", "DOWN");
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    xTouchUp = event.getX();
                    Log.i("EVENT", "UP");

                    float delta = xTouchUp-xTouchDown;

                    if(delta<0 && Math.abs(delta)> 20){
                        next();
                        Log.i("EVENT", "NEXT");
                    }else if(delta>0 && Math.abs(delta)> 20){
                        back();
                    }else {
                        if(mp.isPlaying()){pause();}
                        else {play();}
                    }
                }
                return true;
            }
        });


        // the time runnable
        handler = new Handler();
        time = new Runnable(){
            @Override
            public void run() {
                tv_position.setText(m2s(mp.getCurrentPosition()));
                seekbar.setProgress(mp.getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        };

        playMusic();
    }

    void printDirectory(){
        listTrack.clear();
        listTrackPath.clear();

        file = new File(actualPath);
        for (int i=0; i<file.listFiles().length;i++){
            File tmp = file.listFiles()[i];
            String nom = tmp.getName();
            /*nom=(tmp.isFile())?nom.substring(0,nom.indexOf('.')):nom;
            listTrack.add(String.valueOf(nom));
            listTrackPath.add(String.valueOf(tmp.getAbsolutePath()));*/
            if(tmp.isFile()){
                if(nom.contains(".")) {
                    if (nom.substring(nom.lastIndexOf('.'), nom.length()).equals(".mp3") ||
                            nom.substring(nom.lastIndexOf('.'), nom.length()).equals(".m4a") ||
                            nom.substring(nom.lastIndexOf('.'), nom.length()).equals(".flac")) {
                        listTrack.add(String.valueOf(nom.substring(0, nom.lastIndexOf('.'))));
                        listTrackPath.add(String.valueOf(tmp.getAbsolutePath()));
                    }
                }
            }else{
                listTrack.add(String.valueOf("#"+nom));
                listTrackPath.add(String.valueOf(tmp.getAbsolutePath()));
            }
        }
        tv_actualPath.setText(actualPath);
        mAdapter.notifyDataSetChanged();
    }

    void info(){
        tv_duration.setText(m2s(mp.getDuration()));
        if(title==null){tv_title.setText(listTrack.get(idTrack));}
        else{tv_title.setText(title);}
        seekbar.setMax(mp.getDuration());
        handler.postDelayed(time, 100);
    }

    private void playMusic(){
        if(mp == null){
            mp = new MediaPlayer();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(!replay){next();}
                    else{playMusic();}
                }
            });
            next();
            pause();
            return;
        }
        if(listTrackPath.size()>0){
            mp.stop();
            mp.reset();

            try {
                if(pathImageThumb!=null) {
                    imgView.setImageURI(Uri.parse(pathImageThumb));
                }else{
                    imgView.setImageResource(R.drawable.play);
                }

                mmr.setDataSource(listTrackPath.get(idTrack));
                title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                byte[] artBytes = mmr.getEmbeddedPicture();

                if(artBytes != null)
                {
                    is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                    bm = BitmapFactory.decodeStream(is);
                    imgView.setImageBitmap(bm);
                }else {
                    imgView.setImageResource(R.drawable.play);
                }

                mp.setDataSource(getApplicationContext(), Uri.parse(listTrackPath.get(idTrack)));
                mp.prepare();
                mp.start();
                info();
                imgBt_pause.setImageResource(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Cannot load the music at " + listTrackPath.get(idTrack), Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "No music found !", Toast.LENGTH_SHORT).show();
        }
    }

    void next(){
        if(randomTrack){idTrack+=r.nextInt(listTrack.size());}
        do{
            idTrack+=1;
            if (idTrack >= listTrack.size()) {
                idTrack = 0;
            }
        }while(new File(listTrackPath.get(idTrack)).isDirectory());

        playMusic();
    }

    void back(){
        do{
            idTrack-=1;
            if (idTrack < 0) {
                idTrack = listTrackPath.size()-1;
            }
        }while(new File(listTrackPath.get(idTrack)).isDirectory());

        playMusic();
    }

    void play(){
        mp.start();
        imgBt_pause.setImageResource(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"));
    }

    void pause(){
        mp.pause();
        imgBt_pause.setImageResource(Resources.getSystem().getIdentifier("ic_media_play", "drawable", "android"));
    }

    private String m2s(int duration) {
        NumberFormat formatter = new DecimalFormat("00");
        int sec = duration/1000;
        int min = sec/60;
        sec=sec%60;


        return String.valueOf(min)+":"+String.valueOf(formatter.format(sec));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
