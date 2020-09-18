package com.studentadvisor.noam.studentadvisor.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterDegrees;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerDegreeCard;
import com.studentadvisor.noam.studentadvisor.extras.MarginDecoration;
import com.studentadvisor.noam.studentadvisor.extras.RecyclerTouchListenerDegreeCard;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskAllFollowedDegrees;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLikeDegree;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadDegrees;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_2;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_3;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_4;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.*;

import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_DEGREE_PARCEL;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_PROFILE_IMAGE_ID;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_DEGREE_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_YEAR;


import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayoutCallBack {

    Toolbar mToolBar;
    Context context;
    ImageView headerDrawer;
    private int mSelectedId;

    //the arraylist containing our list of box office his
    private ArrayList<Degree> mListDegrees = new ArrayList<>();
    //the adapter responsible for displaying our degree within a RecyclerView
    private AdapterDegrees mAdapter;
    //the recyclerview containing showing all our degrees
    private RecyclerView mRecycleDegrees;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean firstTime = true;
    private View mStatusBar;
    private Tracker mTracker;
    private static final String TAG = "MainActivity";

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String values = extras.getString(LIST_SELECTED_SUBJECTS);

                if (values.contentEquals("1")) {
                    mListDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, values, false);
                }
                mListDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, values, true);
                //if the database is empty, trigger an AsycnTask to download movie list from the web
                if (mListDegrees.isEmpty()) {
                    // need to handle it
                }

            }
            //update your Adapter to containg the retrieved movies
            mAdapter.setDegrees(mListDegrees);
            mAdapter.notifyDataSetChanged();
        }

        Log.i(TAG, "Setting screen name: " + "MainActivity");
        mTracker.setScreenName("Image~" + "Result Search Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init_typeface();
        drawerSetup();
        //default it set first item as selected
        mSelectedId=savedInstanceState ==null ? R.id.menu_item_home: savedInstanceState.getInt("SELECTED_ID");
//        itemSelection(mSelectedId);

//        headerDrawer = (ImageView) findViewById(R.id.headerPic);
//        Picasso.with(this)
//                .load("http://www.bullnshit.com/student_advisor/schools_pic/idc/header.png")
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder)
//                .into(headerDrawer);


        context = this;
        firstTime = false;

        mRecycleDegrees = (RecyclerView) findViewById(R.id.listAllDegrees);
        //set the layout manager before trying to display data
        mRecycleDegrees.addItemDecoration(new MarginDecoration(this));
//        mRecycleDegrees.setHasFixedSize(true);
//        mRecycleDegrees.setLayoutManager(new GridLayoutManager(this, 2));
        mRecycleDegrees.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AdapterDegrees(this);
        mRecycleDegrees.setAdapter(mAdapter);
        mRecycleDegrees.addOnItemTouchListener(new RecyclerTouchListenerDegreeCard(this, mRecycleDegrees, new ClickListenerDegreeCard() {
            @Override
            public void onClick(View view, int position) {
                if (mAdapter.getDegree(position) != null) {
                    Degree degree = mAdapter.getDegree(position);

                    if (CheckEnabledInternet()) {
                        Intent i = new Intent(context, DegreeActivity.class);
                        i.putExtra(SELECTED_DEGREE, degree);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onClickLike(View view, int position) {
                if (mAdapter.getDegree(position) != null) {
                    Degree degree = mAdapter.getDegree(position);

                    // SqLite database handler
                    SQLiteHandler db = new SQLiteHandler(getApplicationContext());

                    // session manager
                    SessionManager session = new SessionManager(getApplicationContext());
                    if (session.isLoggedIn()) {
                        // Fetching user details from sqlite
                        HashMap<String, String> user = db.getUserDetails();

                        String name = user.get("name");
                        String user_uid = user.get("uid");

                        L.m(name + " " + user_uid);
//                        new TaskLikeDegree(getApplicationContext(), user_uid, name).execute(degree);
                    }


                }
            }
        }));


        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            mListDegrees = savedInstanceState.getParcelableArrayList(STATE_DEGREE);
        } else {

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String values = extras.getString(LIST_SELECTED_SUBJECTS);

                if (values.contentEquals("1")) {
                    mListDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, values, false);
                }
                mListDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, values, true);
                //if the database is empty, trigger an AsycnTask to download movie list from the web
                if (mListDegrees.isEmpty()) {
                    // need to handle it
                }

            }
        }

        //update your Adapter to containg the retrieved movies
        mAdapter.setDegrees(mListDegrees);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                new TaskLoadDegrees((DegreesLoadedListener) context).execute();
            }
        });
        */
        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    /*
    @Override
    public void onAllDegreesLoaded(ArrayList<Degree> listDegrees) {
        L.m("onAllDegreesLoaded: onAllDegreesLoaded ");
        //update the Adapter to contain the new movies downloaded from the web

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mAdapter.setDegrees(listDegrees);
    }
    */
    public void setStatusBarColor(View statusBar, int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mStatusBar.setVisibility(View.GONE);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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

    private void drawerSetup(){
        mDrawer= (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        TextView titleDrawer = (TextView) findViewById(R.id.userDrawerTitle);
//        TextView username = (TextView) findViewById(R.id.username);
//        TextView email = (TextView) findViewById(R.id.email);
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);

        SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());
        // session manager
        SessionManager session = new SessionManager(MyApplication.getAppContext());
        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            String name = user.get("name");
            String mail = user.get("email");
            String id = user.get("dbid_user_pic");

            titleDrawer.setText("Hello " + name);

//            username.setText("Hello " + name);
//            email.setText(mail);
            String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id="+id;
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.mipmap.ic_person)
                    .error(R.mipmap.ic_person)
                    .into(circleImageView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_DEGREE, mListDegrees);
        outState.putInt("SELECTED_ID", mSelectedId);
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


    // ----- DRAWER ------
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedId=menuItem.getItemId();
        itemSelection(mSelectedId);
        return true;
    }

    private void itemSelection(int mSelectedId) {

        switch(mSelectedId){

            case R.id.menu_item_home:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent homeIntent = new Intent(this, NewsFeedActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                break;

            case R.id.menu_item_explore:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(this , ExploreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.menu_item_favorite:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
                startActivity(favoriteIntent);
                break;

            case R.id.menu_item_followes:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent followIntent = new Intent(this, NewsFeedActivity.class);
                followIntent.putExtra(NEWS_FEED_INITIAL_TAB, 1);
                followIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(followIntent);
                break;

            case R.id.menu_item_likes:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent likesIntent = new Intent(this, NewsFeedActivity.class);
                likesIntent.putExtra(NEWS_FEED_INITIAL_TAB, 2);
                likesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(likesIntent);
                break;

            case R.id.menu_item_globe:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent globeIntent = new Intent(this, NewsFeedActivity.class);
                globeIntent.putExtra(NEWS_FEED_INITIAL_TAB, 3);
                globeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(globeIntent);
                break;

            case R.id.menu_item_mail:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "noam.kahan@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing My Opinion");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey Noam, ");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.menu_item_settings:
                mDrawerLayout.closeDrawer(GravityCompat.START);

                User mUser = Util.checkIfLoggedIn();

                String degreesQuery = " " + COLUMN_ID_DEGREE + " IN ("
                        + mUser.getDegree_1() + ", " + mUser.getDegree_2() + ", " + mUser.getDegree_3() + ")";
                ArrayList<Degree> listDegree = new ArrayList<>();
                listDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, degreesQuery, true);

                if (!mUser.getUser_name().contentEquals("-999")) {
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    settingsIntent.putExtra(KEY_BUNDLE_USER_TYPE, mUser.getUser_type());
                    settingsIntent.putExtra(KEY_BUNDLE_USER_YEAR, mUser.getUser_year());
                    settingsIntent.putExtra(KEY_BUNDLE_PROFILE_IMAGE_ID, mUser.getUser_pic_id());
//                    settingsIntent.putExtra(KEY_BUNDLE_USER_DEGREE_1, mUser.getDegree_1());
//                    settingsIntent.putExtra(KEY_BUNDLE_USER_DEGREE_1, mUser.getDegree_2());
//                    settingsIntent.putExtra(KEY_BUNDLE_USER_DEGREE_1, mUser.getDegree_3());
                    settingsIntent.putParcelableArrayListExtra(KEY_BUNDLE_USER_DEGREE_1, listDegree);
                    startActivity(settingsIntent);
                }
                break;

            case R.id.menu_item_about:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.menu_item_logout:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (Util.ifLogedIn()) {
                    if (Util.logOut()) {
                        Intent logOutIntent = new Intent(MainActivity.this, IntroActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                }
                else{
                    Intent logOutIntent = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(logOutIntent);
                    finish();
                }
                break;

        }

    }

    @Override
    public void onGetFollowedDegrees( ArrayList<FollowObj> followedDegrees) {
        String query = Util.convertAllFollowedDegreeToSqlStatement(followedDegrees);
        L.m("--------" + query);
        if (MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, query, true).size() > 0) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(LIST_SELECTED_SUBJECTS, query);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            L.m("yes");
        } else {
//            Snackbar.make(mExploreLayout, "No Data Return", Snackbar.LENGTH_SHORT).show();
            L.m("snack");
        }

    }

    @Override
    public void onGetLikedDegrees( ArrayList<LikeObj> likedDegrees) {

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
                    CheckEnabledInternet();
                }
            });
            networkDialog.create().show();
        }
        return isConnected;
    }
}
