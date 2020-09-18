package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterFavorites;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterNewsFeed;
import com.studentadvisor.noam.studentadvisor.adapters.CommentAdapterList;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.DBComments;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.MarginDecoration;
import com.studentadvisor.noam.studentadvisor.extras.RecycleTouchListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.fragments.MyFavoritesFragment;
import com.studentadvisor.noam.studentadvisor.fragments.NewsFeedFragment;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskInsertComment;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadComments;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadPosts;
import com.studentadvisor.noam.studentadvisor.tasks.TaskVotePost;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_COMMENTS;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_HOME;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_2;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_3;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SUBJECT_4;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.NEWS_FEED_INITIAL_TAB;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_PROFILE_IMAGE_ID;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_DEGREE_1;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.KEY_BUNDLE_USER_YEAR;

/**
 * Created by Noam on 12/31/2015.
 */
public class FavoriteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        PostsLoadedListener,
        DialogCommunicatorNewsFeed {

    private CoordinatorLayout mCoordinator;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Degree mDegree;
    private ExtraInfoDegree mExtraInfo;
    private ProgressDialog pDialog;
    private Dialog mPostCommentsDialog;
    private int mSelectedId;

    private LinearLayout mEmergencyLayout;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private AdapterFavorites rvAdapter;
    private ArrayList<Post> data = new ArrayList<>();
    private String mTypePost;
    private boolean mIsRate;
    private String mUserUid = "0";

    private int rows_to_fetch = 10;
    private int start_from_row = 0;
    private static final String STATE_POSTS_FAVORITE = "state_posts_favorite";
    private static final boolean FIRSTS_POSTS = true;
    private PostsLoadedListener mContextPostsListener;
    private DialogCommunicatorNewsFeed mContextDialogCommunicatorNewsFeed;

    private static boolean isLoading = false;
    private boolean isFinishFetch = false;
    private boolean isFetching = false;
    private boolean resumeChecker;

    private String sql_where_string = "";
    private ArrayList<Post> arrayWherePosts = new ArrayList<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private NewsFeedFragment fragmentContext;

//    private int mTabFragmentNumber = 0;

    private static final String ARG_PARAM_TYPE_POST = "type_post_arg";
    private static final String ARG_PARAM_IS_RATE = "is_rate_arg";
    private static final String ARG_PARAM_TAB_NUM = "tab_number_degree";
    private static final String MAGIC_DBID_DEGREE = "-999";

    private View mStatusBar;
    private Tracker mTracker;
    private static final String TAG = "FavoriteActivity";

    private View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        CheckEnabledInternet();

        generalOnCreateSetUp();
        mContextDialogCommunicatorNewsFeed = this;
        mCoordinator = (CoordinatorLayout) findViewById(R.id.favoriteLayout);

        arrayWherePosts = MyApplication.getWritableCommentsDatabase().readPosts(Util.checkIfLoggedIn().getUser_id());
        sql_where_string = convertPostsToSqlStringWhere(arrayWherePosts);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        init_typeface();
        setupToolbar();
        setupDrawer();
        mSelectedId = savedInstanceState == null ? R.id.menu_item_home : savedInstanceState.getInt("SELECTED_ID");

        mEmergencyLayout = (LinearLayout) findViewById(R.id.emergencyLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeMyFavoriteRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.myFavoriteRecycle);
        recyclerView.addItemDecoration(new MarginDecoration(this));

        rvAdapter = new AdapterFavorites(this);
        //rvAdapter.setClickListener(this);

        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecycleTouchListenerPosts(this,
                recyclerView,
                data.size() == 0 ? -1 : data.size() + 1,
                true,
                new ClickListenerPosts() {
                    @Override
                    public void onClickPlusButton(View view, int position) {

                        Post post = data.get(position);

                        if (post != null) {
                            // SqLite database handler
                            SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                            // session manager
                            SessionManager session = new SessionManager(MyApplication.getAppContext());
                            if (session.isLoggedIn()) {
                                // Fetching user details from sqlite
                                HashMap<String, String> user = db.getUserDetails();

                                String name = user.get("name");
                                String user_uid = user.get("uid");

                                L.m(name + " " + user_uid);
                                if (CheckEnabledInternet()) {
                                    new TaskVotePost(mContextPostsListener, user_uid, name, post.getDbid_post() + "", "1").execute();

                                    if (post.getUser_choice() == 1) {
                                        data.get(position).setTotalScore(data.get(position).getTotalScore() - 1);
                                        data.get(position).setUser_choice(0);
                                    } else if (post.getUser_choice() == 0) {
                                        data.get(position).setTotalScore(data.get(position).getTotalScore() + 1);
                                        data.get(position).setUser_choice(1);
                                    } else if (post.getUser_choice() == -1) {
                                        data.get(position).setTotalScore(data.get(position).getTotalScore() + 2);
                                        data.get(position).setUser_choice(1);
                                    }
                                    rvAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // Log in Dialog
                                Toast.makeText(getBaseContext(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Problem
                        }
                    }

                    @Override
                    public void onClickMinusButton(View view, int position) {
                        Post post = data.get(position);

                        if (post != null) {
                            // SqLite database handler
                            SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                            // session manager
                            SessionManager session = new SessionManager(MyApplication.getAppContext());
                            if (session.isLoggedIn()) {
                                // Fetching user details from sqlite
                                HashMap<String, String> user = db.getUserDetails();

                                String name = user.get("name");
                                String user_uid = user.get("uid");

                                L.m(name + " " + user_uid);
                                if (CheckEnabledInternet()) {
                                    new TaskVotePost(mContextPostsListener, user_uid, name, post.getDbid_post() + "", "-1").execute();

                                    if (post.getUser_choice() == 1) {
                                        data.get(position).setTotalScore(data.get(position).getTotalScore() - 2);
                                        data.get(position).setUser_choice(-1);
                                    } else if (post.getUser_choice() == 0) {
                                        data.get(position).setTotalScore(data.get(position).getTotalScore() - 1);
                                        data.get(position).setUser_choice(-1);
                                    } else if (post.getUser_choice() == -1) {
                                        data.get(position).setTotalScore(data.get(position).getTotalScore() + 1);
                                        data.get(position).setUser_choice(0);
                                    }

                                    rvAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // Log in Dialog
                                Toast.makeText(getBaseContext(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Problem
                        }
                    }

                    @Override
                    public void onClickCommentButton(View view, int position) {
//                mCommunicator.dialog_post_comments();
                        Post post = data.get(position);
                        if (post != null) {
                            if (CheckEnabledInternet()) {
                                new TaskLoadComments(mContextDialogCommunicatorNewsFeed, String.valueOf(post.getDbid_post()), post, 0, position).execute();
                            }
                        }

                    }

                    @Override
                    public void onClickFetchMoreButton(View view, int position) {
                        if (mUserUid != null && mContextPostsListener != null && position > 0) {
                            if (!isLoading && !isFinishFetch && !isFetching && CheckEnabledInternet()) {
                                isLoading = true;
                                isFetching = true;

                                new TaskLoadPosts(mContextPostsListener, !FIRSTS_POSTS,
                                        MAGIC_DBID_DEGREE, //type_post
                                        MAGIC_DBID_DEGREE, //dbid_degree
                                        mUserUid,
                                        sql_where_string)
                                        .execute();

                                rvAdapter.setFETCHING(true);
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }

                        } else {
                            L.m("Error - Degree or TypePost not fetched to the fragment");
                        }

                    }

                    @Override
                    public void onClickDegreeTitleButton(View view, int position) {
                        Post post = data.get(position);
                        if (post != null) {
                            if (post.getDegree_id() != 0) {
                                String sql_statment = " " + COLUMN_ID_DEGREE + " = " + post.getDegree_id() + "";
                                ArrayList<Degree> mListDegrees = new ArrayList<>();

                                mListDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, sql_statment, true);
                                //if the database is empty, trigger an AsycnTask to download movie list from the web
                                if (mListDegrees.isEmpty()) {
                                    // need to handle it
                                } else {
                                    Degree degree = mListDegrees.get(0);
                                    if (CheckEnabledInternet()) {
                                        Intent i = new Intent(getApplicationContext(), DegreeActivity.class);
                                        i.putExtra(SELECTED_DEGREE, degree);
                                        startActivity(i);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onClickAddToFavoritesButton(View view, int position) {
                        Post post = data.get(position);
                        if (post != null) {
                            dialog_add_to_favorite(post);
                            //MyApplication.getWritableCommentsDatabase().insertComment(post, false);
                        }
                    }
                }));

        mProgressBar = (ProgressBar) findViewById(R.id.myFavoriteProgressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#112C4B"), PorterDuff.Mode.MULTIPLY);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(STATE_POSTS_FAVORITE) != null) {
            data = savedInstanceState.getParcelableArrayList(STATE_POSTS_FAVORITE);
            rvAdapter.setPosts(data);
        } else {
            if (mUserUid != null) {
                if (CheckEnabledInternet()) {
                    new TaskLoadPosts(this, FIRSTS_POSTS,
                            MAGIC_DBID_DEGREE, //type_post
                            MAGIC_DBID_DEGREE, // dbid_Degree
                            mUserUid,
                            sql_where_string)
                            .execute();
                }
            } else {
                L.m("Error - mUserUId is Null - NewsFeed");
            }
        }

//        if (savedInstanceState != null) {
//            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
//            mDegree = savedInstanceState.getParcelable(STATE_DEGREE_PARCEL);
//
//        }

    }

    public void setStatusBarColor(View statusBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBar.setVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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

    private String convertPostsToSqlStringWhere(ArrayList<Post> listPosts) {
        String sql_query = "";

        if (listPosts.size() > 0) {
            for (int i = 0; i < listPosts.size(); i++) {
                if (i == listPosts.size() - 1) {
                    sql_query += " " + listPosts.get(i).getDbid_post() + "  ";
                } else {
                    sql_query += " " + listPosts.get(i).getDbid_post() + ", ";
                }
            }
        }
        return sql_query;
    }

    private void generalOnCreateSetUp() {
        // SqLite database handler
        SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

        // session manager
        SessionManager session = new SessionManager(MyApplication.getAppContext());
        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            mUserUid = user.get("uid");
        }

        mContextPostsListener = this;
//        fragmentContext = this;
        resumeChecker = false;
        rows_to_fetch = 10;
        start_from_row = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumeChecker == false) {
            resumeChecker = true;
        } else {
            onRefresh();
        }
//        onRefresh();
        Log.i(TAG, "Setting screen name: " + "FavoriteActivity");
        mTracker.setScreenName("Image~" + "FavoriteActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Favorites");
        setSupportActionBar(mToolbar);

//        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
//        mCollapsingToolbarLayout.setTitle("Home");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupDrawer() {
        mDrawer = (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                startActivity(intent);
                break;

            case R.id.menu_item_favorite:
                mDrawerLayout.closeDrawer(GravityCompat.START);
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
                    Toast.makeText(FavoriteActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
                        Intent logOutIntent = new Intent(FavoriteActivity.this, LoginActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                } else {
                    Intent logOutIntent = new Intent(FavoriteActivity.this, LoginActivity.class);
                    startActivity(logOutIntent);
                    finish();
                }
                break;

        }

    }

    @Override
    public void onRefresh() {
        CheckEnabledInternet();
        if (recyclerView.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mEmergencyLayout.setVisibility(View.GONE);
        }
        start_from_row = 0;
        setIsFinishFetch(false);
        arrayWherePosts = MyApplication.getWritableCommentsDatabase().readPosts(Util.checkIfLoggedIn().getUser_id());
        sql_where_string = convertPostsToSqlStringWhere(arrayWherePosts);
        if (mUserUid != null) {
            if (CheckEnabledInternet()) {
                new TaskLoadPosts(this, FIRSTS_POSTS,
                        MAGIC_DBID_DEGREE, //post
                        MAGIC_DBID_DEGREE, //dbid_degree
                        mUserUid,
                        sql_where_string)
                        .execute();
            }
        } else {
            L.m("Error - Degree or TypePost not fetched to the fragment");
            if (recyclerView.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.GONE);
                mEmergencyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_POSTS_FAVORITE, data);
    }


    //-------PostsLoadedListener

    @Override
    public void onPostsLoadedVolley(ArrayList<Post> listPosts) {
        //update the Adapter to contain the new movies downloaded from the web
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isFetching = false;

        data.clear();
        data.addAll(listPosts);

        rvAdapter.setPosts(data);
        recyclerView.getAdapter().notifyItemRangeChanged(0, data.size() - 1);

        if (data.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            mEmergencyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            mEmergencyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMorePostsLoadedVolley(ArrayList<Post> listPosts) {
        //update the Adapter to contain the new movies downloaded from the web
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isFetching = false;

        //data.addAll(listMovies);
        rvAdapter.setFETCHING(false);
        rvAdapter.addMorePosts(listPosts);
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void makeSnackBarText(String text) {
        Snackbar.make(mCoordinator, text, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.text_dismiss), mSnackBarClickListener)
                .setActionTextColor(getResources().getColor(R.color.snack_bar_color))
                .show();
    }

    @Override
    public void setProgressBarVisible() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setProgressBarGone() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public int getRows_to_fetch() {
        return rows_to_fetch;
    }

    @Override
    public void setRows_to_fetch(int num) {
        rows_to_fetch = num;
    }

    @Override
    public int getStart_from_row() {
        return start_from_row;
    }

    @Override
    public void setStart_from_row(int num) {
        start_from_row = num;
    }

    @Override
    public void setIsLoadingTrue() {
        isLoading = true;
    }

    @Override
    public void setIsLoadingFalse() {
        isLoading = false;
    }

    @Override
    public void setFetchingFalse() {
        isFetching = false;
    }

    @Override
    public void setIsFinishFetch(boolean temp) {
        isFinishFetch = temp;
    }

    @Override
    public void dialog_post_comments(ArrayList<Comment> listComments, final Post post, int tab, final int position) {
        if (mPostCommentsDialog != null && mPostCommentsDialog.isShowing()) {

        } else {
            View view = FavoriteActivity.this.getLayoutInflater().inflate(R.layout.view_post_comments, null);
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
            mPostCommentsDialog = new Dialog(FavoriteActivity.this, R.style.DialogSlideAnim);
            mPostCommentsDialog.setContentView(view);
            mPostCommentsDialog.setCancelable(false);
            mPostCommentsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
            mPostCommentsDialog.show();

            mPostCommentsDialog.setCancelable(true);
//        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            listComments = (listComments != null && listComments.size() > 0) ? listComments : new ArrayList<Comment>();
            final CommentAdapterList commentAdapterList = new CommentAdapterList(FavoriteActivity.this, listComments);
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
                            Toast.makeText(FavoriteActivity.this, "POST", Toast.LENGTH_SHORT).show();

                            // Fetching user details from sqlite
                            HashMap<String, String> user = db.getUserDetails();

                            String name = user.get("name");
                            String user_uid = user.get("uid");

                            String body = mCommentEdit.getText().toString();
                            if (CheckEnabledInternet()) {
                                new TaskInsertComment(mContextDialogCommunicatorNewsFeed, user_uid, name, body, 0, position).execute(post);

                                mCommentEdit.setText("");
                                pDialog.setMessage("Posting Comment ...");
                                showDialog();
                            }
                        } else {
                            // Log in Dialog
//                            Toast.makeText(getBaseContext(), "Need to Log In..", Toast.LENGTH_SHORT).show();
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
            if (tab == TAB_HOME) {
                if (mPostCommentsDialog != null) {
                    mPostCommentsDialog.cancel();
                    clickPostManualy(position);

                }
            }
        } else if (tab == TAB_COMMENTS) {
            if (mPostCommentsDialog != null) {
                mPostCommentsDialog.cancel();
                clickPostManualy(position);

            }
        }
    }

    @Override
    public void dialog_add_to_favorite(final Post post) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You want to remove it from your favorites");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MyApplication.getWritableCommentsDatabase().deleteSpecificComment(Util.checkIfLoggedIn().getUser_id(), post.getDbid_post());
                Toast.makeText(FavoriteActivity.this, "You Removed The Post Successfully", Toast.LENGTH_LONG).show();
                data.remove(post);
                recyclerView.getAdapter().notifyDataSetChanged();

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FavoriteActivity.this, "No No No", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

//            Toast.makeText(this, "Comment Failed", Toast.LENGTH_LONG).show();


    public void clickPostManualy(int position) {
        Post post = data.get(position);
        if (post != null) {
            if (CheckEnabledInternet()) {
                new TaskLoadComments(this, String.valueOf(post.getDbid_post()), post, 0, position).execute();
            }
        }
    }

    public boolean CheckEnabledInternet() {
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
                Intent intent = new Intent(FavoriteActivity.this, LoginActivity.class);
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
    public void setListPosts(ArrayList<Post> listPosts, boolean fetch) {

    }

    @Override
    public ArrayList<Post> getListPost() {
        return null;
    }

    @Override
    public void setListExplorePosts(ArrayList<Post> listPosts, boolean fetch) {

    }

    @Override
    public ArrayList<Post> getListExplorePost() {
        return null;
    }

    @Override
    public ArrayList<Degree> getListLikes() {
        return null;
    }

    @Override
    public ArrayList<Degree> getListFollows() {
        return null;
    }

    @Override
    public void updatePostsActionButton(int dbid_post, boolean ifPlus, boolean ifNewsFeed) {

    }



    // ---- menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_search:
                Intent intent = new Intent(this, ExploreActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
