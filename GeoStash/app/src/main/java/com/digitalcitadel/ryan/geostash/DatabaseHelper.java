package com.digitalcitadel.ryan.geostash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by ryan on 3/17/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stash.db";
    public static final String TABLE_NAME = "stash_table";

    public static final String COL_1 = "_id";
    public static final String COL_2 = "_latitude";
    public static final String COL_3 = "_longitude";
    public static final String COL_4 = "_description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY " +
                "AUTOINCREMENT," +
                COL_2 + " TEXT," +
                COL_3 + " TEXT," +
                COL_4 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void insertData(String description, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, latitude);
        contentValues.put(COL_3, longitude);
        contentValues.put(COL_4, description);
        long result = db.insert(TABLE_NAME, null, contentValues);
//        if (result == -1)
//            return false;
//        else
//            return true;
    }

    public boolean removeData(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numRows = db.delete(TABLE_NAME, COL_1 + "=" + id, null);
        return (numRows == 1);
    }

    public ArrayList<Stash> getStashes()
    {
        ArrayList<Stash> stashList = new ArrayList<Stash>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                long id = cursor.getLong(0);
                String latitude = cursor.getString(1);
                String longtitude = cursor.getString(2);
                String description = cursor.getString(3);
                Stash stash = new Stash(id,latitude, longtitude, description);
                stashList.add(stash);
                cursor.moveToNext();
            }
        }
        return stashList;
    }
}
