package com.example.mitsichury.firsttestlocation;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    TextView tv;

    double distance;
    ArrayList<Location> fixPos;
    ListView lv;
    ArrayList<String> stringPos;
    ArrayAdapter<String> mAdpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fixPos = new ArrayList<>();
        stringPos = new ArrayList<>();
        lv = (ListView)findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fixPos.remove(position);
                L2S();
                mAdpater.notifyDataSetChanged();
                calc();
            }
        });
        L2S();
        mAdpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringPos);
        lv.setAdapter(mAdpater);

        distance=0;

        tv = (TextView)findViewById(R.id.tv);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                fixPos.add(location);
                calc();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Change of status
                Toast.makeText(getApplicationContext(), "stat change "+provider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "prov enable "+provider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "prov disable "+provider, Toast.LENGTH_SHORT).show();
            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, ll);
    }

    private void calc() {
        distance =0.0;
        if (fixPos.size()>1){
            for (int i=0; i<fixPos.size()-1;i++){
                distance+=fixPos.get(i+1).distanceTo(fixPos.get(i));
            }
            L2S();
            mAdpater.notifyDataSetChanged();
        }
        tv.setText("Distance : "+distance);
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

    void L2S(){
        stringPos.clear();
        for (int i=0; i<fixPos.size();i++){
            stringPos.add(fixPos.get(i).toString());
        }
    }
}
