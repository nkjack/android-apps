package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.studentadvisor.noam.studentadvisor.extras.Constants.*;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.LIST_SELECTED_SUBJECTS;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.NEWS_FEED_INITIAL_TAB;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_PROFILE_IMAGE_ID;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_DEGREE_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_YEAR;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterComments;
import com.studentadvisor.noam.studentadvisor.adapters.CommentAdapterList;
import com.studentadvisor.noam.studentadvisor.adapters.SearchAdapter;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.ExtraInfoDegreeLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.fragments.HomeDegreeFragment;
import com.studentadvisor.noam.studentadvisor.fragments.InternetPageFragment;
import com.studentadvisor.noam.studentadvisor.fragments.PostsTabFragment;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskAllFollowedDegrees;
import com.studentadvisor.noam.studentadvisor.tasks.TaskInsertComment;
import com.studentadvisor.noam.studentadvisor.tasks.TaskInsertNewPost;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLikeDegree;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadDegreeExtra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DegreeActivity extends AppCompatActivity implements ExtraInfoDegreeLoadedListener,
        DialogCommunicator,
        DrawerLayoutCallBack,
        NavigationView.OnNavigationItemSelectedListener {
    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager mPager;
    private YourPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Degree mDegree;
    private ExtraInfoDegree mExtraInfo;
    private DialogCommunicator mDialogCommunicator;
    private ProgressDialog pDialog;
    private Dialog mPostCommentsDialog;
    private int mSelectedId;
    private View mStatusBar;
    private static final String TAG = "DegreeActivity";
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_degree);
        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        CheckEnabledInternet();
        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectedId = R.id.menu_item_explore;

        mDialogCommunicator = this;
        init_typeface();

        setupDrawer();

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mPagerAdapter = new YourPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(mPagerAdapter);
        //Notice how the Tab Layout links with the Pager Adapter
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);

        //Notice how The Tab Layout adn View Pager object are linked
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        Log.i(TAG, "Setting screen name: " + "HomeDegreeTab");
                        mTracker.setScreenName("Image~" + "HomeDegreeTab");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 1:
                        Log.i(TAG, "Setting screen name: " + "DegreeRateTab");
                        mTracker.setScreenName("Image~" + "DegreeRateTab");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 2:
                        Log.i(TAG, "Setting screen name: " + "DegreePostTab");
                        mTracker.setScreenName("Image~" + "DegreePostTab");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                }
            }
        });
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setText("");
            mTabLayout.getTabAt(i).setIcon(mPagerAdapter.getIcon(i));
        }

        //Notice how the title is set on the Collapsing Toolbar Layout instead of the Toolbar
        mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.title_activity_degree));


        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            mDegree = savedInstanceState.getParcelable(STATE_DEGREE_PARCEL);
            ImageView imageHeader = (ImageView) findViewById(R.id.imageHeaderDegree);
            L.m(mDegree.getUrlHeaderPic());
            Picasso.with(this)
                    .load(mDegree.getUrlHeaderPic())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageHeader);

            mCollapsingToolbarLayout.setTitle(mDegree.getSchool_name_he());
            mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
            mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
            mPagerAdapter.setDegree(mDegree);
            mPagerAdapter.notifyDataSetChanged();

            if (savedInstanceState.getParcelable(STATE_EXTRA_DEGREE_PARCEL) != null) {
                L.m("saceInstanceState EXTRA DEGREE INFO");
                mExtraInfo = savedInstanceState.getParcelable(STATE_EXTRA_DEGREE_PARCEL);

                onExtraInfoDegreeLoaded(mExtraInfo);
            }
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mDegree = extras.getParcelable(SELECTED_DEGREE);
                if (mDegree != null) {
                    ImageView imageHeader = (ImageView) findViewById(R.id.imageHeaderDegree);
                    L.m(mDegree.getUrlHeaderPic());
                    Picasso.with(this)
                            .load(mDegree.getUrlHeaderPic())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(imageHeader);

                    mCollapsingToolbarLayout.setTitle(mDegree.getSchool_name_he());
                    mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
                    mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
                    mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
                    mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);

                    mPagerAdapter.setDegree(mDegree);
                    mPagerAdapter.notifyDataSetChanged();
                } else {
                    // return to previous activity
                }
            } else {
                // return to previous activity
            }

        }

        startTaskDegreeExtra();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


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

    private void setupDrawer(){
        mDrawer = (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
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
    public void startTaskDegreeExtra() {
        if (mDegree != null) {
            String dbid_degree = String.valueOf(mDegree.getDbid_degree());
            String user_id = checkIfLogedIn();

            L.m("executing Task Load Degree Extra");
            L.m("dbid_degree = " + dbid_degree);
            L.m("user_id = " + user_id);
            if (CheckEnabledInternet()) {
                new TaskLoadDegreeExtra(this, dbid_degree, user_id).execute();
            }
        }
    }

    @Override
    public void dialog_add_to_favorite(final Post post) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You want to add it into your favorites");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MyApplication.getWritableCommentsDatabase().insertComment(post, false, Util.checkIfLoggedIn().getUser_id());
                Toast.makeText(DegreeActivity.this, "Added, Check Out your Favorites", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DegreeActivity.this, "Ok, See u Next Time", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private String checkIfLogedIn() {
        // SqLite database handler
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());

        String user_id = "0";
        // session manager
        SessionManager session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

//            String name = user.get("name");
            user_id = user.get("uid");
        }

        return user_id;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        if (mDegree != null) {
            outState.putParcelable(STATE_DEGREE_PARCEL, mDegree);
        }
        if (mExtraInfo != null) {
            outState.putParcelable(STATE_EXTRA_DEGREE_PARCEL, mExtraInfo);
        }
    }

    @Override
    public void onExtraInfoDegreeLoaded(ExtraInfoDegree extraInfoDegree) {
        mExtraInfo = extraInfoDegree;

        if (mExtraInfo != null) {
            int index = TAB_HOME;
            YourPagerAdapter adapter = ((YourPagerAdapter) mPager.getAdapter());
            HomeDegreeFragment homefragment = (HomeDegreeFragment) adapter.getFragment(index);

            if (homefragment != null) {
                homefragment.setExtraInfo(mExtraInfo, mDegree);
                adapter.notifyDataSetChanged();
            }

            adapter.setDegree(mDegree);
            adapter.setExtraInfo(mExtraInfo);
            adapter.notifyDataSetChanged();

            L.m("extra info " + extraInfoDegree.toString());
        } else {
            int index = TAB_HOME;
            YourPagerAdapter adapter = ((YourPagerAdapter) mPager.getAdapter());
            HomeDegreeFragment homefragment = (HomeDegreeFragment) adapter.getFragment(index);

            if (homefragment != null) {
                homefragment.stopRefresh();
            }

//            Toast.makeText(DegreeActivity.this, "Connection Problem", Toast.LENGTH_SHORT).show();

            /*
            if (mDegree != null) {
                String dbid_degree = String.valueOf(mDegree.getDbid_degree());
                String user_id = checkIfLogedIn();

                L.m("executing Task Load Degree Extra from onExtraInfoDegreeLoaded");
                new TaskLoadDegreeExtra(this, dbid_degree, user_id).execute();

            }
            else{
                //need to go back..
            }
            */
        }

    }

    @Override
    public void onLikeDegree(int decision) {
        if (decision == 1) {
            mExtraInfo.setNumLikes(mExtraInfo.getNumLikes() + 1);
            mExtraInfo.setIfLiked(true);
        } else if (decision == 0) {
            mExtraInfo.setNumLikes(mExtraInfo.getNumLikes() - 1);
            mExtraInfo.setIfLiked(false);
        } else if (decision == -1) {
            Toast.makeText(DegreeActivity.this, "Connection Problem..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Degree getDegree() {
        return mDegree;
    }

    @Override
    public void dialog_post_comments(ArrayList<Comment> listComments, final Post post, final int tabFragment, final int positionPost) {
        if (mPostCommentsDialog != null && mPostCommentsDialog.isShowing()) {

        }
        else {
            View view = DegreeActivity.this.getLayoutInflater().inflate(R.layout.view_post_comments, null);
            RelativeLayout parentPostComments = (RelativeLayout) view.findViewById(R.id.parent_post_comments);
            final TextView mScoreLikes = (TextView) view.findViewById(R.id.scorePost);
            final TextView mScoreComments = (TextView) view.findViewById(R.id.commentCountCard);
            final TextView mScoreRate = (TextView) view.findViewById(R.id.scoreRateText);
//        final TextView posterProfileName = (TextView)view.findViewById(R.id.textViewProfileName);
//        final TextView posterBodyContent = (TextView)view.findViewById(R.id.bodyContent);
            final ListView mlistComments = (ListView) view.findViewById(R.id.list_comments);
            final EditText mCommentEdit = (EditText) view.findViewById(R.id.commentEditText);
            final Button mInsertCommentBtn = (Button) view.findViewById(R.id.postCommentBtn);

            final ImageButton starButton = (ImageButton) view.findViewById(R.id.rateImage);
            final TextView rateText = (TextView) view.findViewById(R.id.scoreRateText);

            if (post != null) {
                mScoreLikes.setText(post.getTotalScore() + "");
                mScoreComments.setText(post.getTotalComments() + "");
                mScoreRate.setText(post.getRate() + "");
//            posterProfileName.setText(post.getUser_name());
//            posterBodyContent.setText(post.getBody_content());

                if (post.getTypePost().contentEquals("qa")) {
                    starButton.setVisibility(View.GONE);
                    rateText.setText("");
                }
            }
//        RecyclerView mCommentsRecycler = (RecyclerView)view.findViewById(R.id.recycler_comments);

            Util.setListViewHeightBasedOnChildren(mlistComments);

//        CommentAdapterList adapterComments = new CommentAdapterList(this, listComments);
//        mCommentsRecycler.setAdapter(adapterComments);
//        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

//        final Dialog postCommentsDialog = new Dialog(DegreeActivity.this, R.style.MaterialSearch);
            mPostCommentsDialog = new Dialog(DegreeActivity.this, R.style.DialogSlideAnim);
            mPostCommentsDialog.setContentView(view);
            mPostCommentsDialog.setCancelable(false);
            mPostCommentsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
            mPostCommentsDialog.show();

            mPostCommentsDialog.setCancelable(true);
//        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            listComments = (listComments != null && listComments.size() > 0) ? listComments : new ArrayList<Comment>();
            final CommentAdapterList commentAdapterList = new CommentAdapterList(DegreeActivity.this, listComments);
            mlistComments.setVisibility(View.VISIBLE);
            mlistComments.setAdapter(commentAdapterList);

            parentPostComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPostCommentsDialog.cancel();
                }
            });

            mInsertCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCommentEdit.getText() != null && !mCommentEdit.getText().toString().contentEquals("") && post != null) {
                        // SqLite database handler
                        SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                        // session manager
                        SessionManager session = new SessionManager(MyApplication.getAppContext());
                        if (session.isLoggedIn()) {
//                        Toast.makeText(DegreeActivity.this, "POST", Toast.LENGTH_SHORT).show();

                            // Fetching user details from sqlite
                            HashMap<String, String> user = db.getUserDetails();

                            String name = user.get("name");
                            String user_uid = user.get("uid");

                            String body = mCommentEdit.getText().toString();

                            CheckEnabledInternet();
                            new TaskInsertComment(mDialogCommunicator, user_uid, name, body, tabFragment, positionPost).execute(post);

                            mCommentEdit.setText("");
                            pDialog.setMessage("Posting Comment ...");
                            showDialog();
                        } else {
                            // Log in Dialog
//                        Toast.makeText(getBaseContext(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                            dialog_log_in();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onPostComment(int success, int tab, int position) {
        //TODO: need to fill
        hideDialog();
        if (success == 1) {
            Toast.makeText(this, "Comment Posted Successfully", Toast.LENGTH_LONG).show();
            PostsTabFragment fragment = (PostsTabFragment) mPagerAdapter.getFragment(tab);
            if (fragment != null) {
                fragment.addCommentToPostList(position);
                if (mPostCommentsDialog != null) {
                    mPostCommentsDialog.cancel();
                    fragment.clickPostManualy(position);

                }
            }

        } else {
            Toast.makeText(this, "Comment Failed to Post", Toast.LENGTH_LONG).show();
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean CheckEnabledInternet() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[]  networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo networkInfo : networkInfos) {
            if(networkInfo.getState()== NetworkInfo.State.CONNECTED) {
                isConnected = true;
//                return isConnected
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

    @Override
    public void dialog_log_in() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You need to Log In or Register. Do you want to continue?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(DegreeActivity.this, LoginActivity.class);
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
                    Toast.makeText(DegreeActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
                        Intent logOutIntent = new Intent(DegreeActivity.this, IntroActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                }
                else{
                    Intent logOutIntent = new Intent(DegreeActivity.this, IntroActivity.class);
                    startActivity(logOutIntent);
                    finish();
                }
                break;

        }

    }

    @Override
    public void onGetFollowedDegrees(ArrayList<FollowObj> followedDegrees) {
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
    public void onGetLikedDegrees(ArrayList<LikeObj> likedDegrees) {

    }


}

class YourPagerAdapter extends FragmentStatePagerAdapter {
    int[] icons = {R.mipmap.ic_home_tab,
            R.mipmap.ic_star_tab_white,
            R.mipmap.ic_comment_tab
    };

    FragmentManager fragmentManager;
    private Map<Integer, Fragment> mPageReferenceMap;
    private Degree fragmentDegree = null;
    private ExtraInfoDegree fragmentExtraInfoDegree = null;

    public YourPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
        mPageReferenceMap = new HashMap<Integer, Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case TAB_HOME:
                //fragment = FragmentMyVotes.newInstance();
                fragment = HomeDegreeFragment.newInstance(fragmentDegree, fragmentExtraInfoDegree);
                mPageReferenceMap.put(position, fragment);
                break;
            case TAB_COMMENTS:
                fragment = PostsTabFragment.newInstance("rate", true, TAB_COMMENTS);
                mPageReferenceMap.put(position, fragment);
                break;
            case TAB_WEB:
                fragment = PostsTabFragment.newInstance("qa", false, TAB_WEB);
                mPageReferenceMap.put(position, fragment);
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + (position + 1);
    }

    public int getIcon(int position) {
        return icons[position];
    }

    public void setDegree(Degree degree) {
        fragmentDegree = degree;
    }

    public void setExtraInfo(ExtraInfoDegree extraInfo) {
        fragmentExtraInfoDegree = extraInfo;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

}


