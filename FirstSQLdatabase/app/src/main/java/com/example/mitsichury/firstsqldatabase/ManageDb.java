package com.example.mitsichury.firstsqldatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by MITSICHURY on 05/08/2015.
 */
public class ManageDb {
    private SQLiteDatabase bdd;
    private CreateTable maBase;

    String tableName = "TEST";
    String rowNum = "num";

    /*
    * Constructeur qui crée la bdd
    */
    public ManageDb(Context context){
        maBase = new CreateTable(context, tableName, null, 1);
    }

    /**
     * Ouvre la bdd en ecriture
     */
    public void open(){
        bdd = maBase.getWritableDatabase();
    }

    /**
     * Ferme l'acces à la bdd
     */
    public void close(){
        bdd.close();
    }

    public void insertValue(int num){
        ContentValues values = new ContentValues();
        values.put(rowNum, num);
        bdd.insert(tableName, null, values);
    }

    public void delete(int id){
        bdd.delete(tableName, "id" + "=" + id, null);
    }

    ArrayList getNum(){
        ArrayList<Num> entry = new ArrayList<>();
        Cursor c = bdd.query(tableName, new String[]{"id","num"},null , null, null, null, null);
        while (c.moveToNext()){
            entry.add(new Num(c.getInt(0), c.getInt(1)));
        }

        return entry;
    }
}
