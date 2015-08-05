package com.example.mitsichury.firstsqldatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MITSICHURY on 05/08/2015.
 */
public class CreateTable extends SQLiteOpenHelper {

    private String tableDefault = "CREATE TABLE IF NOT EXISTS TEST(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "num INTEGER" +
            ");";
    public CreateTable(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableDefault);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table TEST");
        db.execSQL(tableDefault);
    }
}
