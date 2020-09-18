package com.studentadvisor.noam.studentadvisor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.studentadvisor.noam.studentadvisor.logging.L;

import java.util.Date;
import java.util.HashMap;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_DEGREE_LIKES;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.TABLE_ALL_DEGREES;

/**
 * Created by Noam on 11/15/2015.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_SEEN_INTRO = "seen_intro";
    private static final String KEY_PIC_ID = "dbid_user_pic";
    private static final String KEY_TYPE_USER = "user_type";
    private static final String KEY_YEARS_STUDY = "user_year";
    private static final String KEY_DEGREE_1 = "user_degree_1";
    private static final String KEY_DEGREE_2 = "user_degree_2";
    private static final String KEY_DEGREE_3 = "user_degree_3";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT, " + KEY_SEEN_INTRO + " TEXT, "
                + KEY_PIC_ID + " INTEGER, " + KEY_TYPE_USER + " TEXT, "
                + KEY_YEARS_STUDY + " INTEGER, " + KEY_DEGREE_1 + " INTEGER,"
                + KEY_DEGREE_2 + " INTEGER, " + KEY_DEGREE_3 + " INTEGER "
                + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database Users created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name
                        , String email
                        , String uid
                        , String created_at
                        , String seen_intro
                        , int dbid_user_pic
                        , String user_type
                        , int user_year
                        , int user_degree_1
                        , int user_degree_2
                        , int user_degree_3) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_SEEN_INTRO, seen_intro); // Seen Intro
        values.put(KEY_PIC_ID, dbid_user_pic); // id_pic
        values.put(KEY_TYPE_USER, user_type);
        values.put(KEY_YEARS_STUDY, user_year);
        values.put(KEY_DEGREE_1, user_degree_1);
        values.put(KEY_DEGREE_2, user_degree_2);
        values.put(KEY_DEGREE_3, user_degree_3);


        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
            user.put("seen_intro", cursor.getString(5));
            user.put("dbid_user_pic", String.valueOf(cursor.getInt(6)));
            user.put("user_type", cursor.getString(7));
            user.put("user_year", String.valueOf(cursor.getInt(8)));
            user.put("user_degree_1", String.valueOf(cursor.getInt(9)));
            user.put("user_degree_2", String.valueOf(cursor.getInt(10)));
            user.put("user_degree_3", String.valueOf(cursor.getInt(11)));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void updateSeenInto(String unique_id){
        String updateQuery = "UPDATE " + TABLE_USER + " SET " + KEY_SEEN_INTRO + " = 'yes' WHERE "
                + KEY_UID + " = '" + unique_id + "' ;";

        SQLiteDatabase mDatabase = this.getWritableDatabase();

        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(updateQuery);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("update seen_intro " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void updateQuery(String unique_id, String user_type, int user_year, int degree_1,
                            int degree_2, int degree_3){
        String updateQuery = "UPDATE " + TABLE_USER + " SET "
                + KEY_TYPE_USER + " = '"+ user_type +"' , "
                + KEY_YEARS_STUDY + " = "+ user_year +" , "
                + KEY_DEGREE_1 + " = "+ degree_1 +" , "
                + KEY_DEGREE_2 + " = "+ degree_2 +" , "
                + KEY_DEGREE_3 + " = "+ degree_3 +" "
                + "WHERE " + KEY_UID + " = '" + unique_id + "' ;";

        L.m(updateQuery);

        SQLiteDatabase mDatabase = this.getWritableDatabase();

        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(updateQuery);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("update settings " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }
}
