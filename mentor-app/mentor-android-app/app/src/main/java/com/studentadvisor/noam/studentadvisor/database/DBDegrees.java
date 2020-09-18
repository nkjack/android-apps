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
import com.studentadvisor.noam.studentadvisor.pojo.School;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Noam on 11/7/2015.
 */

public class DBDegrees {
    public static final int ALL_DEGREES = 0;
    public static final int ALL_SUBJECTS = 1;
    public static final int ALL_SCHOOLS = 2;
    private DegreesHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBDegrees(Context context) {
        Log.d("noam", "DBDregrees");
        mHelper = new DegreesHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertDegrees(int table, ArrayList<Degree> listDegrees, boolean clearPrevious) {

        if (clearPrevious) {
            deleteDegrees(table);
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + (table == ALL_DEGREES ? TABLE_ALL_DEGREES : null) + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listDegrees.size(); i++) {
            Degree currentDegree = listDegrees.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindLong(1, currentDegree.getDbid_degree());
            statement.bindString(2, currentDegree.getFaculty_name());
            statement.bindString(3, currentDegree.getDegree_name());
            statement.bindString(4, currentDegree.getSchool_name_he());
            statement.bindString(5, currentDegree.getSchool_name_en());
            statement.bindString(6, currentDegree.getSchool_url_web());
            statement.bindString(7, currentDegree.getSubject_1());
            statement.bindString(8, currentDegree.getSubject_2() == null ? "" : currentDegree.getSubject_2());
            statement.bindString(9, currentDegree.getSubject_3() == null ? "" : currentDegree.getSubject_3());
            statement.bindString(10, currentDegree.getSubject_4() == null ? "" : currentDegree.getSubject_4());
            statement.bindLong(11, currentDegree.getLikes());
            statement.bindLong(12, currentDegree.getFollowes());
            statement.bindString(13, currentDegree.getUrlLogoPic());
            statement.bindString(14, currentDegree.getUrlHeaderPic());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listDegrees.size() + " " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void insertSubjects(int table, ArrayList<Subject> listSubjects, boolean clearPrevious) {

        if (clearPrevious) {
            deleteSubjects(table);
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + (table == ALL_SUBJECTS ? TABLE_ALL_SUBJECTS : null) + " VALUES (?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listSubjects.size(); i++) {
            Subject currentSubject = listSubjects.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindLong(1, currentSubject.getDbid_subject());
            statement.bindString(2, currentSubject.getSubject_name());
            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listSubjects.size() + " " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void insertSchools(int table, ArrayList<School> listSchools, boolean clearPrevious) {

        if (clearPrevious) {
            deleteSchools(table);
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + (table == ALL_SCHOOLS ? TABLE_ALL_SCHOOLS : null) + " VALUES (?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listSchools.size(); i++) {
            School currentSchool = listSchools.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindLong(1, currentSchool.getDbid_school());
            statement.bindString(2, currentSchool.getSchool_name_he());
            statement.bindString(3, currentSchool.getSchool_name_en());
            statement.bindString(4, currentSchool.getScool_url_website());
            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listSchools.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<Degree> readDegrees(int table) {
        ArrayList<Degree> listDegrees = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {COLUMN_ID_DEGREE,
                COLUMN_FACULTY_NAME,
                COLUMN_DEGREE_NAME,
                COLUMN_SCHOOL_NAME_HE,
                COLUMN_SCHOOL_NAME_EN,
                COLUMN_URL_WEB_SCHOOL,
                COLUMN_SUBJECT_1,
                COLUMN_SUBJECT_2,
                COLUMN_SUBJECT_3,
                COLUMN_SUBJECT_4,
                COLUMN_DEGREE_LIKES,
                COLUMN_DEGREE_FOLLOWES,
                COLUMN_URL_LOGO_SCHOOL,
                COLUMN_URL_HEADER_SCHOOL

        };
        L.m(TABLE_ALL_DEGREES);
        Cursor cursor = mDatabase.query((table == ALL_DEGREES ? TABLE_ALL_DEGREES : TABLE_ALL_DEGREES), columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                Degree Degree = new Degree();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                Degree.setDbid_degree(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_DEGREE)));
                Degree.setFaculty_name(cursor.getString(cursor.getColumnIndex(COLUMN_FACULTY_NAME)));
                Degree.setDegree_name(cursor.getString(cursor.getColumnIndex(COLUMN_DEGREE_NAME)));
                Degree.setSchool_name_he(cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_HE)));
                Degree.setSchool_name_en(cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_EN)));
                Degree.setSchool_url_web(cursor.getString(cursor.getColumnIndex(COLUMN_URL_WEB_SCHOOL)));
                Degree.setSubject_1(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_1)));
                Degree.setSubject_2(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_2)));
                Degree.setSubject_3(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_3)));
                Degree.setSubject_4(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_4)));
                Degree.setLikes(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGREE_LIKES)));
                Degree.setFollowes(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGREE_FOLLOWES)));
                Degree.setUrlLogoPic(cursor.getString(cursor.getColumnIndex(COLUMN_URL_LOGO_SCHOOL)));
                Degree.setUrlHeaderPic(cursor.getString(cursor.getColumnIndex(COLUMN_URL_HEADER_SCHOOL)));

                //add the Degree to the list of Degree objects which we plan to return
                listDegrees.add(Degree);
            }
            while (cursor.moveToNext());
        }
        return listDegrees;
    }

    public ArrayList<Subject> readSubjects(int table) {
        ArrayList<Subject> listSubjects = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {COLUMN_ID_SUBJECT,
                COLUMN_SUBJECT_NAME
        };
        L.m(TABLE_ALL_SUBJECTS);
        Cursor cursor = mDatabase.query((table == ALL_SUBJECTS ? TABLE_ALL_SUBJECTS : null), columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Subject object and retrieve the data from the cursor to be stored in this Degree object
                Subject subject = new Subject();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                subject.setDbid_subject(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_SUBJECT)));
                subject.setSubject_name(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_NAME)));

                //add the Degree to the list of Degree objects which we plan to return
                listSubjects.add(subject);
            }
            while (cursor.moveToNext());
        }
        return listSubjects;
    }

    public ArrayList<String> readFilterSubject(String subjects_sql, boolean ifWhere){
        ArrayList<String> listSubjects = new ArrayList<>();

        String query = "SELECT DISTINCT " + COLUMN_SUBJECT_NAME + " " +
                        "FROM (" +
                            "  SELECT " + COLUMN_SCHOOL_NAME_HE + ", " +
                                        COLUMN_SUBJECT_1 + ", " +
                                        COLUMN_SUBJECT_2 + ", " +
                                        COLUMN_SUBJECT_3 + ", " +
                                        COLUMN_SUBJECT_4 + " " +
                            " FROM " + TABLE_ALL_DEGREES  +
                                (ifWhere ? " WHERE " + subjects_sql + "" : "") +
                            " )" +
                        "JOIN " + TABLE_ALL_SUBJECTS +
                                " ON (" + COLUMN_SUBJECT_1 + "=" + COLUMN_SUBJECT_NAME + " OR " +
                                         COLUMN_SUBJECT_2 + "=" + COLUMN_SUBJECT_NAME + " OR " +
                                         COLUMN_SUBJECT_3 + "=" + COLUMN_SUBJECT_NAME + " OR " +
                                         COLUMN_SUBJECT_4 + "=" + COLUMN_SUBJECT_NAME +
                                ") "

                 ;
        L.m(query);


        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {


                String subject = cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_NAME));

                //add the Degree to the list of Degree objects which we plan to return
                listSubjects.add(subject);
            }
            while (cursor.moveToNext());
        }
        return listSubjects;
    }

    public ArrayList<School> readSchools() {
        ArrayList<School> listSchools = new ArrayList<>();

        String query = "SELECT DISTINCT " + COLUMN_SCHOOL_NAME_HE +
                                         ", " + COLUMN_SCHOOL_NAME_EN +
                                            ", " + COLUMN_URL_WEB_SCHOOL +
                " FROM " + TABLE_ALL_DEGREES +
                " ORDER BY " + COLUMN_SCHOOL_NAME_HE + " DESC";
        L.m(query);

        Cursor cursor = mDatabase.rawQuery(query, null);
        L.m("read Schools");
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                School school = new School();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                school.setDbid_school(-1);
                school.setSchool_name_he(cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_HE)));
                school.setSchool_name_en(cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_EN)));
                school.setScool_url_website(cursor.getString(cursor.getColumnIndex(COLUMN_URL_WEB_SCHOOL)));

                //add the Degree to the list of Degree objects which we plan to return
                listSchools.add(school);
            }
            while (cursor.moveToNext());
        }
        return listSchools;
    }

    public ArrayList<String> readFilterSchools(String schools_sql, boolean ifWhere){
        ArrayList<String> listSchools = new ArrayList<>();

        String query = "SELECT DISTINCT " + COLUMN_SCHOOL_NAME_HE + " " +
                "FROM (" +
                "  SELECT " + COLUMN_SCHOOL_NAME_HE + " "+
                " FROM " + TABLE_ALL_DEGREES  +
                (ifWhere ? " WHERE " + schools_sql + "" : "") +
                " )"

                ;
        L.m(query);


        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {


                String school = cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_HE));

                //add the Degree to the list of Degree objects which we plan to return
                listSchools.add(school);
            }
            while (cursor.moveToNext());
        }
        return listSchools;
    }

    public ArrayList<Degree> readDegreesFilter(int table, String subjects_sql, boolean ifWhere){
        ArrayList<Degree> listDegrees = new ArrayList<>();

        String query = "SELECT DISTINCT * FROM " + TABLE_ALL_DEGREES +
                 (ifWhere ? " WHERE " + subjects_sql + "" : "") +
                    " ORDER BY " + COLUMN_DEGREE_LIKES + " + " + COLUMN_DEGREE_FOLLOWES + " DESC ";
        L.m(query);


        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                Degree degree = new Degree();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                degree.setDbid_degree(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_DEGREE)));
                degree.setFaculty_name(cursor.getString(cursor.getColumnIndex(COLUMN_FACULTY_NAME)));
                degree.setDegree_name(cursor.getString(cursor.getColumnIndex(COLUMN_DEGREE_NAME)));
                degree.setSchool_name_he(cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_HE)));
                degree.setSchool_name_en(cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL_NAME_EN)));
                degree.setSchool_url_web(cursor.getString(cursor.getColumnIndex(COLUMN_URL_WEB_SCHOOL)));
                degree.setSubject_1(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_1)));
                degree.setSubject_2(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_2)));
                degree.setSubject_3(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_3)));
                degree.setSubject_4(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_4)));
                degree.setLikes(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGREE_LIKES)));
                degree.setFollowes(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGREE_FOLLOWES)));
                degree.setUrlLogoPic(cursor.getString(cursor.getColumnIndex(COLUMN_URL_LOGO_SCHOOL)));
                degree.setUrlHeaderPic(cursor.getString(cursor.getColumnIndex(COLUMN_URL_HEADER_SCHOOL)));


                //add the Degree to the list of Degree objects which we plan to return
                listDegrees.add(degree);
            }
            while (cursor.moveToNext());
        }
        return listDegrees;
    }

    public void deleteDegrees(int table) {
        mDatabase.delete((table == ALL_DEGREES ? TABLE_ALL_DEGREES : null), null, null);
    }

    public void deleteSubjects(int table) {
        mDatabase.delete((table == ALL_SUBJECTS ? TABLE_ALL_SUBJECTS : null), null, null);
    }

    public void deleteSchools(int table) {
        mDatabase.delete((table == ALL_SCHOOLS ? TABLE_ALL_SCHOOLS : null), null, null);
    }


    public void degreeSetLikes(int table, int amount, String dbid_degree) {

        //create a sql prepared statement
        String sql = "UPDATE " + TABLE_ALL_DEGREES +
                " SET " + COLUMN_DEGREE_LIKES + " = " + amount +
                " WHERE " + COLUMN_ID_DEGREE + " = " + dbid_degree +
                " ;";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("update Likes "  + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void degreeSetFollowes(int table, int amount, String dbid_degree) {

        //create a sql prepared statement
        String sql = "UPDATE " + TABLE_ALL_DEGREES +
                " SET " + COLUMN_DEGREE_FOLLOWES + " = " + amount +
                " WHERE " + COLUMN_ID_DEGREE + " = " + dbid_degree +
                " ;";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();

        //set the transaction as successful and end the transaction
        L.m("update Followes "  + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    private static class DegreesHelper extends SQLiteOpenHelper {

        private static final String CREATE_TABLE_ALL_DEGREES = "CREATE TABLE " + TABLE_ALL_DEGREES + " (" +
                COLUMN_ID_DEGREE + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FACULTY_NAME + " TEXT," +
                COLUMN_DEGREE_NAME + " TEXT," +
                COLUMN_SCHOOL_NAME_HE + " TEXT," +
                COLUMN_SCHOOL_NAME_EN + " TEXT," +
                COLUMN_URL_WEB_SCHOOL + " TEXT, " +
                COLUMN_SUBJECT_1 + " TEXT," +
                COLUMN_SUBJECT_2 + " TEXT," +
                COLUMN_SUBJECT_3 + " TEXT," +
                COLUMN_SUBJECT_4 + " TEXT," +
                COLUMN_DEGREE_LIKES + " INTEGER, " +
                COLUMN_DEGREE_FOLLOWES + " INTEGER, " +
                COLUMN_URL_LOGO_SCHOOL + " TEXT, " +
                COLUMN_URL_HEADER_SCHOOL + " TEXT " +

                ");";

        private static final String CREATE_TABLE_ALL_SUBJECTS = "CREATE TABLE " + TABLE_ALL_SUBJECTS + " (" +
                COLUMN_ID_SUBJECT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_NAME + " TEXT " +
                ");";

        private static final String CREATE_TABLE_ALL_SCHOOLS = "CREATE TABLE " + TABLE_ALL_SCHOOLS + " (" +
                COLUMN_ID_SUBJECT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SCHOOL_NAME_EN + " TEXT, " +
                COLUMN_SCHOOL_NAME_HE + " TEXT, " +
                COLUMN_URL_WEB_SCHOOL + " TEXT " +
                ");";

        private static final String DB_NAME = "degrees_db";
        private static final int DB_VERSION = 2;

        private Context mContext;

        public DegreesHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_ALL_DEGREES);
                db.execSQL(CREATE_TABLE_ALL_SUBJECTS);
                db.execSQL(CREATE_TABLE_ALL_SCHOOLS);
                Log.d("noam","create table all degrees executed");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table all degrees executed");
//                db.execSQL(" DROP TABLE " + TABLE_ALL_DEGREES + " IF EXISTS;");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_DEGREES + ";");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_SUBJECTS + ";");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_SCHOOLS + ";");

                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }
}

