package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.greenfrvr.hashtagview.HashtagView;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.Tags.Transformers;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterDegrees;
import com.studentadvisor.noam.studentadvisor.adapters.SearchAdapter;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.SubjectsLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.SharedPreference;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.School;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.services.MyDegreeService;
import com.studentadvisor.noam.studentadvisor.services.MySubjectsService;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskAllFollowedDegrees;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadDegrees;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadSubjects;


import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_DEGREE_PARCEL;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.*;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.*;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_PROFILE_IMAGE_ID;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_DEGREE_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_YEAR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TransferQueue;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ExploreActivity extends AppCompatActivity implements SubjectsLoadedListener,
        DegreesLoadedListener,
        DrawerLayoutCallBack,
        NavigationView.OnNavigationItemSelectedListener{
    //    TagGroup mTagGroup;
    HashtagView mTagGroup, mSchoolTagGroup;
    HashtagView mSearchTagGroup;
    Button mSearchButton;
    CoordinatorLayout mExploreLayout;
    TextView mHeaderTv;
    Toolbar toolbar;

    private static final String TAG = "ExploreActivity";

    //The key used to store arraylist of degrees objects to and from parcelable
    private static final String STATE_SUBJECT = "state_subject";
    //the arraylist containing our list of box office his
    private ArrayList<Subject> mListSubjects = new ArrayList<>();
    private List<School> mSchoolList = new ArrayList<School>();

    private List<String> mSearchList = new ArrayList<String>();
    private List<String> mSearchSchoolList = new ArrayList<String>();
    private List<String> mWholeSearchList = new ArrayList<String>();

    private List<String> mStringSubjectList = new ArrayList<String>();
    private List<String> mStringSchoolList = new ArrayList<String>();

    //The key used to store arraylist of degrees objects to and from parcelable
    private static final String STATE_DEGREE = "state_degree";
    //the arraylist containing our list of box office his
    private ArrayList<Degree> mListDegrees = new ArrayList<>();
    //the adapter responsible for displaying our degree within a RecyclerView
    private AdapterDegrees mAdapter;
    //the recyclerview containing showing all our degrees
    private ArrayList<String> mDegrees;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;

        private static final long POLL_FREQUENCY = 86400000;
//    private static final long POLL_FREQUENCY = 15000;
    private JobScheduler mJobScheduler;
    //int corresponding to the id of our JobSchedulerService
    private static final int JOB_ID_DEGREE = 100;
    private static final int JOB_ID_SUBJECT = 101;

    final String PREFS_NAME = "MyPrefsFile";
    private View mStatusBar;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        checkPref();
//        setupJob();
        mExploreLayout = (CoordinatorLayout) findViewById(R.id.exploreLayout);

        init_typeface();
        toolBarData();
        drawerLayoutSetup();
        mSelectedId = savedInstanceState == null ? R.id.menu_item_explore : savedInstanceState.getInt("SELECTED_ID");

        mHeaderTv = (TextView) findViewById(R.id.headerSearchTags);

        //TAGS GROUP
        mTagGroup = (HashtagView) findViewById(R.id.tag_group);
        mTagGroup.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item, boolean selected) {
//                Snackbar.make(mExploreLayout, String.format("Selected items: %s", Arrays.toString(mTagGroup.getSelectedItems().toArray())), Snackbar.LENGTH_SHORT).show();
                if (!mSearchList.contains(item.toString()) && !mWholeSearchList.contains(item.toString())) {
                    if ((mSearchList.size() < 4)) {
                        mSearchList.add(item.toString());
                        mWholeSearchList.add(item.toString());
                        mSearchTagGroup.setData(mWholeSearchList, Transformers.HASH);
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                } else {
                    mSearchList.remove(item.toString());
                    mWholeSearchList.remove(item.toString());

                    if (mWholeSearchList.size() == 0) {
                        mSearchTagGroup.removeAllViews();
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                    else {
                        mSearchTagGroup.setData(mWholeSearchList, Transformers.HASH);
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                }
                if (mSearchList.size() == 4) {

                }
            }
        });
        mSchoolTagGroup = (HashtagView) findViewById(R.id.tag_school_group);
        mSchoolTagGroup.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item, boolean selected) {
//                Snackbar.make(mExploreLayout, String.format("Selected items: %s", Arrays.toString(mSchoolTagGroup.getSelectedItems().toArray())), Snackbar.LENGTH_SHORT).show();
                if (!mSearchSchoolList.contains(item.toString()) && !mWholeSearchList.contains(item.toString())) {
                    mSearchSchoolList.add(item.toString());
                    mWholeSearchList.add(item.toString());
                    mSearchTagGroup.setData(mWholeSearchList, Transformers.HASH);
                    selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);

                } else {
                    mSearchSchoolList.remove(item.toString());
                    mWholeSearchList.remove(item.toString());

                    if (mWholeSearchList.size() == 0) {
                        mSearchTagGroup.removeAllViews();
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                    else {
                        mSearchTagGroup.setData(mWholeSearchList, Transformers.HASH);
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                }
            }
        });

        mSearchTagGroup = (HashtagView) findViewById(R.id.tag_search_group);
        mSearchTagGroup.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item, boolean selected) {
                if(mWholeSearchList.contains(item.toString())){
                    mWholeSearchList.remove(item.toString());
                    if (mSearchList.contains(item.toString())){
                        mSearchList.remove(item.toString());
                    }
                    if(mSearchSchoolList.contains(item.toString())){
                        mSearchSchoolList.remove(item.toString());
                    }

                    if (mWholeSearchList.size() == 0){
                        mSearchTagGroup.removeAllViews();
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                    else{
                        mSearchTagGroup.setData(mWholeSearchList, Transformers.HASH);
                        selectedChipToSqlWhereStatement(mSearchList, mSearchSchoolList);
                    }
                }
            }
        });

        //check subjects in saveInstanceState bundle
        checkSaveInstanceSubjects(savedInstanceState);
        //check degrees in SaveInstanceState bundle
        checkSaveInstanceDegrees(savedInstanceState);





        mSearchButton = (Button) findViewById(R.id.exploreButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.m(convertToSqlWhereStatment(mSearchList, mSearchSchoolList));

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                if (mSearchList.size() > 0 || mSearchSchoolList.size() > 0) {
                    if (MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, convertToSqlWhereStatment(mSearchList, mSearchSchoolList), true).size() > 0) {
                        i.putExtra(LIST_SELECTED_SUBJECTS, convertToSqlWhereStatment(mSearchList, mSearchSchoolList));
                        startActivity(i);
                        L.m("yes");
                    } else {
                        Snackbar.make(mExploreLayout, "No Data Return", Snackbar.LENGTH_SHORT).show();
                        L.m("snack");
                    }
                } else {
                    i.putExtra(LIST_SELECTED_SUBJECTS, "1");
                    startActivity(i);
                    L.m("all");
                }

            }
        });

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void checkPref() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            L.m("First time - launching explore activity");

            // first time task

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).apply();
            setupJob();

        }
    }

    private void setupJob() {
        mJobScheduler = JobScheduler.getInstance(this);
        //set an initial delay with a Handler so that the data loading by the JobScheduler does not clash with the loading inside the Fragment
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //schedule the job after the delay has been elapsed
                buildJob();
            }
        }, 30000);
    }

    private void buildJob() {
        //attach the job ID and the name of the Service that will work in the background
        JobInfo.Builder degreeBuilder = new JobInfo.Builder(JOB_ID_DEGREE, new ComponentName(this, MyDegreeService.class));
        //set periodic polling that needs net connection and works across device reboots
        degreeBuilder.setPeriodic(POLL_FREQUENCY)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);
        mJobScheduler.schedule(degreeBuilder.build());
        L.m("Build MyDegreeService");

        //---- subjects
        //attach the job ID and the name of the Service that will work in the background
        JobInfo.Builder subjectsBuilder = new JobInfo.Builder(JOB_ID_SUBJECT, new ComponentName(this, MySubjectsService.class));
        //set periodic polling that needs net connection and works across device reboots
        subjectsBuilder.setPeriodic(POLL_FREQUENCY)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);
        mJobScheduler.schedule(subjectsBuilder.build());
        L.m("Build MySubjectsService");
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

    private void checkSaveInstanceDegrees(Bundle savedInstanceState){
        if ((savedInstanceState != null) && savedInstanceState.getParcelableArray(STATE_DEGREE) != null) {
            mListDegrees = savedInstanceState.getParcelableArrayList(STATE_DEGREE);

        } else {
            mListDegrees = MyApplication.getWritableDatabase().readDegrees(DBDegrees.ALL_DEGREES);
            mSchoolList = MyApplication.getWritableDatabase().readSchools();

            //DEGREES
            if (mListDegrees.isEmpty()) {
                L.m("ExploreActivity: executing TaskLoadDegrees");
                new TaskLoadDegrees(this).execute();
            } else {
                final List<String> list = new ArrayList<String>();
                for (School school : mSchoolList) {
                    list.add(school.getSchool_name_he());
                }
                mSchoolTagGroup.setData(list, Transformers.HASH);
            }
        }

    }
    private void checkSaveInstanceSubjects(Bundle savedInstanceState) {
        if ((savedInstanceState != null) && savedInstanceState.getParcelableArray(STATE_SUBJECT) != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            mListSubjects = savedInstanceState.getParcelableArrayList(STATE_SUBJECT);
        } else {
            mListSubjects = MyApplication.getWritableDatabase().readSubjects(DBDegrees.ALL_SUBJECTS);

            //SUBJECTS
            if (mListSubjects.isEmpty()) {
                L.m("ExploreActivity: executing TaskLoadSubjects");
                new TaskLoadSubjects(this).execute();
            } else {
                final List<String> list = new ArrayList<String>();
                for (Subject subject : mListSubjects) {
                    list.add(subject.getSubject_name());
                }
                mTagGroup.setData(list, Transformers.HASH);
//                mSchoolTagGroup.setData(list, Transformers.HASH);

            }

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
    public void onAllSubjectsLoaded(ArrayList<Subject> listSubjects) {
        Log.d("noam", listSubjects.toString());
        List<String> list = new ArrayList<String>();
        for (Subject subject : listSubjects) {
            list.add(subject.getSubject_name());
        }

//        mTagGroup = (TagGroup) findViewById(R.id.tag_group);
//        mTagGroup.setTags(list);
//        mTagGroup = (HashtagView) findViewById(R.id.tag_group);
        mListSubjects = listSubjects;
        mTagGroup.setData(list, Transformers.HASH);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_SUBJECT, mListSubjects);
        outState.putParcelableArrayList(STATE_DEGREE, mListDegrees);
    }


    @Override
    public void onAllDegreesLoaded(ArrayList<Degree> listDegrees) {
        L.m("onAllDegreesLoaded: onAllDegreesLoaded ");
        //update the Adapter to contain the new movies downloaded from the web
        /*
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        */
//        mAdapter.setDegrees(listDegrees);
        mListDegrees = listDegrees;
        mSchoolList = MyApplication.getWritableDatabase().readSchools();

        //DEGREES
        if (!mSchoolList.isEmpty()) {
            final List<String> list = new ArrayList<String>();
            for (School school : mSchoolList) {
                list.add(school.getSchool_name_he());
            }
            mSchoolTagGroup.setData(list, Transformers.HASH);
        }
    }

    private String convertToSqlWhereStatment(List<String> subjectList, List<String> schoolList) {
        String subQuery = "";
        String subjects_sql = "" + COLUMN_SUBJECT_1 + "||" + COLUMN_SUBJECT_2 + "||" +
                COLUMN_SUBJECT_3 + "||" + COLUMN_SUBJECT_4 + " ";

        if (subjectList.size() > 0) {
            for (int i = 0; i < subjectList.size(); i++) {
                if (i == subjectList.size() - 1) {
                    subQuery += subjects_sql + " LIKE '%" + subjectList.get(i) + "%'  ";
                } else {
                    subQuery += subjects_sql + " LIKE '%" + subjectList.get(i) + "%' AND ";
                }
            }
        }


        if (schoolList.size() > 0) {
            if (subjectList.size() > 0) {
                subQuery += " AND ( ";
            }
            for (int i = 0; i < schoolList.size(); i++) {
                if (i == schoolList.size() - 1) {
                    subQuery += COLUMN_SCHOOL_NAME_HE + " LIKE '%" + schoolList.get(i) + "%'  ";
                } else {
                    subQuery += COLUMN_SCHOOL_NAME_HE + " LIKE '%" + schoolList.get(i) + "%' OR ";
                }
            }
            if (subjectList.size() > 0) {
                subQuery += " )";
            }
        }

        return subQuery;
    }

    private void selectedChipToSqlWhereStatement(List<String> subjectList, List<String> schoolList){

        if (subjectList.size() == 0 && schoolList.size() == 0){
            mHeaderTv.setVisibility(View.VISIBLE);

            //DEGREES
            if (mListDegrees.isEmpty()) {
                L.m("ExploreActivity: executing TaskLoadDegrees");
                new TaskLoadDegrees(this).execute();
            } else {
                final List<String> list = new ArrayList<String>();
                for (School school : mSchoolList) {
                    list.add(school.getSchool_name_he());
                }
                mSchoolTagGroup.setData(list, Transformers.HASH);
            }

            //SUBJECTS
            if (mListSubjects.isEmpty()) {
                L.m("ExploreActivity: executing TaskLoadSubjects");
//                new TaskLoadSubjects(this).execute();
            } else {
                final List<String> list = new ArrayList<String>();
                for (Subject subject : mListSubjects) {
                    list.add(subject.getSubject_name());
                }
                mTagGroup.setData(list, Transformers.HASH);

            }
        }
        else {
            mHeaderTv.setVisibility(View.GONE);

            String subQuery = "";
            String subjects_sql = "" + COLUMN_SUBJECT_1 + " || " + COLUMN_SUBJECT_2 + " || " +
                    COLUMN_SUBJECT_3 + " || " + COLUMN_SUBJECT_4 + " ";

            if (subjectList.size() > 0) {
                for (int i = 0; i < subjectList.size(); i++) {
                    if (i == subjectList.size() - 1) {
                        subQuery += subjects_sql + " LIKE '%" + subjectList.get(i) + "%'  ";
                    } else {
                        subQuery += subjects_sql + " LIKE '%" + subjectList.get(i) + "%' AND ";
                    }
                }
            }


            if (schoolList.size() > 0) {
                String schools_sql = "'";
                if (subjectList.size() > 0) {
                    subQuery += " AND ( ";
                }
                for (int i = 0; i < schoolList.size(); i++) {
                    if (i == schoolList.size() - 1) {
                        schools_sql += schoolList.get(i) + "'";
                    } else {
                        schools_sql += schoolList.get(i) + "||";
                    }
                }

                for (int i = 0; i < schoolList.size(); i++) {
                    if (i == schoolList.size() - 1) {
                        subQuery += schools_sql + " LIKE '%'||" + COLUMN_SCHOOL_NAME_HE + "||'%'  ";
                    } else {
                        subQuery += schools_sql + " LIKE '%'||" + COLUMN_SCHOOL_NAME_HE + "||'%' OR ";
                    }
                }
                if (subjectList.size() > 0) {
                    subQuery += " )";
                }

            }

            mStringSubjectList = MyApplication.getWritableDatabase().readFilterSubject(subQuery, true);
            mStringSchoolList = MyApplication.getWritableDatabase().readFilterSchools(subQuery, true);

            L.m(mStringSubjectList.toString());
            L.m(mStringSchoolList.toString());

            for (String item : mWholeSearchList){
                if (mStringSubjectList.contains(item)){
                    mStringSubjectList.remove(item);
                }
                if (mStringSchoolList.contains(item)){
                    mStringSchoolList.remove(item);
                }
            }

            if (mStringSchoolList.size() != 0) {
                mSchoolTagGroup.setData(mStringSchoolList, Transformers.HASH);
            }else{
                mSchoolTagGroup.removeAllViews();
            }
            if (mStringSubjectList.size() != 0) {
                mTagGroup.setData(mStringSubjectList, Transformers.HASH);
            }else{
                mTagGroup.removeAllViews();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_search:
                loadToolBarSearch();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //------------ Search View ------------- //

    private void toolBarData() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Explore");
//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public void drawerLayoutSetup(){
        mDrawer= (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
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

    public void loadToolBarSearch() {


        ArrayList<String> degreeStored = SharedPreference.loadList(ExploreActivity.this, Util.PREFS_NAME, Util.KEY_COUNTRIES);

        View view = ExploreActivity.this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView searchImg = (ImageView) view.findViewById(R.id.img_search);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

        Util.setListViewHeightBasedOnChildren(listSearch);

        edtToolSearch.setHint("חפש תואר");

        final Dialog toolbarSearchDialog = new Dialog(ExploreActivity.this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(true);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

//        toolbarSearchDialog.setCancelable(true);
        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        degreeStored = (degreeStored != null && degreeStored.size() > 0) ? degreeStored : new ArrayList<String>();
        final SearchAdapter searchAdapter = new SearchAdapter(ExploreActivity.this, degreeStored, false);

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
                SharedPreference.addList(ExploreActivity.this, Util.PREFS_NAME, Util.KEY_COUNTRIES, country);
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


                        List<Degree> tempListDegree  = new ArrayList<Degree>();
                        tempListDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, searchQuery, true);

                        L.m(""+ tempListDegree.size() + " size of temp list degree");

                        if (tempListDegree.size() == 1){
                            edtToolSearch.setText("");
                            Intent i = new Intent(getApplicationContext(), DegreeActivity.class);
//                        i.putExtra(STATE_DEGREE_PARCEL, tempListDegree.get(0));
                            i.putExtra(SELECTED_DEGREE, tempListDegree.get(0));
                            startActivity(i);
                        }
                        else if (tempListDegree.size() > 1){

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra(LIST_SELECTED_SUBJECTS, searchQuery);
                            startActivity(i);

                            // Toast.makeText(getApplicationContext(), "Too Many Results, be more specific", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                        }

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Search your Degree..", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                String[] country = ExploreActivity.this.getResources().getStringArray(R.array.countries_array);
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


                    List<Degree> tempListDegree  = new ArrayList<Degree>();
                    tempListDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, searchQuery, true);

                    L.m(""+ tempListDegree.size() + " size of temp list degree");

                    if (tempListDegree.size() == 1){
                        edtToolSearch.setText("");
                        Intent i = new Intent(getApplicationContext(), DegreeActivity.class);
//                        i.putExtra(STATE_DEGREE_PARCEL, tempListDegree.get(0));
                        i.putExtra(SELECTED_DEGREE, tempListDegree.get(0));
                        startActivity(i);
                    }
                    else if (tempListDegree.size() > 1){

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra(LIST_SELECTED_SUBJECTS, searchQuery);
                        startActivity(i);

                       // Toast.makeText(getApplicationContext(), "Too Many Results, be more specific", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Search your Degree..", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    public void onGetFollowedDegrees(ArrayList<FollowObj> followedDegrees) {

    }

    @Override
    public void onGetLikedDegrees(ArrayList<LikeObj> likedDegrees) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedId=menuItem.getItemId();
        itemSelection(mSelectedId);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + "ExploreActivity");
        mTracker.setScreenName("Image~" + "ExploreActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void itemSelection(int mSelectedId) {

        switch(mSelectedId){

            case R.id.menu_item_home:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent homeIntent = new Intent(this, NewsFeedActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
//                finish();
                break;

            case R.id.menu_item_explore:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                onRestart();
                /*
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(this , ExploreActivity.class);
                startActivity(intent);
                finish();
                */
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
                    Toast.makeText(ExploreActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
                        Intent logOutIntent = new Intent(ExploreActivity.this, IntroActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                }
                else{
                    Intent logOutIntent = new Intent(ExploreActivity.this, IntroActivity.class);
                    startActivity(logOutIntent);
                    finish();
                }
                break;

        }

    }
}
