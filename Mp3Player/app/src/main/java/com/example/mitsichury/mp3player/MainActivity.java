package com.example.mitsichury.mp3player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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

import com.example.mitsichury.simplemp3player.R;

import java.io.ByteArrayInputStream;
import java.io.File;
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
    ListView lv_musicTrackList;
    File file;
    String actualPath;
    TextView tv_actualPath;
    ArrayAdapter<String> mAdapter;

    Random r;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    InputStream is;
    Bitmap bm;
    NumberFormat formatter = new DecimalFormat("00");

    String pathImageThumb;
    String title;

    float xTouchDown;
    float xTouchUp;

    /*New Variables*/
    Player player; //  The service Player
    BroadcastReceiver receiver;
    Intent intentService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Player.MyBinder mb = (Player.MyBinder)service;
            player = mb.getPlayer();
            player.init();

            // Explorer
            /*actualPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            printDirectory();

            idTrack = player.getActualTrack();
            invalidate();*/

            actualPath = player.getCurrentPath();
            idTrack = player.getActualTrack();
            printDirectory();
            invalidate();
            seekbar.setProgress(player.getCurrentPosition());

            Toast.makeText(getApplicationContext(), "Player connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            player = null;
            Toast.makeText(getApplicationContext(), "Player disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link to the player service
        intentService = new Intent(this, Player.class);
        bindService(intentService, connection, Context.BIND_AUTO_CREATE);

        listTrack = new ArrayList<String>();
        listTrackPath = new ArrayList<String>();
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
                if (tmp != null) {
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
                position = position - 1;
                if (new File(listTrackPath.get(position)).isDirectory()) {
                    actualPath = listTrackPath.get(position);
                    printDirectory();
                } else {
                    if(player.getCurrentPath() != actualPath){player.setTrackPath(listTrackPath, actualPath);}
                    idTrack = position;
                    player.setTrackChange(idTrack);
                    invalidate();
                }
            }
        });

        // Listeners
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(seekBar.getProgress());
                    tv_position.setText(m2s(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(time);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
                handler.postDelayed(time, 100);
            }
        });


        imgBt_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        });

        imgBt_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
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
                if(player.isRandom()){
                    //randomTrack=false;
                    player.setRandom(false);
                    imgBt_random.setBackgroundColor(0xFFD6D7D7);
                }
                else{
                    //randomTrack=true;
                    player.setRandom(true);
                    imgBt_random.setBackgroundColor(0xFF000000);
                }
            }
        });

        imgBt_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isReplay()) {
                    //replay=false;
                    player.setReplay(false);
                    imgBt_replay.setBackgroundColor(0xFFD6D7D7);
                } else {
                    //replay=true;
                    player.setReplay(true);
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
                        previous();
                    }else {
                        if(player.isPlaying()){pause();} else {play();}
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
                int curPos = player.getCurrentPosition();
                tv_position.setText(m2s(curPos));
                seekbar.setProgress(curPos);
                handler.postDelayed(this, 100);
            }
        };

        // Receiver from the service
         receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(Player.PLAYER_MESSAGE);
                idTrack = player.getActualTrack();
                invalidate();
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Player.PLAYER_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
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
        tv_duration.setText(m2s(player.getDuration()));
        if(title==null){tv_title.setText(listTrack.get(idTrack));}
        else{tv_title.setText(title);}
        seekbar.setMax(player.getDuration());
        handler.postDelayed(time, 100);
    }

    /**
     *  The method try to get the picture and informations about the song and display them
     */
    private void invalidate(){

        if(listTrackPath.size()>0){

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

            info();
            imgBt_pause.setImageResource(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"));

        }
    }

    //TODO
    void next(){
        player.next();
        idTrack = player.getActualTrack();
        invalidate();
    }

    void previous(){
        player.previous();
        idTrack = player.getActualTrack();
        invalidate();
    }

    void play(){
        player.play();
        invalidate();
        imgBt_pause.setImageResource(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"));
    }

    void pause(){
        player.pause();
        invalidate();
        imgBt_pause.setImageResource(Resources.getSystem().getIdentifier("ic_media_play", "drawable", "android"));
    }

    private String m2s(int duration) {
        int sec = duration/1000;
        int min = sec/60;
        sec=sec%60;

        return String.valueOf(min)+":"+String.valueOf(formatter.format(sec));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(time);
        player.saveSharedPreferences();
        super.onDestroy();
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
