package com.ford.emergencyconnect;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDb";

    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CREATE = "CREATE TABLE user ("
            +"email TEXT PRIMARY KEY, "
            +"password TEXT, "
            +"name TEXT, "
            +"phone TEXT, "
            +"age INTEGER, "
            +"skills TEXT, "
            +"preConditions TEXT, "
            +"mode TEXT);";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase UserDb, int oldVersion, int newVersion) {
        UserDb.execSQL("DROP TABLE IF EXISTS UserDb");
        onCreate(UserDb);
    }
}
