package com.example.prax.vis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class Historydatabse extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "History.db";
    public static final String TABLE_NAME = "History";
    public static final String colvehicleid = "id";
    public static final String colname = "name";
    public static final String coltime = "time";
    public static final String coldate = "date";

    public Historydatabse(@Nullable Context context) {
        super(context,DATABASE_NAME,null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME +
                " ("+colvehicleid+" VARCHAR NOT NULL, "+ colname + " VARCHAR, "
                + coltime + " VARCHAR, " + coldate + " VARCHAR )";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void addData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();

        cv1.put(colvehicleid, "MM0AV8866");
        cv1.put(colname, "Prashant Kumar");
        cv1.put(coltime, "11:50");
        cv1.put(coldate, "10/11/2018");
        db.insert(TABLE_NAME, null, cv1);

        ContentValues cv2 = new ContentValues();

        cv2.put(colvehicleid,"NA 123456");
        cv2.put(colname,"Sandeep Bhandari");
        cv2.put(coltime,"09:50");
        cv2.put(coldate,"11/11/2018");
        db.insert(TABLE_NAME, null, cv2);

        ContentValues cv3 = new ContentValues();

        cv3.put(colvehicleid,"NA 789123");
        cv3.put(colname,"Suresh Paudel");
        cv3.put(coltime,"14:10");
        cv3.put(coldate,"11/11/2018");
        db.insert(TABLE_NAME, null, cv3);
    }
    public void savenewItem(String id,String name,String time,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv3 = new ContentValues();

        cv3.put(colvehicleid,id);
        cv3.put(colname,name);
        cv3.put(coltime,time);
        cv3.put(coldate,date);
        db.insert(TABLE_NAME, null, cv3);
    }
    public Cursor getListContents(String vehid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME ,null);
        return data;

    }
}
