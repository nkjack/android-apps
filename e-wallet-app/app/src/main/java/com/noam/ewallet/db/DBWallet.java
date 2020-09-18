package com.noam.ewallet.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.noam.ewallet.logging.L;
import com.noam.ewallet.pojo.Category;
import com.noam.ewallet.pojo.Purchase;
import com.noam.ewallet.pojo.SubCategory;

import java.util.ArrayList;
import java.util.Date;

import static com.noam.ewallet.extras.Keys.DBWallet.*;

/**
 * Created by Noam on 3/29/2017.
 */
public class DBWallet {
    public static final int ALL_WALLET = 0;
    public static final int ALL_CATEGORIES = 1;
    public static final int ALL_SUB_CATEGORIES = 2;
    private WalletHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBWallet(Context context) {
        Log.d("noam", "DBWallet");
        mHelper = new WalletHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insert_purchase(int table, ArrayList<Purchase> listPurchases, boolean clearPrevious) {

        if (clearPrevious) {
            deletePurchases(table);
        }

        //create a sql prepared statemen
        String sql = "INSERT INTO " + (table == ALL_WALLET ? TABLE_NAME_WALLET : null) + " VALUES (NULL,?,?,?,?,?);";
        //compile the statement and start a transaction

        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listPurchases.size(); i++) {
            Purchase currentPurchase = listPurchases.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindLong(1, currentPurchase.getCategory_key());
            statement.bindLong(2, currentPurchase.getSub_category_key());
            statement.bindLong(3, currentPurchase.getPurchase_price());
            statement.bindLong(4, currentPurchase.getDate_time());
            statement.bindString(5, currentPurchase.getPurchase_comment());

            L.m(statement.toString());
            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listPurchases.size() + " " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void insertCategory(int table, ArrayList<Category> listCategories, boolean clearPrevious) {

        if (clearPrevious) {
            deleteCategoriess(table);
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + (table == ALL_CATEGORIES ? TABLE_NAME_CATEGORY : null) + " VALUES (NULL,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listCategories.size(); i++) {
            Category curretCategory = listCategories.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(1, curretCategory.getCategory_name());
            statement.bindLong(2, curretCategory.getCategory_budget());
            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting categories " + listCategories.size() + " " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void insertSubCategories(int table, ArrayList<SubCategory> listSubCategories, boolean clearPrevious) {

        if (clearPrevious) {
            deleteSubCategories(table);
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + (table == ALL_SUB_CATEGORIES ? TABLE_NAME_SUB_CATEGORY : null) + " VALUES (NULL,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listSubCategories.size(); i++) {
            SubCategory currentSub = listSubCategories.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(1, currentSub.getSub_category_name());
            statement.bindLong(2, currentSub.getCategory_id());
            statement.bindLong(3, currentSub.getSub_category_budget());
            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting sub categories " + listSubCategories.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public int getCategoryBudget(int dbid_category) {

        String query = "SELECT DISTINCT "+ COLUMN_CATEGORY_BUDGET +" FROM " + TABLE_NAME_CATEGORY
                + " WHERE " + COLUMN_CATEGORY_ID+ " = " + dbid_category + "";


        //String query2 = "SELECT * FROM " + TABLE_NAME_WALLET ;
        L.m(query);
        Cursor cursor = mDatabase.rawQuery(query, null);

        int budgetToReturn = 0;
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                budgetToReturn = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_BUDGET));
                //long time = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TIME));
                //int price = cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE));

                L.m("budget - " + budgetToReturn);
                //L.m("Purchase: time - " + time + "   price - " + price);
            }
            while (cursor.moveToNext());
        }
        return budgetToReturn;
    }

    public int getAllCategoryBudget(){
        String query = "SELECT DISTINCT SUM("+ COLUMN_CATEGORY_BUDGET +") as "
        + COLUMN_CATEGORY_BUDGET + " FROM " + TABLE_NAME_CATEGORY;

        //String query2 = "SELECT * FROM " + TABLE_NAME_WALLET ;
        L.m(query);
        Cursor cursor = mDatabase.rawQuery(query, null);

        int budgetToReturn = 0;
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                budgetToReturn = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_BUDGET));
                //long time = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TIME));
                //int price = cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE));

                L.m("categories budget - " + budgetToReturn);
                //L.m("Purchase: time - " + time + "   price - " + price);
            }
            while (cursor.moveToNext());
        }
        return budgetToReturn;
    }

    public void updateCategoryBudget(int dbid_category, int newBudget){
        //create a sql prepared statement
        String sql = "UPDATE " + TABLE_NAME_CATEGORY + " SET " + COLUMN_CATEGORY_BUDGET + " = "
                + newBudget +
                    " WHERE " + COLUMN_CATEGORY_ID + " = " + dbid_category +
                    " ;";

        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("delete purchase " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public int sumMonthlyExpenses(long startOfMonth, int dbid_category) {

        String query = "";
        if (!(dbid_category > 0)) {
            query = "SELECT DISTINCT SUM(" + COLUMN_PRICE + ") as all_price FROM " + TABLE_NAME_WALLET
                    + " WHERE " + COLUMN_DATE_TIME + " > " + startOfMonth + "";
        } else {
            query = "SELECT DISTINCT SUM(" + COLUMN_PRICE + ") as all_price FROM " + TABLE_NAME_WALLET
                    + " WHERE " + COLUMN_DATE_TIME + " > " + startOfMonth +
                    " AND " + COLUMN_CATEGORY_KEY + " = " + dbid_category;
        }

        //String query2 = "SELECT * FROM " + TABLE_NAME_WALLET ;
        L.m(query);
        Cursor cursor = mDatabase.rawQuery(query, null);

        int sumToReturn = 0;
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                sumToReturn = cursor.getInt(cursor.getColumnIndex("all_price"));
                //long time = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TIME));
                //int price = cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE));

                L.m("Purchase: all_price - " + sumToReturn);
                //L.m("Purchase: time - " + time + "   price - " + price);
            }
            while (cursor.moveToNext());
        }
        return sumToReturn;
    }

    public ArrayList<Purchase> readFilterPurchase(String subjects_sql, boolean ifWhere) {
        ArrayList<Purchase> listPurchase = new ArrayList<>();

        String query = "SELECT DISTINCT " +
                " w." + COLUMN_TB_WALLET_ID + ", " +
                " c." + COLUMN_CATEGORY_ID + ", " +
                " c." + COLUMN_CATEGORY_NAME + ", " +
                " s." + COLUMN_SUB_CATEGORY_ID + ", " +
                " s." + COLUMN_NAME_SUB_CAT + ", " +
                " w." + COLUMN_PRICE + ", " +
                " w." + COLUMN_DATE_TIME + ", " +
                " w." + COLUMN_COMMENT + " " +
                " FROM " + TABLE_NAME_WALLET + " w " +
                "JOIN " + TABLE_NAME_CATEGORY + " c " +
                " ON ( w." + COLUMN_CATEGORY_KEY + " = c." + COLUMN_CATEGORY_ID + " ) " +
                "JOIN " + TABLE_NAME_SUB_CATEGORY + " s " +
                " ON ( w." + COLUMN_SUB_CATEGORY_KEY + " = s." + COLUMN_SUB_CATEGORY_ID + " ) " +
                (ifWhere ? " WHERE " + subjects_sql + "" : "") +
                " ORDER BY w." + COLUMN_DATE_TIME;

        L.m(query);


        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                Purchase purchase = new Purchase();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                purchase.setDbid_purchase(cursor.getInt(cursor.getColumnIndex(COLUMN_TB_WALLET_ID)));
                purchase.setCategory_key(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                purchase.setCategory_name(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                purchase.setSub_category_key(cursor.getInt(cursor.getColumnIndex(COLUMN_SUB_CATEGORY_ID)));
                purchase.setSub_category_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUB_CAT)));
                purchase.setPurchase_price(cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)));
                purchase.setDate_time(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TIME)));
                purchase.setPurchase_comment(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)));

                L.m("purchase : " + purchase.getCategory_name() +" " +
                purchase.getSub_category_name() + " " + purchase.getCategory_key() + " "
                + purchase.getDate_time() + " " + purchase.getPurchase_price());
                L.m("------------------------");

                //add the Degree to the list of Degree objects which we plan to return
                listPurchase.add(purchase);
            }
            while (cursor.moveToNext());
        }
        return listPurchase;
    }

    public ArrayList<Category> readFilterCategory(String subjects_sql, boolean ifWhere) {
        ArrayList<Category> listCategory = new ArrayList<>();

        String query = "SELECT DISTINCT " +
                COLUMN_CATEGORY_ID + ", " +
                COLUMN_CATEGORY_NAME + ", " +
                COLUMN_CATEGORY_BUDGET + " " +
                " FROM " + TABLE_NAME_CATEGORY +
                (ifWhere ? " WHERE " + subjects_sql + "" : "") +
                " ";

        L.m(query);


        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                Category category = new Category();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                category.setDbid_category(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                category.setCategory_name(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                category.setCategory_budget(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_BUDGET)));


                //add the Degree to the list of Degree objects which we plan to return
                listCategory.add(category);
            }
            while (cursor.moveToNext());
        }
        return listCategory;
    }


    public ArrayList<SubCategory> readFilterSubCategory(String subjects_sql, boolean ifWhere) {
        ArrayList<SubCategory> listSubCategory = new ArrayList<>();

        String query = "SELECT DISTINCT " +
                "s." + COLUMN_SUB_CATEGORY_ID + ", " +
                "s." + COLUMN_NAME_SUB_CAT + ", " +
                "s." + COLUMN_CATEGORY_ID_FK + ", " +
                "s." + COLUMN_SUB_BUDGET + " " +
                " FROM " + TABLE_NAME_SUB_CATEGORY + " s " +
                " JOIN " + TABLE_NAME_CATEGORY + " c " +
                " ON " + " s." + COLUMN_CATEGORY_ID_FK + " = c." + COLUMN_CATEGORY_ID + " " +
                (ifWhere ? " WHERE c." + COLUMN_CATEGORY_ID + " = " + subjects_sql + "" : "") +
                " ";

        L.m(query);


        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Degree object and retrieve the data from the cursor to be stored in this Degree object
                SubCategory subCategory = new SubCategory();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Degree object to contain our data
                subCategory.setDbid_sub_category(cursor.getInt(cursor.getColumnIndex(COLUMN_SUB_CATEGORY_ID)));
                subCategory.setSub_category_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUB_CAT)));
                subCategory.setCategory_id(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID_FK)));
                subCategory.setSub_category_budget(cursor.getInt(cursor.getColumnIndex(COLUMN_SUB_BUDGET)));


                //add the Degree to the list of Degree objects which we plan to return
                listSubCategory.add(subCategory);
            }
            while (cursor.moveToNext());
        }
        return listSubCategory;
    }


    public void deletePurchases(int table) {
        mDatabase.delete((table == ALL_WALLET ? TABLE_NAME_WALLET : null), null, null);
    }

    public void deleteCategoriess(int table) {
        mDatabase.delete((table == ALL_CATEGORIES ? TABLE_NAME_CATEGORY : null), null, null);
    }

    public void deleteSubCategories(int table) {
        mDatabase.delete((table == ALL_SUB_CATEGORIES ? TABLE_NAME_SUB_CATEGORY : null), null, null);
    }

    public void deletePurchase(int dbid_purchase, int dbid_category, int dbid_sub_category) {
        //create a sql prepared statement
        String sql = "";
        if (dbid_category > 0) {
            sql = "DELETE FROM " + TABLE_NAME_WALLET +
                    " WHERE " + COLUMN_CATEGORY_KEY + " = " + dbid_category +
                    " ;";
        } else if (dbid_sub_category > 0) {
            sql = "DELETE FROM " + TABLE_NAME_WALLET +
                    " WHERE " + COLUMN_SUB_CATEGORY_KEY + " = " + dbid_sub_category +
                    " ;";
        } else {
            sql = "DELETE FROM " + TABLE_NAME_WALLET +
                    " WHERE " + COLUMN_TB_WALLET_ID + " = " + dbid_purchase +
                    " ;";
        }
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("delete purchase " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void deleteCategory(int dbid_category) {
        //create a sql prepared statement
        String sql = "DELETE FROM " + TABLE_NAME_CATEGORY +
                " WHERE " + COLUMN_CATEGORY_ID + " = " + dbid_category +
                " ;";

        deleteSubCategory(-1, dbid_category);
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("delete category " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void deleteSubCategory(int dbid_sub_category, int dbid_category) {
        //create a sql prepared statement
        String sql = "";
        if (dbid_category > 0) {
            sql = "DELETE FROM " + TABLE_NAME_SUB_CATEGORY +
                    " WHERE " + COLUMN_CATEGORY_ID_FK + " = " + dbid_category +
                    " ;";
        } else {
            sql = "DELETE FROM " + TABLE_NAME_SUB_CATEGORY +
                    " WHERE " + COLUMN_SUB_CATEGORY_ID + " = " + dbid_sub_category +
                    " ;";
        }
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.execute();
        //set the transaction as successful and end the transaction
        L.m("delete sub_category " + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }


    private static class WalletHelper extends SQLiteOpenHelper {

        private static final String CREATE_TABLE_WALLET = "CREATE TABLE " + TABLE_NAME_WALLET + " (" +
                COLUMN_TB_WALLET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CATEGORY_KEY + " INTEGER," +
                COLUMN_SUB_CATEGORY_KEY + " INTEGER," +
                COLUMN_PRICE + " INTEGER," +
                COLUMN_DATE_TIME + " INTEGER," +
                COLUMN_COMMENT + " TEXT " +

                ");";

        private static final String CREATE_TABLE_ALL_CATEGORIES = "CREATE TABLE " + TABLE_NAME_CATEGORY + " (" +
                COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_NAME + " TEXT ," +
                COLUMN_CATEGORY_BUDGET + " INTEGER " +
                ");";

        private static final String CREATE_TABLE_ALL_SUB_CATEGORIES = "CREATE TABLE " + TABLE_NAME_SUB_CATEGORY + " (" +
                COLUMN_SUB_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SUB_CAT + " TEXT, " +
                COLUMN_CATEGORY_ID_FK + " INTEGER, " +
                COLUMN_SUB_BUDGET + " INTEGER " +
                ");";


        /*
        private static final String INSERT_DUMMY_CATEGORIES = "INSERT INTO " + TABLE_NAME_CATEGORY +
        " ( " + COLUMN_CATEGORY_NAME  +" , " + COLUMN_CATEGORY_BUDGET + " ) " +
        " VALUES ('סופר',400),('דלק', 600), () ";
        */

        private static final String DB_NAME = DATABASE_WALLET_NAME;
        private static final int DB_VERSION = 1;

        private Context mContext;

        public WalletHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_WALLET);
                db.execSQL(CREATE_TABLE_ALL_CATEGORIES);
                db.execSQL(CREATE_TABLE_ALL_SUB_CATEGORIES);
                L.m("create table wallet executed");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table all degrees executed");
//                db.execSQL(" DROP TABLE " + TABLE_ALL_DEGREES + " IF EXISTS;");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WALLET + ";");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY + ";");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SUB_CATEGORY + ";");

                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }
}

