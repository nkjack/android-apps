package com.studentadvisor.noam.studentadvisor.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.School;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import java.util.ArrayList;
import java.util.Date;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBComments.*;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_DEGREE_LIKES;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.TABLE_ALL_DEGREES;

/**
 * Created by Noam on 12/19/2015.
 */
public class DBComments {
    public static final int ALL_DEGREES = 0;
    public static final int ALL_SUBJECTS = 1;
    public static final int ALL_SCHOOLS = 2;
    private CommentsHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBComments(Context context) {
        Log.d("noam", "DBComments");
        mHelper = new CommentsHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertComment(Post post, boolean clearPrevious, String user_login_id) {

//        if (clearPrevious) {
//            deleteComments();
//        }
        deleteSpecificComment(user_login_id, post.getDbid_post());

        L.m("DBID POST -- " + post.getDbid_post() + " ");

        //create a sql prepared statement
        String sql = "INSERT INTO " + TABLE_ALL_COMMENTS + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        //for a given column index, simply bind the data to be put inside that index
        statement.bindLong(1, post.getDbid_post());
        statement.bindString(2, post.getBody_content());
        statement.bindLong(3, post.getDate_added() == null ? -1 : post.getDate_added().getTime());
        statement.bindString(4, post.getUser_name());
        statement.bindString(5, post.getUser_create_id());
        statement.bindLong(6, post.getDbid_user_pic());
        statement.bindLong(7, post.getDegree_id());
        statement.bindLong(8, post.getRate());
        statement.bindString(9, post.getTypePost());
        statement.bindLong(10, post.getTotalScore());
        statement.bindLong(11, post.getTotalComments());
        statement.bindLong(12, post.getUser_choice());
        statement.bindString(13, post.getDegree_name());
        statement.bindString(14, post.getFaculty_name());
        statement.bindString(15, post.getSchool_name());
        statement.bindString(16, user_login_id);

        try {
            statement.execute();
        }
        catch (Exception e){
            L.m(e.toString());
        }

        //set the transaction as successful and end the transaction
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }


    public ArrayList<Post> readPosts(String user_log_in) {
        ArrayList<Post> listPosts = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {COLUMN_COMMENT_POST_ID ,
                COLUMN_COMMENT_BODY_CONTENT ,
                COLUMN_COMMENT_DATE_ADDED ,
                COLUMN_COMMENT_USER_NAME ,
                COLUMN_COMMENT_USER_ID ,
                COLUMN_COMMENT_USER_PIC_ID,
                COLUMN_COMMENT_DEGREE_ID ,
                COLUMN_COMMENT_RATE ,
                COLUMN_COMMENT_TYPE_POST ,
                COLUMN_COMMENT_TOTAL_SCORE ,
                COLUMN_COMMENT_TOTAL_COMMENTS ,
                COLUMN_COMMENT_USER_CHOICE ,
                COLUMN_COMMENT_DEGREE_NAME ,
                COLUMN_COMMENT_FACULTY_NAME ,
                COLUMN_COMMENT_SCHOOL_NAME

        };
        L.m(TABLE_ALL_COMMENTS);

        String query = "SELECT DISTINCT * FROM " + TABLE_ALL_COMMENTS +
                " WHERE " + COLUMN_COMMENT_USER_LOGIN_ID + " = '" + user_log_in + "'";
        L.m(query);

        Cursor cursor = mDatabase.rawQuery(query, null);
//        Cursor cursor = mDatabase.query(TABLE_ALL_COMMENTS, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                Post post = new Post();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                post.setDbid_post(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_POST_ID)));
                post.setBody_content(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_BODY_CONTENT)));

                long dateAddedMilliseconds = cursor.getLong(cursor.getColumnIndex(COLUMN_COMMENT_DATE_ADDED));
                post.setDate_added(dateAddedMilliseconds != -1 ? new Date(dateAddedMilliseconds) : null);

                post.setUser_name(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_USER_NAME)));
                post.setUser_create_id(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_USER_ID)));
                post.setDbid_user_pic(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_USER_PIC_ID)));
                post.setDegree_id(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_DEGREE_ID)));
                post.setRate(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_RATE)));
                post.setTypePost(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_TYPE_POST)));
                post.setTotalScore(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_TOTAL_SCORE)));
                post.setTotalComments(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_TOTAL_COMMENTS)));
                post.setUser_choice(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_USER_CHOICE)));
                post.setDegree_name(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_DEGREE_NAME)));
                post.setFaculty_name(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_FACULTY_NAME)));
                post.setSchool_name(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_SCHOOL_NAME)));

                //add the Degree to the list of Degree objects which we plan to return
                listPosts.add(post);
            }
            while (cursor.moveToNext());
        }
        return listPosts;
    }


    public void deleteComments() {
        mDatabase.delete(TABLE_ALL_COMMENTS, null, null);
    }

    public void deleteSpecificComment(String log_in_user, int post_id) {
        String sql = "DELETE FROM " + TABLE_ALL_COMMENTS + " WHERE " +
                COLUMN_COMMENT_USER_LOGIN_ID + " = '" + log_in_user + "' AND " +
                COLUMN_COMMENT_POST_ID + " = " +  post_id + " ;";

        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("delete from favorites - specific -  "  + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

    }

    private static class CommentsHelper extends SQLiteOpenHelper {

        private static final String CREATE_TABLE_ALL_COMMENTS = "CREATE TABLE " + TABLE_ALL_COMMENTS + " (" +
                COLUMN_COMMENT_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_COMMENT_BODY_CONTENT + " TEXT," +
                COLUMN_COMMENT_DATE_ADDED + " INTEGER," +
                COLUMN_COMMENT_USER_NAME + " TEXT," +
                COLUMN_COMMENT_USER_ID + " TEXT," +
                COLUMN_COMMENT_USER_PIC_ID + " INTEGER, " +
                COLUMN_COMMENT_DEGREE_ID + " INTEGER, " +
                COLUMN_COMMENT_RATE + " INTEGER," +
                COLUMN_COMMENT_TYPE_POST + " TEXT," +
                COLUMN_COMMENT_TOTAL_SCORE + " INTEGER," +
                COLUMN_COMMENT_TOTAL_COMMENTS + " INTEGER," +
                COLUMN_COMMENT_USER_CHOICE + " INTEGER, " +
                COLUMN_COMMENT_DEGREE_NAME + " TEXT, " +
                COLUMN_COMMENT_FACULTY_NAME + " TEXT, " +
                COLUMN_COMMENT_SCHOOL_NAME + " TEXT, " +
                COLUMN_COMMENT_USER_LOGIN_ID + " TEXT " +
                ");";


        private static final String DB_NAME = "comments_db";
        private static final int DB_VERSION = 2;

        private Context mContext;

        public CommentsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_ALL_COMMENTS);
                Log.d("noam", "create table all comments executed");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table all degrees executed");
//                db.execSQL(" DROP TABLE " + TABLE_ALL_COMMENTS + " IF EXISTS;");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_COMMENTS + ";");

                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }
}

