package com.example.mitsichury.firstsqldatabase;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    Button bt_add;
    ListView lv;
    ArrayList<Num> numInDb;
    ArrayAdapter<Num> mAdapter;
    ManageDb db;
    Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new ManageDb(this);


        r = new Random();

        db.open();
        numInDb = db.getNum();
        db.close();

        bt_add = (Button)findViewById(R.id.bt_add);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                db.insertValue(r.nextInt(1000));
                numInDb.clear();
                numInDb.addAll(db.getNum());
                mAdapter.notifyDataSetChanged();
                db.close();
            }
        });

        lv = (ListView)findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<Num>(this, android.R.layout.simple_list_item_1, numInDb);
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                db.open();
                Toast.makeText(getApplicationContext(), numInDb.get(position).getId()+" deleted", Toast.LENGTH_SHORT).show();
                db.delete(numInDb.get(position).getId());
                numInDb.clear();
                numInDb.addAll(db.getNum());
                mAdapter.notifyDataSetChanged();
                db.close();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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
