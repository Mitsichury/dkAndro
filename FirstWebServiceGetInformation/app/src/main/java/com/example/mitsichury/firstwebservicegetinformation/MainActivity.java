package com.example.mitsichury.firstwebservicegetinformation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends Activity{

    ExpandableListAdapter listAdapter;
    ArrayList<Header> listData;
    ExpandableListView expLv;
    ImageView imgV;

    Handler handler;
    Runnable run;
    ProgressBar progressBar;

    Comparator triParShow = new Comparator<Header>() {
        @Override
        public int compare(Header lhs, Header rhs) {
            return (lhs.getDate().before(rhs.getDate()))?-1:1;
        }
    };

    Comparator triParParution = new Comparator<Header>() {
        @Override
        public int compare(Header lhs, Header rhs) {
            return (lhs.getDateParution().before(rhs.getDateParution()))?-1:1;
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgV = (ImageView)findViewById(R.id.imgViewChargement);
        progressBar = (ProgressBar)findViewById(R.id.pgBar);

        handler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                prepareData();
                listAdapter = new ExpandableListAdapter(getApplicationContext(), listData);
                expLv.setAdapter(listAdapter);
                imgV.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        };

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        expLv =(ExpandableListView)findViewById(R.id.expandableLv);
        expLv.setIndicatorBoundsRelative(width-100, 0);
        listData = new ArrayList<>();

        if(savedInstanceState != null){
            imgV.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            listData = (ArrayList<Header>) savedInstanceState.getSerializable("HEADER");
            listAdapter = new ExpandableListAdapter(getApplicationContext(), listData);
            expLv.setAdapter(listAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        if(listData != null || listData.isEmpty()){savedInstance.putSerializable("HEADER", listData);}
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(listAdapter == null){
            /*prepareData();
            listAdapter = new ExpandableListAdapter(this, listData);
            expLv.setAdapter(listAdapter);*/
            handler.postDelayed(run, 100);
        }
    }

    private void prepareData() {
        //Toast.makeText(getApplicationContext(), "DL", Toast.LENGTH_SHORT).show();
        final ParserXML obj = new ParserXML("http://www.zenithlimoges.com/?rss");
        obj.dlXMLfile();
        while (!obj.isParsingComplete());
        listData = obj.getData();
        Collections.sort(listData, triParShow);
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
        String name = item.getTitle().toString();

        //noinspection SimplifiableIfStatement
        if (name.equals("Tri date parution")) {
            Collections.sort(listData, triParParution);
            listAdapter.notifyDataSetChanged();
            return true;
        }else{
            Collections.sort(listData, triParShow);
            listAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
