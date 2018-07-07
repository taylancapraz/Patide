package com.wrexsoft.canturgut.patide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by canta on 5/21/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "gameos.db";

    public static final String TABLE_NAME = "events";
    public static final String COL_1 = "TABLEID";
    public static final String COL_2 = "ID";
    public static final String COL_3 = "COMMENTS";
    public static final String COL_4 = "DATE";
    public static final String COL_5 = "ESTIMATEDTIME";
    public static final String COL_6 = "EVENTNAME";
    public static final String COL_7 = "PRIORITY";

    public static final String TABLE_NAME3 = "eventtoadd";
    public static final String COL2_1 = "TABLEID";
    public static final String COL2_2 = "ID";
    public static final String COL2_3 = "METHOD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (TABLEID INTEGER PRIMARY KEY AUTOINCREMENT, ID TEXT UNIQUE,COMMENTS TEXT,DATE TEXT UNIQUE, ESTIMATEDTIME TEXT, EVENTNAME TEXT, PRIORITY TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME3 + " (TABLEID INTEGER PRIMARY KEY AUTOINCREMENT, ID TEXT UNIQUE, METHOD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME3);
        onCreate(db);
    }

    public boolean insertData(String id, String comments, String date, String estimatedtime, String eventname, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, id);
        contentValues.put(COL_3, comments);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, estimatedtime);
        contentValues.put(COL_6, eventname);
        contentValues.put(COL_7, priority);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            Log.d("databaseInsert", COL_2 + ": " + id);
            Log.d("databaseInsert", COL_3 + ": " + comments);
            Log.d("databaseInsert", COL_4 + ": " + date);
            Log.d("databaseInsert", COL_5 + ": " + estimatedtime);
            Log.d("databaseInsert", COL_6 + ": " + eventname);
            Log.d("databaseInsert", COL_7 + ": " + priority);
            Log.d("databaseInsert", "-----------------------");
            return true;
        }
    }

    public Cursor getSQLiteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null);
        return result;
    }

    public boolean updateData(String id, String comments, String date, String estimatedtime, String eventname, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, id);
        contentValues.put(COL_3, comments);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, estimatedtime);
        contentValues.put(COL_6, eventname);
        contentValues.put(COL_7, priority);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }


    public void removeEvent(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_2 + "= '" + id + "'");
        Log.d("databaseInsert", "Event with the following ID is removed: " + id);
        database.close();
    }

    public boolean insertToKuyruk(String id, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2_2, id);
        contentValues.put(COL2_3, type);
        long result = db.insert(TABLE_NAME3, null, contentValues);
        Log.d("databaseInsert", "-----------------------");
        Log.d("databaseInsert", " TRY  ");
        Log.d("databaseInsert", COL2_2 + ": " + id);
        Log.d("databaseInsert", "-----------------------");

        if (result == -1) {
            return false;
        } else {
            Log.d("databaseInsert", "-----------------------");
            Log.d("databaseInsert", " Following is added to database");
            Log.d("databaseInsert", COL2_2 + ": " + id);
            Log.d("databaseInsert", "-----------------------");
            return true;
        }
    }

    public Cursor getKuyrukData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME3, null);
        return result;
    }

    public void removeFromKuyruk(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME3 + " WHERE " + COL2_2 + "= '" + id + "'");
        Log.d("databaseInsert", "Event with the following ID is removed from kuyruk: " + id);
        database.close();
    }

    public void dropAllTables() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
        database.execSQL("CREATE TABLE " + TABLE_NAME + " (TABLEID INTEGER PRIMARY KEY AUTOINCREMENT, ID TEXT UNIQUE,COMMENTS TEXT,DATE TEXT, ESTIMATEDTIME TEXT, EVENTNAME TEXT, PRIORITY TEXT)");
        database.execSQL("CREATE TABLE " + TABLE_NAME3 + " (TABLEID INTEGER PRIMARY KEY AUTOINCREMENT, ID TEXT UNIQUE)");

        database.close();
    }
}
