package com.ford.emergencyconnect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class UserDb {

    private UserDbHelper dbHelper;
    private SQLiteDatabase db;

    private final static String EMP_TABLE="user";
    private final static String EMP_EMAIL="email";
    private final static String EMP_PASSWORD="password";
    private final static String EMP_NAME="name";
    private final static String EMP_PHONE="phone";
    private final static String EMP_AGE="age";
    private final static String EMP_SKILLS="skills";
    private final static String EMP_PRECONDITIONS="preConditions";
    private final static String EMP_MODE="mode";

    public UserDb(Context context){
        dbHelper = new UserDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void createUser(User user) {
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(EMP_EMAIL, user.email);
            values.put(EMP_PASSWORD, user.password);
            values.put(EMP_NAME, user.name);
            values.put(EMP_PHONE, user.phone);
            values.put(EMP_AGE, user.age);
            values.put(EMP_SKILLS, user.skills);
            values.put(EMP_PRECONDITIONS, user.preConditions);
            values.put(EMP_MODE, user.mode);
            db.insertOrThrow(EMP_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public User findUser(String email, String password) {
        Cursor cursor = getDbCursor();
        User user = null;
        for (int i=0; i<cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            if (cursor.getString(cursor.getColumnIndex("email")).equalsIgnoreCase(email)&&cursor.getString(cursor.getColumnIndex("password")).equalsIgnoreCase(password)) {
                user = new User(cursor.getString(cursor.getColumnIndex("email")),
                        cursor.getString(cursor.getColumnIndex("password")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("phone")),
                        cursor.getInt(cursor.getColumnIndex("age")),
                        cursor.getString(cursor.getColumnIndex("skills")),
                        cursor.getString(cursor.getColumnIndex("preConditions")),
                        cursor.getString(cursor.getColumnIndex("mode")));
            }
        }
        cursor.close();
        return user;
    }

    public int numUsers() {
        Cursor cursor = getDbCursor();
        int n = cursor.getCount();
        cursor.close();
        return n;
    }

    private Cursor getDbCursor() {
        String[] cols = new String[] {EMP_EMAIL, EMP_PASSWORD, EMP_NAME, EMP_PHONE, EMP_AGE, EMP_SKILLS, EMP_PRECONDITIONS, EMP_MODE};
        Cursor cursor = db.query(true, EMP_TABLE, cols, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToPosition(0);
        }
        return cursor;
    }
}

