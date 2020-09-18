package com.studentadvisor.noam.studentadvisor.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreeNewPostLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskInsertNewPost;

import java.util.HashMap;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_NEW_POST_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_NEW_POST_EXTRA_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_WRITE_POST_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;

public class NewPostActivity extends AppCompatActivity  implements DegreeNewPostLoadedListener{

    Toolbar mToolBar;
    private Degree mDegree;
    EditText mEditText;
    Button rateButton, askButton;
    RatingBar mRateBar;
    boolean isRating = true;
    String typePost = "rate";
    ExtraInfoDegree mExtraInfoDegree;
    ProgressDialog pDialog;
    private Tracker mTracker;
    private static final String TAG = "NewPostActivity";
    private final static String PREF_POST = "pref_post_remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("RATE");
        setSupportActionBar(mToolBar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditText = (EditText) findViewById(R.id.editTextNewPost);
        rateButton = (Button) findViewById(R.id.rateButton);
        askButton = (Button) findViewById(R.id.askButton);
        mRateBar = (RatingBar) findViewById(R.id.ratingBarNewPost);
        mRateBar.setRating(2.5F);

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRateBar.setVisibility(View.VISIBLE);
                isRating = true;
                mToolBar.setTitle("RATE");
                mEditText.setHint(R.string.post_rate_string);
            }
        });

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRateBar.setVisibility(View.GONE);
                isRating = false;
                mToolBar.setTitle("ASK");
                mEditText.setHint(R.string.post_ask_string);

                L.m(mRateBar.getRating()+"");
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDegree = extras.getParcelable(PARCEL_NEW_POST_DEGREE);
            mExtraInfoDegree = extras.getParcelable(PARCEL_NEW_POST_EXTRA_DEGREE);

            if (mDegree == null || mExtraInfoDegree == null){
                //TODO return to previous activity
                finish();
            }

            typePost = extras.getString(PARCEL_WRITE_POST_TYPE);

            if (mExtraInfoDegree.isIfPostRate()){
                rateButton.setVisibility(View.GONE);
                typePost = "ask";
            }

        }
        else{
            //TODO return to previous activity
        }

        if (typePost != null && typePost.contentEquals("rate")){
            mRateBar.setVisibility(View.VISIBLE);
            isRating = true;
            mToolBar.setTitle("RATE");
            mEditText.setHint(R.string.post_rate_string);
        }
        else{
            mRateBar.setVisibility(View.GONE);
            isRating = false;
            mToolBar.setTitle("ASK");
            mEditText.setHint(R.string.post_ask_string);

        }

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        mEditText.setText(MyApplication.readFromPreferences(this, PREF_POST, ""));
        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + "New Post Activity");
        mTracker.setScreenName("Image~" + "New Post Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_post, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_post:
                if (mEditText.getText() != null && !mEditText.getText().toString().contentEquals("")){

                    // SqLite database handler
                    SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                    // session manager
                    SessionManager session = new SessionManager(MyApplication.getAppContext());
                    if (session.isLoggedIn()) {
//                        Toast.makeText(NewPostActivity.this, "POST", Toast.LENGTH_SHORT).show();

                        // Fetching user details from sqlite
                        HashMap<String, String> user = db.getUserDetails();

                        String name = user.get("name");
                        String user_uid = user.get("uid");

                        String body = mEditText.getText().toString();
                        String typePost = isRating ? "rate" : "qa";

                        String rate = String.valueOf((int) mRateBar.getRating());
                        rate = isRating ? rate : "-1";

                        new TaskInsertNewPost(this, user_uid,name, body,rate,typePost).execute(mDegree);
                        pDialog.setMessage("Posting Comment ...");
                        showDialog();

                    }
                    else{
                        // Log in Dialog
//                        Toast.makeText(this, "Need to Log In..", Toast.LENGTH_SHORT).show();
                        dialog_log_in();
                    }

                }else{
                    // Log in Dialog
                    Toast.makeText(this, "Empty Text..", Toast.LENGTH_SHORT).show();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void dialog_log_in() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You need to Log In or Register. Do you want to continue?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(NewPostActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(NewsFeedActivity.this, "You clicked no button", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onInsertNewPostListener(int success) {
        hideDialog();
        if (success == 1){
            MyApplication.saveToPreferences(this, PREF_POST, "");
            Intent i = new Intent(this, DegreeActivity.class);
            i.putExtra(SELECTED_DEGREE, mDegree);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

            finish();
        }
        else{
            Toast.makeText(this,"Error Posting..", Toast.LENGTH_LONG).show();
            MyApplication.saveToPreferences(this, PREF_POST, mEditText.getText().toString());
            finish();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!MyApplication.readFromPreferences(this, PREF_POST, "").contentEquals("")){
            MyApplication.saveToPreferences(this, PREF_POST, mEditText.getText().toString());
        }
    }
}
