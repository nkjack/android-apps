package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.NEWS_FEED_INITIAL_TAB;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_PROFILE_IMAGE_ID;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_DEGREE_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_YEAR;

/**
 * Created by Noam on 1/17/2016.
 */
public class AboutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayoutCallBack {

    private int mSelectedId;
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;


    private View mStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        toolbarSetup();
        init_typeface();
        drawerSetup();
        mSelectedId = savedInstanceState == null ? R.id.menu_item_home : savedInstanceState.getInt("SELECTED_ID");

        ListView lv = (ListView) findViewById(R.id.copyrightList);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        ListView lv2 = (ListView) findViewById(R.id.infoAndCreditList);
        lv2.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


    }

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

    private void toolbarSetup() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("About");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void drawerSetup() {
        mDrawer = (NavigationView) findViewById(R.id.navigation_drawer);
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
            String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id=" + id;
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.mipmap.ic_person)
                    .error(R.mipmap.ic_person)
                    .into(circleImageView);
        }
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
        mSelectedId = menuItem.getItemId();
        itemSelection(mSelectedId);
        return true;
    }

    private void itemSelection(int mSelectedId) {

        switch (mSelectedId) {

            case R.id.menu_item_home:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent homeIntent = new Intent(this, NewsFeedActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                break;

            case R.id.menu_item_explore:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(this, ExploreActivity.class);
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
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
                break;

            case R.id.menu_item_logout:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (Util.ifLogedIn()) {
                    if (Util.logOut()) {
                        Intent logOutIntent = new Intent(AboutActivity.this, IntroActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                } else {
                    Intent logOutIntent = new Intent(AboutActivity.this, IntroActivity.class);
                    startActivity(logOutIntent);
                    finish();
                }
                break;

        }

    }

    @Override
    public void onGetFollowedDegrees(ArrayList<FollowObj> followedDegrees) {

    }

    @Override
    public void onGetLikedDegrees(ArrayList<LikeObj> likedDegrees) {

    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//            return;
//
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0)
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += view.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }
}

