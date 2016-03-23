package com.ford.emergencyconnect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDb {

    private UserDbHelper dbHelper;
    private SQLiteDatabase db;

    public final static String EMP_TABLE="user";
    public final static String EMP_EMAIL="email";
    public final static String EMP_PASSWORD="password";
    public final static String EMP_NAME="name";
    public final static String EMP_PHONE="phone";
    public final static String EMP_AGE="age";
    public final static String EMP_SKILLS="skills";
    public final static String EMP_PRECONDITIONS="preConditions";
    public final static String EMP_MODE="mode";

    public UserDb(Context context){
        dbHelper = new UserDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long createUser(User user) {
        ContentValues values = new ContentValues();
        values.put(EMP_EMAIL, user.email);
        values.put(EMP_PASSWORD, user.password);
        values.put(EMP_NAME, user.name);
        values.put(EMP_PHONE, user.phone);
        values.put(EMP_AGE, user.age);
        values.put(EMP_SKILLS, user.skills);
        values.put(EMP_PRECONDITIONS, user.preConditions);
        values.put(EMP_MODE, user.mode);
        return db.insert(EMP_TABLE, null, values);
    }

    public User findUser(String email, String password) {
        String[] cols = new String[] {EMP_EMAIL, EMP_PASSWORD, EMP_NAME, EMP_PHONE, EMP_AGE, EMP_SKILLS, EMP_PRECONDITIONS, EMP_MODE};
        Cursor cursor = db.query(true, EMP_TABLE, cols, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToPosition(0);
        }
        User user = null;
        for (int i=0; i<cursor.getCount(); i++) {
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
        return user;
    }
}

