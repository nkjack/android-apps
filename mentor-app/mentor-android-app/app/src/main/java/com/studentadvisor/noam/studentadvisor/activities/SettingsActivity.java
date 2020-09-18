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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.adapters.SearchAdapter;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.SharedPreference;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.fragments.ConfirmWelcomeDialogFragment;
import com.studentadvisor.noam.studentadvisor.fragments.WelcomeFragment3Picture;
import com.studentadvisor.noam.studentadvisor.fragments.WelcomeFragmentSt2;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.RequestHandler;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.School;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskInsertUsersExtraInfo;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadDegrees;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.LIST_SELECTED_SUBJECTS;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_DEGREE_NAME;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SCHOOL_NAME_HE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.NEWS_FEED_INITIAL_TAB;

/**
 * Created by Noam on 1/8/2016.
 */
public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayoutCallBack, View.OnClickListener, DegreesLoadedListener, WelcomeScreenDialogCommunicator {

    private int PICK_IMAGE_REQUEST = 1;
    public static final String UPLOAD_URL = "http://bullnshit.com/student_advisor/db_scripts/upload_image.php";
    public static final String UPLOAD_KEY = "image";
    public static final String UPLOAD_KEY_UNIQUE_ID = "user_unique_id";
    public static final String TAG = "MY MESSAGE";
    private static final String STATE_DEGREE_WELCOME = "state_degree_welcome";

    private static final String STATE_USER_TYPE = "state_user_type";
    private static final String STATE_USER_YEAR = "state_user_year";
    private static final String STATE_IF_BABY = "state_id_baby";
    private static final String STATE_DEGREE_1 = "state_degree_1";
    private static final String STATE_DEGREE_2 = "state_degree_2";
    private static final String STATE_DEGREE_3 = "state_degree_3";
    private static final String STATE_PROFILE_IMAGE_ID = "state_profile_image_id";


    private String userName;
    private String unique_id;
    private Uri filePath;

    private String yearStudying = "שנה א";

    private int mSelectedId;
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    private boolean ifBaby = false;
    private int user_year;
    private String user_type;
    private Bitmap profileImageBitmap;
    private int profileImageID;

    Button choosePicButton, uploadPicButton, addDegreeButton, finishButton;
    ImageButton dltDegree1Btn, dltDegree2Btn, dltDegree3Btn;
    ImageView profileImage;
    ImageButton babyImage, studentImage;
    TextView degreeTV1, degreeTV2, degreeTV3, profileNameTV;
    RadioGroup mRadioGroup;

    private SQLiteHandler db;
    private SessionManager session;
    private ArrayList<String> mDegrees;
    private ArrayList<Degree> mListDegrees = new ArrayList<>();
    private Degree degree1, degree2, degree3;

    private View mStatusBar;
    private Tracker mTracker;
    private static final String TAG_TRACKER = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                user_type = extras.getString(KEY_BUNDLE_USER_TYPE);
                user_year = extras.getInt(KEY_BUNDLE_USER_YEAR);
                profileImageID = extras.getInt(KEY_BUNDLE_PROFILE_IMAGE_ID);

                ArrayList<Degree> listDegree = extras.getParcelableArrayList(KEY_BUNDLE_USER_DEGREE_1);

                if (listDegree!= null){
                    if (!listDegree.isEmpty()) {
                        int length = listDegree.size();
                        if (0 < length) {
                            if (listDegree.get(0) != null) {
                                degree1 = listDegree.get(0);
                            }
                        }
                        if (1 < length) {
                            if (listDegree.get(1) != null) {
                                degree1 = listDegree.get(1);
                            }
                        }
                        if (2 < length) {
                            if (listDegree.get(2) != null) {
                                degree1 = listDegree.get(2);
                            }
                        }
                    }
                }
//                degree1 = extras.getParcelable(KEY_BUNDLE_USER_DEGREE_1);
//                degree2 = extras.getParcelable(KEY_BUNDLE_USER_DEGREE_2);
//                degree3 = extras.getParcelable(KEY_BUNDLE_USER_DEGREE_3);

                if (user_type.contentEquals("baby")){ifBaby = true;}
                else{ifBaby = false;}

                if (user_type == null || profileImageID == 0){
                    finish();
                }

                L.m("settings user");
                L.m("user_type - " + user_type);
                L.m("user_year - " + user_year);
                L.m("profileImageID - " + profileImageID);
            }
            else{
                finish();
            }
        }
        cardentialSetup();
        toolbarSetup();
        init_typeface();
        drawerSetup();
        mSelectedId = savedInstanceState == null ? R.id.menu_item_home : savedInstanceState.getInt("SELECTED_ID");

        finishButton = (Button) findViewById(R.id.finishSettingsButton);
        choosePicButton = (Button) findViewById(R.id.buttonChoose);
        uploadPicButton = (Button) findViewById(R.id.buttonUpload);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        degreeTV1 = (TextView) findViewById(R.id.degreeTv1);
        degreeTV2 = (TextView) findViewById(R.id.degreeTv2);
        degreeTV3 = (TextView) findViewById(R.id.degreeTv3);
        addDegreeButton = (Button) findViewById(R.id.addBtn);
        dltDegree1Btn = (ImageButton) findViewById(R.id.dltBtn1);
        dltDegree2Btn = (ImageButton) findViewById(R.id.dltBtn2);
        dltDegree3Btn = (ImageButton) findViewById(R.id.dltBtn3);
        mRadioGroup = (RadioGroup) findViewById(R.id.RGYear);
        babyImage = (ImageButton) findViewById(R.id.imageBaby);
        studentImage = (ImageButton) findViewById(R.id.imageStudent);
        profileNameTV = (TextView) findViewById(R.id.textViewProfileName);

        finishButton.setOnClickListener(this);
        choosePicButton.setOnClickListener(this);
        uploadPicButton.setOnClickListener(this);
        dltDegree1Btn.setOnClickListener(this);
        dltDegree2Btn.setOnClickListener(this);
        dltDegree3Btn.setOnClickListener(this);
        addDegreeButton.setOnClickListener(this);
        babyImage.setOnClickListener(this);
        studentImage.setOnClickListener(this);

        setRadioGroup();
        checkSaveInstanceDegrees(savedInstanceState);

        if (ifBaby) {
            if (!babyImage.isPressed()) {
                babyImage.setPressed(true);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    babyImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_clicked));
                    studentImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_normal));
                } else {
                    babyImage.setBackground(getResources().getDrawable(R.drawable.border_button_clicked));
                    studentImage.setBackground(getResources().getDrawable(R.drawable.border_button_normal));

                }

                studentImage.setPadding(16,16,16,16);
                babyImage.setPadding(16, 16, 16, 16);
            }
        }
        else{
            if (!studentImage.isPressed()) {
                studentImage.setPressed(true);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    studentImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_clicked));
                    babyImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_normal));
                } else {
                    studentImage.setBackground(getResources().getDrawable(R.drawable.border_button_clicked));
                    babyImage.setBackground(getResources().getDrawable(R.drawable.border_button_normal));
                }
                studentImage.setPadding(16,16,16,16);
                babyImage.setPadding(16, 16, 16, 16);
            }
        }

        String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id=" + profileImageID;
        Picasso.with(this)
                .load(url)
                .placeholder(R.mipmap.ic_person)
                .error(R.mipmap.ic_person)
                .into(profileImage);


        L.m(ifBaby + " baby");
        if (!ifBaby){
            switch (user_year){
                case 1:
                    mRadioGroup.check(R.id.firstYearButton);
                    break;
                case 2:
                    mRadioGroup.check(R.id.secondYearButton);
                    break;
                case 3:
                    mRadioGroup.check(R.id.thirdYearButton);
                    break;
                case 4:
                    mRadioGroup.check(R.id.fourthYearButton);
                    break;
                case 5:
                    mRadioGroup.check(R.id.fifthYearButton);
                    break;
                case 6:
                    mRadioGroup.check(R.id.sixthYearButton);
                    break;
                case 7:
                    mRadioGroup.check(R.id.seventhYearButton);
                    break;
                case 99:
                    mRadioGroup.check(R.id.enoughtYearButton);
                    break;
            }
        }

        if (degree1 != null){
            degreeTV1.setText(degree1.getDegree_name() +"ב " + degree1.getSchool_name_he());
        }
        if (degree2 != null){
            degreeTV2.setText(degree2.getDegree_name() +"ב " + degree2.getSchool_name_he());
        }
        if (degree3 != null){
            degreeTV3.setText(degree3.getDegree_name() +"ב " + degree3.getSchool_name_he());
        }

        profileNameTV.setText(userName);

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + "Setting Activity");
        mTracker.setScreenName("Image~" + "Setting Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void checkSaveInstanceDegrees(Bundle savedInstanceState) {
        if ((savedInstanceState != null) ) {
            if (savedInstanceState.getParcelableArray(STATE_DEGREE_WELCOME) != null) {
                mListDegrees = savedInstanceState.getParcelableArrayList(STATE_DEGREE_WELCOME);
            }
            if (savedInstanceState.getString(STATE_USER_TYPE) != null) {
                user_type = savedInstanceState.getString(STATE_USER_TYPE);
            }

            if (savedInstanceState.getInt(STATE_PROFILE_IMAGE_ID) != 0){
                profileImageID = savedInstanceState.getInt(STATE_PROFILE_IMAGE_ID);
            }

            ifBaby = savedInstanceState.getBoolean(STATE_IF_BABY);

            if (savedInstanceState.getInt(STATE_USER_YEAR) != 0) {
                user_year = savedInstanceState.getInt(STATE_USER_YEAR);
            }
            if (savedInstanceState.getParcelable(STATE_DEGREE_1) != null) {
                degree1 = savedInstanceState.getParcelable(STATE_DEGREE_1);
            }
            if (savedInstanceState.getParcelable(STATE_DEGREE_2) != null) {
                degree2 = savedInstanceState.getParcelable(STATE_DEGREE_2);
            }
            if (savedInstanceState.getParcelable(STATE_DEGREE_3) != null) {
                degree3 = savedInstanceState.getParcelable(STATE_DEGREE_3);
            }

            if (user_type == null || user_year == 0){
                finish();
            }

        } else {
            mListDegrees = MyApplication.getWritableDatabase().readDegrees(DBDegrees.ALL_DEGREES);

            //DEGREES
            if (mListDegrees.isEmpty()) {
                L.m("ExploreActivity: executing TaskLoadDegrees");
                new TaskLoadDegrees(this).execute();
            } else {

            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_DEGREE_WELCOME, mListDegrees);
        outState.putBoolean(STATE_IF_BABY, ifBaby);
        outState.putString(STATE_USER_TYPE, user_type);
        outState.putInt(STATE_USER_YEAR, user_year);
        outState.putInt(STATE_PROFILE_IMAGE_ID, profileImageID);

        if (degree1 != null) {
            outState.putParcelable(STATE_DEGREE_1, degree1);
        }
        if (degree2 != null) {
            outState.putParcelable(STATE_DEGREE_2, degree1);
        }
        if (degree3 != null) {
            outState.putParcelable(STATE_DEGREE_3, degree1);
        }
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


    private void cardentialSetup(){
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            finish();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        userName = name;
        unique_id = user.get("uid");
    }

    private void toolbarSetup() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("Settings");
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

    @Override
    public void onGetFollowedDegrees(ArrayList<FollowObj> followedDegrees) {

    }

    @Override
    public void onGetLikedDegrees(ArrayList<LikeObj> likedDegrees) {

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
                    Toast.makeText(SettingsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menu_item_settings:
                mDrawerLayout.closeDrawer(GravityCompat.START);
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
                        Intent logOutIntent = new Intent(SettingsActivity.this, IntroActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                } else {
                    Intent logOutIntent = new Intent(SettingsActivity.this, IntroActivity.class);
                    startActivity(logOutIntent);
                    finish();
                }
                break;

        }

    }

    @Override
    public void onClick(View v) {
        if (v == finishButton){
            finishButton();
        }
        if (v == choosePicButton) {
//            showFileChooser();
            showFileChooser();
        }
        if (v == uploadPicButton) {
//            uploadImage();
            if (profileImageBitmap != null) {
                uploadImage();
            }
            else{
                Toast.makeText(SettingsActivity.this, "Please, Choose a Picture", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == addDegreeButton) {
//            showFileChooser();
            loadToolBarSearch();
        }
        if (v == dltDegree1Btn) {
//            uploadImage();
            deleteDegreeStudying(1);
        }
        if (v == dltDegree2Btn) {
//            showFileChooser();
            deleteDegreeStudying(2);
        }
        if (v == dltDegree3Btn) {
//            uploadImage();
            deleteDegreeStudying(3);
        }
        if (v == babyImage){
            ifBaby = true;

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                babyImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_clicked));
                studentImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_normal));
                user_type = "baby";
            } else {
                babyImage.setBackground(getResources().getDrawable(R.drawable.border_button_clicked));
                studentImage.setBackground(getResources().getDrawable(R.drawable.border_button_normal));
                user_type = "baby";
            }

            studentImage.setPadding(16,16,16,16);
            babyImage.setPadding(16, 16, 16, 16);
        }
        if (v == studentImage){
            ifBaby = false;

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                studentImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_clicked));
                babyImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_normal));
                user_type = "student";
            } else {
                studentImage.setBackground(getResources().getDrawable(R.drawable.border_button_clicked));
                babyImage.setBackground(getResources().getDrawable(R.drawable.border_button_normal));
                user_type = "student";
            }
            studentImage.setPadding(16,16,16,16);
            babyImage.setPadding(16,16,16,16);
        }
    }

    private void setRadioGroup() {
        /* Attach CheckedChangeListener to radio group */
        mRadioGroup.check(R.id.firstYearButton);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
//                    Toast.makeText(SettingsActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    yearStudying = rb.getText().toString();
                    int year = 1;
                    switch (yearStudying) {
                        case "שנה א":
                            year = 1;
                            break;
                        case "שנה ב":
                            year = 2;
                            break;
                        case "שנה ג":
                            year = 3;
                            break;
                        case "שנה ד":
                            year = 4;
                            break;
                        case "שנה ה":
                            year = 5;
                            break;
                        case "שנה ו":
                            year = 6;
                            break;
                        case "שנה ז":
                            year = 7;
                            break;
                        case "דיי כבר":
                            year = 99;
                            break;
                    }
                    setYearsStudying(year);
                }

            }
        });
    }

    public void setTextView(Degree degreeName) {
        if (degreeTV1.getText().toString().contentEquals("תואר להוספה")) {
            degreeTV1.setText(degreeName.getDegree_name() + " " + "ב"+ degreeName.getSchool_name_he());
            setDegreeStudying(1, degreeName);
        } else if (degreeTV2.getText().toString().contentEquals("תואר להוספה")) {
            degreeTV2.setText(degreeName.getDegree_name() + " " + "ב"+ degreeName.getSchool_name_he());
            setDegreeStudying(2, degreeName);
        } else if (degreeTV3.getText().toString().contentEquals("תואר להוספה")) {
            degreeTV3.setText(degreeName.getDegree_name() + " " + "ב"+degreeName.getSchool_name_he());
            setDegreeStudying(3, degreeName);
        } else {

        }
    }

    @Override
    public void continueToNext(int fragmentNum) {

    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImageBitmap = cropToSquare(profileImageBitmap);
                profileImageBitmap = Bitmap.createScaledBitmap(profileImageBitmap, 200, 200, true);

                profileImage.setImageBitmap(profileImageBitmap);
//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    public void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SettingsActivity.this, "Uploading Image", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.length() != 0) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                if (unique_id != null) {
                    data.put(UPLOAD_KEY_UNIQUE_ID, unique_id);
                    L.m("YES - unique id - " + unique_id);
                } else {
                    L.m("NO - unique id - " + unique_id);
                }

                String result = rh.sendPostRequest(UPLOAD_URL, data);

                return result;
            }
        }

        if (CheckEnabledInternet()) {
            UploadImage ui = new UploadImage();
            if (unique_id != null) {
                ui.execute(profileImageBitmap);
            } else {
                Toast.makeText(SettingsActivity.this, "problem with log in credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private boolean CheckEnabledInternet() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }
        if (!isConnected) {
            AlertDialog.Builder networkDialog = new AlertDialog.Builder(this);
            networkDialog.setTitle("Not Connected");
            networkDialog.setMessage("Please connect to internet to proceed");
            networkDialog.setCancelable(false);
            networkDialog.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    CheckEnabledInternet();
                }
            });
            networkDialog.create().show();
        }

        return isConnected;
    }

    public void loadToolBarSearch() {
        if (mListDegrees != null && !mListDegrees.isEmpty()) {
            if (!ifBaby) {

                ArrayList<String> degreeStored = SharedPreference.loadList(SettingsActivity.this, Util.PREFS_NAME, Util.KEY_COUNTRIES);

                View view = SettingsActivity.this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
                LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
                ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
                final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
                ImageView searchImg = (ImageView) view.findViewById(R.id.img_search);
                final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
                final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

                Util.setListViewHeightBasedOnChildren(listSearch);

                edtToolSearch.setHint("חפש תואר");

                final Dialog toolbarSearchDialog = new Dialog(SettingsActivity.this, R.style.MaterialSearch);
                toolbarSearchDialog.setContentView(view);
                toolbarSearchDialog.setCancelable(true);
                toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
                toolbarSearchDialog.show();

//        toolbarSearchDialog.setCancelable(true);
                toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                degreeStored = (degreeStored != null && degreeStored.size() > 0) ? degreeStored : new ArrayList<String>();
                final SearchAdapter searchAdapter = new SearchAdapter(SettingsActivity.this, degreeStored, false);

                listSearch.setVisibility(View.VISIBLE);
                listSearch.setAdapter(searchAdapter);

                parentToolbarSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbarSearchDialog.cancel();

                    }

                });
                listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                        String country = String.valueOf(adapterView.getItemAtPosition(position));
                        SharedPreference.addList(SettingsActivity.this, Util.PREFS_NAME, Util.KEY_COUNTRIES, country);
                        edtToolSearch.setText(country);
                        listSearch.setVisibility(View.GONE);

                    }
                });

                edtToolSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            //do something
                            if (!edtToolSearch.getText().toString().contentEquals("")) {
                                List<String> tempDegreeSearch = new ArrayList<String>();
                                tempDegreeSearch.add(edtToolSearch.getText().toString());

                                String searchString = edtToolSearch.getText().toString();
                                String degree = "";
                                String school = "";
                                int lastPsik = searchString.lastIndexOf(",");
                                if (searchString.contains(",")) {
                                    degree = searchString.substring(0, lastPsik);
                                    school = searchString.substring(lastPsik + 2);
                                }


                                String searchQuery = " " + COLUMN_DEGREE_NAME + " = '" + degree +
                                        "' AND " + COLUMN_SCHOOL_NAME_HE + " = '" + school + "' ";


                                List<Degree> tempListDegree = new ArrayList<Degree>();
                                tempListDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, searchQuery, true);

                                if (tempListDegree.size() == 1) {
//                             tempListDegree.get(0)

                                    setTextView(tempListDegree.get(0));
                                    toolbarSearchDialog.dismiss();
                                } else if (tempListDegree.size() > 1) {
                                    boolean if_ok = true;
                                    String tempString = tempListDegree.get(0).getDegree_name() + ", " + tempListDegree.get(0).getSchool_name_he();
                                    for (Degree tempDegree : tempListDegree) {
                                        String checkStr = tempDegree.getDegree_name() + ", " + tempDegree.getSchool_name_he();
                                        if (!checkStr.contentEquals(tempString)) {
                                            if_ok = false;
                                        }
                                    }

                                    if (if_ok) {
                                        setTextView(tempListDegree.get(0));
                                        toolbarSearchDialog.dismiss();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Too Many Results, be more specific", Toast.LENGTH_LONG).show();
                                        edtToolSearch.setText("");
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                                    edtToolSearch.setText("");
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Search your Degree..", Toast.LENGTH_LONG).show();
                            }
                        }
                        return false;
                    }
                });

                edtToolSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                String[] country = SettingsActivity.this.getResources().getStringArray(R.array.countries_array);
                        mDegrees = new ArrayList<String>();
                        for (Degree degree : mListDegrees) {
                            mDegrees.add(degree.getDegree_name() + ", " + degree.getSchool_name_he());
                        }

                        listSearch.setVisibility(View.VISIBLE);
                        searchAdapter.updateList(mDegrees, true);


                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ArrayList<String> filterList = new ArrayList<String>();
                        boolean isNodata = false;
                        if (s.length() > 0) {
                            for (int i = 0; i < mDegrees.size(); i++) {


                                if (mDegrees.get(i).toLowerCase().contains(s.toString().trim().toLowerCase())) {

                                    filterList.add(mDegrees.get(i));

                                    listSearch.setVisibility(View.VISIBLE);
                                    searchAdapter.updateList(filterList, true);
                                    isNodata = true;
                                }
                            }
                            if (!isNodata) {
                                listSearch.setVisibility(View.GONE);
                                txtEmpty.setVisibility(View.VISIBLE);
                                txtEmpty.setText("No data found");
                            }
                        } else {
                            listSearch.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                imgToolBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toolbarSearchDialog.dismiss();
                    }
                });

                searchImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!edtToolSearch.getText().toString().contentEquals("")) {
                            List<String> tempDegreeSearch = new ArrayList<String>();
                            tempDegreeSearch.add(edtToolSearch.getText().toString());

                            String searchString = edtToolSearch.getText().toString();
                            String degree = "";
                            String school = "";
                            int lastPsik = searchString.lastIndexOf(",");
                            if (searchString.contains(",")) {
                                degree = searchString.substring(0, lastPsik);
                                school = searchString.substring(lastPsik + 2);
                            }


                            String searchQuery = " " + COLUMN_DEGREE_NAME + " = '" + degree +
                                    "' AND " + COLUMN_SCHOOL_NAME_HE + " = '" + school + "' ";


                            List<Degree> tempListDegree = new ArrayList<Degree>();
                            tempListDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, searchQuery, true);

                            if (tempListDegree.size() == 1) {
//                             tempListDegree.get(0)

                                setTextView(tempListDegree.get(0));
                                toolbarSearchDialog.dismiss();
                            } else if (tempListDegree.size() > 1) {
                                boolean if_ok = true;
                                String tempString = tempListDegree.get(0).getDegree_name() + ", " + tempListDegree.get(0).getSchool_name_he();
                                for (Degree tempDegree : tempListDegree) {
                                    String checkStr = tempDegree.getDegree_name() + ", " + tempDegree.getSchool_name_he();
                                    if (!checkStr.contentEquals(tempString)) {
                                        if_ok = false;
                                    }
                                }

                                if (if_ok) {
                                    setTextView(tempListDegree.get(0));
                                    toolbarSearchDialog.dismiss();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Too Many Results, be more specific", Toast.LENGTH_LONG).show();
                                    edtToolSearch.setText("");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                                edtToolSearch.setText("");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Search your Degree..", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            } else {
                Toast toast = Toast.makeText(SettingsActivity.this, "Sorry, U Told Us U Are Not A Student", Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        }

    }

    @Override
    public void setIfBaby(boolean bool) {

    }

    public void setYearsStudying(int years) {
        user_year = years;
    }

    public void setDegreeStudying(int num, Degree degree) {
        switch (num) {
            case 1:
                degree1 = degree;
                break;
            case 2:
                degree2 = degree;
                break;
            case 3:
                degree3 = degree;
                break;
        }
    }

    public void deleteDegreeStudying(int num) {
        switch (num) {
            case 1:
                degree1 = null;
                degreeTV1.setText("תואר להוספה");
                break;
            case 2:
                degree2 = null;
                degreeTV2.setText("תואר להוספה");
                break;
            case 3:
                degree3 = null;
                degreeTV3.setText("תואר להוספה");
                break;
        }
    }

    @Override
    public void onAllDegreesLoaded(ArrayList<Degree> listDegrees) {
        mListDegrees = listDegrees;

    }

    public void finishButton() {
        if (CheckEnabledInternet()) {
            createFinishDialog(userName
                    , ifBaby
                    , String.valueOf(user_year)
                    , degree1
                    , degree2
                    , degree3
                    , user_type);
        }


    }

    @Override
    public void onPostLoadedUsersExtraInfo(int success, String user_unique_id) {
        if (success == 1) {
            SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

            db.updateQuery(unique_id,
                    user_type,
                    user_year,
                    degree1 != null ? degree1.getDbid_degree() : 0,
                    degree2 != null ? degree2.getDbid_degree() : 0,
                    degree3 != null ? degree3.getDbid_degree() : 0);
            Toast.makeText(SettingsActivity.this, "Updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(SettingsActivity.this, "Problem With Sending Info", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void backToLoginActivity() {

    }

    public void createFinishDialog(String name,  boolean ifBaby, String yearsStudy, Degree degree1,
                                   Degree degree2, Degree degree3, final String userType) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You want to Update your Info?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finishConfirmDialog(userType);
                Toast.makeText(SettingsActivity.this, "Updating..", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(NewsFeedActivity.this, "You clicked no button", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void finishConfirmDialog(String userType) {
        if (unique_id != null && CheckEnabledInternet()) {
            new TaskInsertUsersExtraInfo(this,
                    unique_id,
                    userType,
                    user_year,
                    degree1 != null ? degree1.getDbid_degree() : 0,
                    degree2 != null ? degree2.getDbid_degree() : 0,
                    degree3 != null ? degree3.getDbid_degree() : 0).execute();
        }
    }

}
