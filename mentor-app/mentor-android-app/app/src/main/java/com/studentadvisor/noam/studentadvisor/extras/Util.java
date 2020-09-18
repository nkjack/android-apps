package com.studentadvisor.noam.studentadvisor.extras;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.studentadvisor.noam.studentadvisor.activities.LoginActivity;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_2;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_3;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_4;

/**
 * Created by Noam on 11/7/2015.
 */
public class Util {
    public static final String PREFS_NAME = "TAKEOFFANDROID";
    public static final String KEY_COUNTRIES = "DegreesSearch";
    public static final int REQUEST_CODE = 1234;

    public static boolean isLollipopOrGreater() {
        return Build.VERSION.SDK_INT >= 21 ? true : false;
    }
    public static boolean isJellyBeanOrGreater(){
        return Build.VERSION.SDK_INT>=16?true:false;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean isConnectedNetwork (Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo () != null && cm.getActiveNetworkInfo ().isConnectedOrConnecting ();

    }

    public static User checkIfLoggedIn(){
        User userProfile = new User(false, "-999","-999",-1,"guest",-1,-1,-1,-1);

        // SqLite database handler
        SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());
        // session manager
        SessionManager session = new SessionManager(MyApplication.getAppContext());
        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            String name = user.get("name");
            String user_uid = user.get("uid");
            String user_pic_id = user.get("dbid_user_pic");
            String user_type = user.get("user_type");
            String user_year = user.get("user_year");
            String user_degree_1 = user.get("user_degree_1");
            String user_degree_2 = user.get("user_degree_2");
            String user_degree_3 = user.get("user_degree_3");

            L.m(name + " " + user_uid);
            userProfile.setLoggedIn(true);
            userProfile.setUser_id(user_uid);
            userProfile.setUser_name(name);
            userProfile.setUser_pic_id(Integer.valueOf(user_pic_id));
            userProfile.setUser_type(user_type);
            userProfile.setUser_year(Integer.valueOf(user_year));
            userProfile.setDegree_1(Integer.valueOf(user_degree_1));
            userProfile.setDegree_2(Integer.valueOf(user_degree_2));
            userProfile.setDegree_3(Integer.valueOf(user_degree_3));
        }
        else{
            // Log in Dialog
            //Toast.makeText(MyApplication.getAppContext(), "Need to Log In..", Toast.LENGTH_SHORT).show();
        }

        return userProfile;
    }

    public static boolean logOut(){
        boolean ifLogedOut = false;
        // SqLite database handler
        SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

        // session manager
        SessionManager session = new SessionManager(MyApplication.getAppContext());

        if (session.isLoggedIn()) {
            session.setLogin(false);

            db.deleteUsers();

            ifLogedOut = true;
        }

        return ifLogedOut;
    }

    public static boolean ifLogedIn(){
        boolean bool = false;
        // session manager
        SessionManager session = new SessionManager(MyApplication.getAppContext());

        if (session.isLoggedIn()) {
            bool = true;
        }

        return bool;
    }

    public static String convertAllFollowedDegreeToSqlStatement(ArrayList<FollowObj> followedDegrees){
        String subQuery = " " + COLUMN_ID_DEGREE + " IN (";

        if (followedDegrees.size() > 0) {
            for (int i = 0; i < followedDegrees.size(); i++) {
                if (i == 0 && followedDegrees.size() != 1) {
                    subQuery +=  "'" + followedDegrees.get(i).getDbid_degree() + "', ";
                }
                else if (i == followedDegrees.size() - 1) {
                    subQuery +=  " '" + followedDegrees.get(i).getDbid_degree() + "'  ";
                }
                else {
                    subQuery +=  " '" + followedDegrees.get(i).getDbid_degree() + "', ";
                }
            }
        }

        subQuery += ")";
        return  subQuery;
    }


    public static String convertAllLikedDegreeToSqlStatement(ArrayList<LikeObj> likedDegrees){
        String subQuery = " " + COLUMN_ID_DEGREE + " IN (";

        if (likedDegrees.size() > 0) {
            for (int i = 0; i < likedDegrees.size(); i++) {
                if (i == 0 && likedDegrees.size() != 1) {
                    subQuery +=  "'" + likedDegrees.get(i).getDbid_degree() + "', ";
                }
                else if (i == likedDegrees.size() - 1) {
                    subQuery +=  " '" + likedDegrees.get(i).getDbid_degree() + "'  ";
                }
                else {
                    subQuery +=  " '" + likedDegrees.get(i).getDbid_degree() + "', ";
                }
            }
        }

        subQuery += ")";
        return  subQuery;
    }

    public static boolean isValidEmail(String target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}
