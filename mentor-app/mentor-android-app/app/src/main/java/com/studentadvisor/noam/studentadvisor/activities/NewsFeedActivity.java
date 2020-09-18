package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
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

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.SettingsKeys.*;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterComments;
import com.studentadvisor.noam.studentadvisor.adapters.CommentAdapterList;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.fragments.ExploreFeedsFragment;
import com.studentadvisor.noam.studentadvisor.fragments.FollowesFragment;
import com.studentadvisor.noam.studentadvisor.fragments.HomeDegreeFragment;
import com.studentadvisor.noam.studentadvisor.fragments.MyFavoritesFragment;
import com.studentadvisor.noam.studentadvisor.fragments.MyLikesFragment;
import com.studentadvisor.noam.studentadvisor.fragments.NewsFeedFragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_DEGREE_PARCEL;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_EXTRA_DEGREE_PARCEL;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_COMMENTS;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_EXPLORE_POSTS;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_COUNT;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_HOME;
import static com.studentadvisor.noam.studentadvisor.extras.Constants.TAB_WEB;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.NEWS_FEED_INITIAL_TAB;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;

public class NewsFeedActivity extends AppCompatActivity implements DialogCommunicatorNewsFeed,
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLayoutCallBack {

    private static final String TAG = "NewsFeedActivity";

    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager mPager;
    private NewsFeedPageAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Degree mDegree;
    private ExtraInfoDegree mExtraInfo;
    private DialogCommunicatorNewsFeed mDialogCommunicator;
    private ProgressDialog pDialog;
    private Dialog mPostCommentsDialog;
    private int mSelectedId;
    private boolean mCommentDialogStatus = false;
    private TextView mToolbBarTitle;

    private ArrayList<Post> mListPosts = new ArrayList<>();
    private ArrayList<Post> mListExplorePosts = new ArrayList<>();
    private ArrayList<Degree> mListLikes = new ArrayList<>();
    private ArrayList<Degree> mListFollows = new ArrayList<>();

    private View mStatusBar;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        mStatusBar = findViewById(R.id.statusBarBackground);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.colorPrimaryDark));

        mCoordinator = (CoordinatorLayout) findViewById(R.id.rootCoordinatorNewsFeed);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mDialogCommunicator = this;
        init_typeface();
        setupToolbar();
        setupDrawer();
        setupTabs();
        mSelectedId = savedInstanceState == null ? R.id.menu_item_home : savedInstanceState.getInt("SELECTED_ID");


//        if (savedInstanceState != null) {
//            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
//            mDegree = savedInstanceState.getParcelable(STATE_DEGREE_PARCEL);
//
//        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int tab = 0;
            tab = extras.getInt(NEWS_FEED_INITIAL_TAB);
            if (mPager != null) {
                mPager.setCurrentItem(tab);
            }
        } else {
            // return to previous activity
        }


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


    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar_news_feed);
        mToolbar.setTitle("News Feed");
        setSupportActionBar(mToolbar);

//        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
//        mCollapsingToolbarLayout.setTitle("Home");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbBarTitle.setText("News Feed");
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

    private void setupTabs() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_news_feed);
        mPagerAdapter = new NewsFeedPageAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.view_pager_news_feed);
        mPager.setAdapter(mPagerAdapter);
        //Notice how the Tab Layout links with the Pager Adapter
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);

        //Notice how The Tab Layout adn View Pager object are linked
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
                    case 0:
                        mToolbBarTitle.setText("News Feed");
                        CheckEnabledInternet();
                        Log.i(TAG, "Setting screen name: " + "News Feed Tab");
                        mTracker.setScreenName("Image~" + "News Feed Tab");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 1:
                        mToolbBarTitle.setText("My Followes");
                        Log.i(TAG, "Setting screen name: " + "My Followes");
                        mTracker.setScreenName("Image~" + "My Followes");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 2:
                        mToolbBarTitle.setText("My Likes");
                        Log.i(TAG, "Setting screen name: " + "My Likes");
                        mTracker.setScreenName("Image~" + "My Likes");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 3:
                        mToolbBarTitle.setText("Community");
                        CheckEnabledInternet();
                        Log.i(TAG, "Setting screen name: " + "Community");
                        mTracker.setScreenName("Image~" + "Community");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                }
            }
        });
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setText("");
            mTabLayout.getTabAt(i).setIcon(mPagerAdapter.getIcon(i));
        }
    }

    @Override
    public void dialog_post_comments(ArrayList<Comment> listComments, final Post post, final int tabFragment, final int positionPost) {
        if (mPostCommentsDialog != null && mPostCommentsDialog.isShowing()) {

        } else {
            View view = NewsFeedActivity.this.getLayoutInflater().inflate(R.layout.view_post_comments, null);
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
            mPostCommentsDialog = new Dialog(NewsFeedActivity.this, R.style.DialogSlideAnim);
            mPostCommentsDialog.setContentView(view);
            mPostCommentsDialog.setCancelable(false);
            mPostCommentsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
            mPostCommentsDialog.show();

            mPostCommentsDialog.setCancelable(true);
//        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            listComments = (listComments != null && listComments.size() > 0) ? listComments : new ArrayList<Comment>();
            CommentAdapterList commentAdapterList = new CommentAdapterList(NewsFeedActivity.this, listComments);
//            AdapterComments commentAdapterList = new AdapterComments(NewsFeedActivity.this, listComments);


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
//                            Toast.makeText(NewsFeedActivity.this, "POST", Toast.LENGTH_SHORT).show();

                            // Fetching user details from sqlite
                            HashMap<String, String> user = db.getUserDetails();

                            String name = user.get("name");
                            String user_uid = user.get("uid");

                            String body = mCommentEdit.getText().toString();

                            new TaskInsertComment(mDialogCommunicator, user_uid, name, body, tabFragment, positionPost).execute(post);

                            mCommentEdit.setText("");
                            pDialog.setMessage("Posting Comment ...");
                            showDialog();
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
                NewsFeedFragment fragment = (NewsFeedFragment) mPagerAdapter.getFragment(tab);
                if (fragment != null) {
                    fragment.addCommentToPostList(position);
                    if (mPostCommentsDialog != null) {
                        mPostCommentsDialog.cancel();
                        fragment.clickPostManualy(position);

                    }
                }
            } else if (tab == TAB_EXPLORE_POSTS) {
                ExploreFeedsFragment fragment = (ExploreFeedsFragment) mPagerAdapter.getFragment(tab);
                if (fragment != null) {
                    fragment.addCommentToPostList(position);
                    if (mPostCommentsDialog != null) {
                        mPostCommentsDialog.cancel();
                        fragment.clickPostManualy(position);

                    }
                }
            } else if (tab == TAB_COMMENTS) {
                MyFavoritesFragment fragment = (MyFavoritesFragment) mPagerAdapter.getFragment(tab);
                if (fragment != null) {
                    fragment.addCommentToPostList(position);
                    if (mPostCommentsDialog != null) {
                        mPostCommentsDialog.cancel();
                        fragment.clickPostManualy(position);

                    }
                }
            }

        } else {
            Toast.makeText(this, "Comment Failed", Toast.LENGTH_LONG).show();
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
                Toast.makeText(NewsFeedActivity.this, "Great! Check Your Favorites", Toast.LENGTH_LONG).show();
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
                if (mPager != null) {
                    mPager.setCurrentItem(0);
                }
                break;

            case R.id.menu_item_explore:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(this, ExploreActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_item_favorite:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
                startActivity(favoriteIntent);
                break;

            case R.id.menu_item_followes:
                mDrawerLayout.closeDrawer(GravityCompat.START);

//                User user = Util.checkIfLoggedIn();
//                if (user != null && user.isLoggedIn())
                if (mPager != null) {
                    mPager.setCurrentItem(1);
                }
                break;

            case R.id.menu_item_likes:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (mPager != null) {
                    mPager.setCurrentItem(2);
                }
                break;

            case R.id.menu_item_globe:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (mPager != null) {
                    mPager.setCurrentItem(3);
                }
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
                    Toast.makeText(NewsFeedActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
                        Intent logOutIntent = new Intent(NewsFeedActivity.this, IntroActivity.class);
                        startActivity(logOutIntent);
                        finish();
                    }
                } else {
                    Intent logOutIntent = new Intent(NewsFeedActivity.this, IntroActivity.class);
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
        ArrayList<Degree> listDegrees = new ArrayList<>();
        if (MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, query, true).size() > 0) {

            listDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, query, true);
            mListFollows.clear();
            mListFollows.addAll(listDegrees);
            //if the database is empty, trigger an AsycnTask to download movie list from the web
            L.m("yes");
        }

        if (!listDegrees.isEmpty()) {
            int index = TAB_COMMENTS;
            NewsFeedPageAdapter adapter = ((NewsFeedPageAdapter) mPager.getAdapter());
            FollowesFragment followesFragment = (FollowesFragment) adapter.getFragment(index);

            if (followesFragment != null) {
                followesFragment.setmListDegrees(listDegrees);
                adapter.notifyDataSetChanged();
            }

        } else {
            int index = TAB_COMMENTS;
            NewsFeedPageAdapter adapter = ((NewsFeedPageAdapter) mPager.getAdapter());
            FollowesFragment followesFragment = (FollowesFragment) adapter.getFragment(index);

            if (followesFragment != null) {
                followesFragment.stopRefresh();
                followesFragment.setEmergencyLayoutVisibleRecyclerGone();
            }

//            Toast.makeText(NewsFeedActivity.this, "Connection Problem", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onGetLikedDegrees(ArrayList<LikeObj> likedDegrees) {
        String query = Util.convertAllLikedDegreeToSqlStatement(likedDegrees);
        L.m("--------" + query);
        ArrayList<Degree> listDegrees = new ArrayList<>();
        if (MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, query, true).size() > 0) {

            listDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, query, true);
            mListLikes.clear();
            mListLikes.addAll(listDegrees);
            //if the database is empty, trigger an AsycnTask to download movie list from the web
            L.m("yes");
        }

        if (!listDegrees.isEmpty()) {
            int index = TAB_WEB;
            NewsFeedPageAdapter adapter = ((NewsFeedPageAdapter) mPager.getAdapter());
            MyLikesFragment likesFragment = (MyLikesFragment) adapter.getFragment(index);

            if (likesFragment != null) {
                likesFragment.setmListDegrees(listDegrees);
                adapter.notifyDataSetChanged();
            }

        } else {
            int index = TAB_WEB;
            NewsFeedPageAdapter adapter = ((NewsFeedPageAdapter) mPager.getAdapter());
            MyLikesFragment likesFragment = (MyLikesFragment) adapter.getFragment(index);

            if (likesFragment != null) {
                likesFragment.stopRefresh();
                likesFragment.setEmergencyLayoutVisibleRecyclerGone();
            }

//            Toast.makeText(NewsFeedActivity.this, "Connection Problem", Toast.LENGTH_SHORT).show();

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
                Intent intent = new Intent(NewsFeedActivity.this, LoginActivity.class);
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
        if (!fetch) {
            mListPosts.clear();
            mListPosts.addAll(listPosts);
        } else {
            mListPosts.addAll(listPosts);
        }
    }

    @Override
    public ArrayList<Post> getListPost() {
        return mListPosts;
    }

    @Override
    public void setListExplorePosts(ArrayList<Post> listPosts, boolean fetch) {
        if (!fetch) {
            mListExplorePosts.clear();
            mListExplorePosts.addAll(listPosts);
        } else {
            mListExplorePosts.addAll(listPosts);
        }
    }

    @Override
    public ArrayList<Post> getListExplorePost() {
        return mListExplorePosts;
    }

    @Override
    public ArrayList<Degree> getListLikes() {
        return mListLikes;
    }

    @Override
    public ArrayList<Degree> getListFollows() {
        return mListFollows;
    }

    @Override
    public void updatePostsActionButton(int dbid_post, boolean ifPlus, boolean ifNewsFeed) {

        if (!ifNewsFeed) {
            for (Post newsPost : mListPosts) {
                if (newsPost.getDbid_post() == dbid_post) {
                    if (ifPlus) {
                        if (newsPost.getUser_choice() == 1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() - 1);
                            newsPost.setUser_choice(0);
                        } else if (newsPost.getUser_choice() == 0) {
                            newsPost.setTotalScore(newsPost.getTotalScore() + 1);
                            newsPost.setUser_choice(1);
                        } else if (newsPost.getUser_choice() == -1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() + 2);
                            newsPost.setUser_choice(1);
                        }
                    } else {
                        if (newsPost.getUser_choice() == 1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() - 2);
                            newsPost.setUser_choice(-1);

//                                        post.getDbid_post();
                        } else if (newsPost.getUser_choice() == 0) {
                            newsPost.setTotalScore(newsPost.getTotalScore() - 1);
                            newsPost.setUser_choice(-1);
                        } else if (newsPost.getUser_choice() == -1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() + 1);
                            newsPost.setUser_choice(0);
                        }
                    }
                }

            }
        } else {
            for (Post newsPost : mListExplorePosts) {
                if (newsPost.getDbid_post() == dbid_post) {
                    if (ifPlus) {
                        if (newsPost.getUser_choice() == 1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() - 1);
                            newsPost.setUser_choice(0);
                        } else if (newsPost.getUser_choice() == 0) {
                            newsPost.setTotalScore(newsPost.getTotalScore() + 1);
                            newsPost.setUser_choice(1);
                        } else if (newsPost.getUser_choice() == -1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() + 2);
                            newsPost.setUser_choice(1);
                        }
                    } else {
                        if (newsPost.getUser_choice() == 1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() - 2);
                            newsPost.setUser_choice(-1);

//                                        post.getDbid_post();
                        } else if (newsPost.getUser_choice() == 0) {
                            newsPost.setTotalScore(newsPost.getTotalScore() - 1);
                            newsPost.setUser_choice(-1);
                        } else if (newsPost.getUser_choice() == -1) {
                            newsPost.setTotalScore(newsPost.getTotalScore() + 1);
                            newsPost.setUser_choice(0);
                        }
                    }
                }
            }
        }


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

class NewsFeedPageAdapter extends FragmentStatePagerAdapter {
    int[] icons = {R.mipmap.ic_home_tab,
            R.mipmap.ic_follow_tab,
            R.mipmap.ic_hurt_tab,
            R.mipmap.ic_globe_tab
    };

    FragmentManager fragmentManager;
    private Map<Integer, Fragment> mPageReferenceMap;

    public NewsFeedPageAdapter(FragmentManager fm) {
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
                fragment = NewsFeedFragment.newInstance(TAB_HOME);
                mPageReferenceMap.put(position, fragment);
                break;
            case TAB_COMMENTS:
                fragment = FollowesFragment.newInstance(TAB_COMMENTS);
                mPageReferenceMap.put(position, fragment);
                break;
            case TAB_WEB:
                fragment = MyLikesFragment.newInstance(TAB_COMMENTS);
                mPageReferenceMap.put(position, fragment);
                break;
            case TAB_EXPLORE_POSTS:
                fragment = ExploreFeedsFragment.newInstance(TAB_EXPLORE_POSTS);
                mPageReferenceMap.put(position, fragment);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + (position + 1);
    }

    public int getIcon(int position) {
        return icons[position];
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
