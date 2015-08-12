package com.example.mitsichury.firstwebservicegetinformation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ArrayList<Header> listData;
    ExpandableListView expLv;
    ImageView imgV;
    Menu menu;

    Handler handler;
    Runnable run;
    ProgressBar progressBar;

    /**
     * Sort by show date
     */
    Comparator sortByShowDate = new Comparator<Header>() {
        @Override
        public int compare(Header lhs, Header rhs) {
            return (lhs.getDate().before(rhs.getDate())) ? -1 : 1;
        }
    };

    /**
     * Sort by date release in RSS feed
     */
    Comparator sortByRealease = new Comparator<Header>() {
        @Override
        public int compare(Header lhs, Header rhs) {
            return (lhs.getDateParution().before(rhs.getDateParution())) ? 1 : -1;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Need if the app is killef before the data is loaded
        listData = new ArrayList<>();

        imgV = (ImageView) findViewById(R.id.imgViewChargement);
        progressBar = (ProgressBar) findViewById(R.id.pgBar);

        handler = new Handler();

        // Needeed to avoid freeze application
        run = new Runnable() {
            @Override
            public void run() {
                prepareData();
            }
        };

        // Get the width of the screen to place the indicator
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        expLv = (ExpandableListView) findViewById(R.id.expandableLv);

        // Check wich method need to be use because of api level
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expLv.setIndicatorBounds(width - 100, 0);
        } else {
            expLv.setIndicatorBoundsRelative(width - 100, 0);
        }

        // If the screen rotate, do not need to reload and dl all the data
        if (savedInstanceState != null) {
            imgV.setVisibility(View.GONE); // Launch pic
            progressBar.setVisibility(View.GONE); // Lauch progBar
            listData = (ArrayList<Header>) savedInstanceState.getSerializable("HEADER");
            listAdapter = new ExpandableListAdapter(this, listData);
            expLv.setAdapter(listAdapter);
        }
    }

    /**
     * When the sreen rotate, save the data
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        if (listData != null || listData.isEmpty()) {
            savedInstance.putSerializable("HEADER", listData);
        }
    }

    /**
     * Each time the app start, it checks if internet connexion is enble and if the data already exist in a previous start (screen rotation)
     */
    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mData = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mWifi.isConnected() || mData.isConnected()) {
            if (listAdapter == null) {
                // Avoid freezing start
                // Can be refact in AsyncTask
                handler.post(run);
            }
        } else {
            // If no connection, it displays a toast and leave the app
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connexion), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void prepareData() {
        // Create the parser
        final ParserXML obj = new ParserXML("http://www.zenithlimoges.com/?rss");
        // DL XML File
        obj.dlXMLfile();

        // While the File is not dl and parse the app wait (new Runnable to avoid freezing with a while)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!obj.isParsingComplete()) {
                    handler.postDelayed(this, 100);
                } else {
                    // Get the data from xml file
                    listData = obj.getData();
                    // Sort by showcase date
                    Collections.sort(listData, sortByShowDate);
                    // Create the custom adapter
                    listAdapter = new ExpandableListAdapter(getApplicationContext(), listData);
                    expLv.setAdapter(listAdapter);
                    // Hide lauch picture
                    imgV.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    // Display menu option
                    menu.setGroupVisible(0, true);
                }
            }
        }, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Hide menu to avoid sort whereas the data is not loaded
        menu.setGroupVisible(0, false);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        ///String name = item.getTitle().toString();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_by_feed_app) {
            Collections.sort(listData, sortByRealease);
            listAdapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.about) {
            startActivity(new Intent(this, About.class));
        } else if (id == R.id.sort_by_show) {
            Collections.sort(listData, sortByShowDate);
            listAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
