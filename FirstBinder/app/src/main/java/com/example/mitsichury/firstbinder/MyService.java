package com.example.mitsichury.firstbinder;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Random;

public class MyService extends Service {

    Random r = new Random();

    public class MyBinder extends Binder{

        public MyService getService(){
            return MyService.this;
        }
    }

    private int state;

    /*@Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(getApplicationContext(), "Coin Coin", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Coin Coin onCreate", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public int plus(){
        state++;
        return state;
    }

    public int minus(){
        state--;
        return state;
    }
}
