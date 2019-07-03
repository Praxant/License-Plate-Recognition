package com.example.prax.vis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Mydatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Informations.db";
    public static final String TABLE_NAME = "Information";
    public static final String colvehicleid = "id";
    public static final String colname = "name";

    public static final String coladdress = "address";

    public static final String colcontact = "contact";


    public Mydatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME +
                " ("+colvehicleid+" VARCHAR NOT NULL, "+ colname + " VARCHAR, "
                 + colcontact + " VARCHAR, " + coladdress + " VARCHAR  )";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void addData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();

        cv1.put(colvehicleid,"MM0AV8866");
        cv1.put(colname,"Prashant Kumar");
        cv1.put(coladdress,"Rajbiraj-4,Saptari, Nepal");
        cv1.put(colcontact,"9053580166");

        db.insert(TABLE_NAME, null, cv1);

        ContentValues cv2 = new ContentValues();

        cv2.put(colvehicleid,"NA 123456");
        cv2.put(colname,"Sandeep Bhandari");
        cv2.put(coladdress,"Butwal, Nepal");
        cv2.put(colcontact,"9053580167");
        db.insert(TABLE_NAME, null, cv2);

        ContentValues cv3 = new ContentValues();

        cv3.put(colvehicleid,"NA 789123");
        cv3.put(colname,"Suresh Paudel");
        cv3.put(coladdress,"Parbat, Nepal");
        cv3.put(colcontact,"90535801678");
        db.insert(TABLE_NAME, null, cv3);

    }

    public void savenewItem(String id,String name,String contact,String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv3 = new ContentValues();

        cv3.put(colvehicleid,id);
        cv3.put(colname,name);
        cv3.put(coladdress,address);
        cv3.put(colcontact,contact);
        db.insert(TABLE_NAME, null, cv3);
    }

    public boolean searchforserver(String vehid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE id LIKE  '%"+vehid+"%'",null);
        if(data.getCount()<=0)
        {
            data.close();
            return false;

        }
        data.close();
        return true;
    }
    public Cursor getListContents(String vehid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE id LIKE  '%"+vehid+"%'",null);
        return data;

    }



}
