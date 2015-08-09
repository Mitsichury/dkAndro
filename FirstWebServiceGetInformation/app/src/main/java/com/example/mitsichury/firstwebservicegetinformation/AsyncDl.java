package com.example.mitsichury.firstwebservicegetinformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by MITSICHURY on 08/08/2015.
 */
public class AsyncDl extends AsyncTask {

    Header head;
    ExpandableListAdapter expandableListAdapter;

    @Override
    protected Bitmap doInBackground(Object[] params) {
        //Log.i("ASYNC", "DEBUT de "+head.getTitle());
        head = (Header)params[0];
        expandableListAdapter = (ExpandableListAdapter) params[2];
        return dlBitmap((String) params[1]);
    }

    @Override
    protected void onPostExecute(Object o) {
        //super.onPostExecute(o);
        head.setImage((Bitmap)o);
        expandableListAdapter.notifyDataSetChanged();
        //Log.i("ASYNC", "FIN de "+head.getTitle()+" "+((Bitmap)o==null));
    }

    private Bitmap dlBitmap(String linkToImage) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream((InputStream) new URL(linkToImage).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("DLL", "DLL de "+head.getTitle()+" "+(bmp==null));
        return bmp;
    }
}
