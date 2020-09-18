package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.Keys;
import com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Noam on 11/15/2015.
 */
public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button btnContinue;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.fragment_1_txt_color));

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //Do what you need for this SDK
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(this.getResources().getColor(R.color.login_dark));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
        */

        init_typeface();
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnContinue = (Button) findViewById(R.id.btnContinue);

        String emailFromPref = MyApplication.readFromPreferences(getApplicationContext(), Keys.SharedPreferencesKeys.KEY_PREFERENCES_EMAIL,"");
        inputEmail.setText(emailFromPref);
        if (!emailFromPref.contentEquals(""))
        {
            inputPassword.requestFocus();
        }

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {

            HashMap<String, String> user = db.getUserDetails();

            L.m(user.toString());
            String seen_intro = user.get("seen_intro");
             //User is already logged in. Take him to main activity
            if (seen_intro == null){

            }
            else if (seen_intro.contentEquals("no")) {
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
            else if (seen_intro.contentEquals("yes")){
                Intent intent = new Intent(LoginActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                //Toast.makeText(LoginActivity.this, "Problem Loging In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                finish();
            }
        }

        //Continue button Click Event
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                session.setLogin(false);
//                db.deleteUsers();
                Util.logOut();
                startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
            }
        });

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    if (Util.isValidEmail(email)) {
                        if (CheckEnabledInternet()) {
                            checkLogin(email, password);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Check Your Email",Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                //finish();
            }
        });

    }

    public void setStatusBarColor(View statusBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
//            int actionBarHeight = getActionBarHeight();
            int actionBarHeight = 0;
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                UrlEndpoints.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        String seen_intro = user.getString("seen_intro");
                        int dbid_user_pic = user.getInt("dbid_user_pic");
                        String user_type = user.getString("user_type");
                        int user_year = user.getInt("user_year");
                        int user_degree_1 = user.getInt("user_degree_1");
                        int user_degree_2 = user.getInt("user_degree_2");
                        int user_degree_3 = user.getInt("user_degree_3");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, seen_intro, dbid_user_pic, user_type
                                    , user_year, user_degree_1, user_degree_2, user_degree_3);

                        L.m("name - " + name);
                        L.m("email - " + email);
                        L.m("created_at - " + created_at);
                        L.m("seen_intro - " + seen_intro);
                        L.m("dbid_user_pic - " + dbid_user_pic);
                        L.m("user_type - " + user_type);
                        L.m("user_year - " + user_year);
                        L.m("user_degree_1 - " + user_degree_1);
                        L.m("user_degree_2 - " + user_degree_2);
                        L.m("user_degree_3 - " + user_degree_3);

//                        Intent intent = new Intent(LoginActivity.this,
//                                WelcomeActivity.class);
//                        startActivity(intent);
//                        finish();

//                         Launch main activity
                        if (seen_intro.contentEquals("no")) {
                            Intent intent = new Intent(LoginActivity.this,
                                    WelcomeActivity.class);
                            startActivity(intent);
                            MyApplication.saveToPreferences(getApplicationContext(), Keys.SharedPreferencesKeys.KEY_PREFERENCES_EMAIL, email);
//                            finish();
                        }
                        else{
                            Intent intent = new Intent(LoginActivity.this,
                                    NewsFeedActivity.class);
                            startActivity(intent);
                            finish();
                            MyApplication.saveToPreferences(getApplicationContext(), Keys.SharedPreferencesKeys.KEY_PREFERENCES_EMAIL, email);

                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        L.m(errorMsg);
//                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Error Login In.. Try Again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Error Login In.. Try Again", Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Error Login In.. Try Again", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void init_typeface() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("Alef-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public boolean CheckEnabledInternet() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[]  networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo networkInfo : networkInfos) {
            if(networkInfo.getState()== NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }
        if(!isConnected) {
            AlertDialog.Builder networkDialog = new AlertDialog.Builder(this);
            networkDialog.setTitle("Not Connected");
            networkDialog.setMessage("Please connect to internet to proceed");
            networkDialog.setCancelable(false);
            networkDialog.setPositiveButton("Okay!",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    CheckEnabledInternet();
                }
            });
            networkDialog.create().show();
        }

        return isConnected;
    }
}