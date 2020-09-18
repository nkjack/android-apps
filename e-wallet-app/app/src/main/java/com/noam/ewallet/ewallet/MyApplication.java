package com.noam.ewallet.ewallet;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.noam.ewallet.db.DBWallet;

/**
 * Created by Noam on 3/30/2017.
 */
public class MyApplication extends Application{
    private static MyApplication sInstance;
    private static DBWallet mDatabase;

    public static synchronized MyApplication getInstance() {
        return sInstance;
    }
    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static DBWallet getWritableDatabase() {
        if (mDatabase == null) {
            mDatabase = new DBWallet(getAppContext());
        }
        return mDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDatabase = new DBWallet(this);
    }

    public static void saveToPreferences(Context context, String preferenceName, int preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceName, preferenceValue);
        editor.apply();
    }

    public static int readFromPreferences(Context context, String preferenceName, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getInt(preferenceName, defaultValue);
    }

}
