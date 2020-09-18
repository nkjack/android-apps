package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.activities.DegreeActivity;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.MarginDecoration;
import com.studentadvisor.noam.studentadvisor.extras.RecycleTouchListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadComments;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadPosts;
import com.studentadvisor.noam.studentadvisor.tasks.TaskVotePost;

import java.util.ArrayList;
import java.util.HashMap;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;

/**
 * Created by Noam on 12/19/2015.
 */
public class MyFavoritesFragment extends Fragment implements  PostsLoadedListener {
    private DialogCommunicatorNewsFeed mCommunicator;

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private AdapterNewsFeed rvAdapter;
    private ArrayList<Post> data = new ArrayList<>();
    private RelativeLayout mRoot;
    private String mTypePost;
    private boolean mIsRate;
    private String mUserUid = "0";

    private static final String STATE_POSTS_FAVORITES = "state_posts_favorites";
    private static final boolean FIRSTS_POSTS = true;
    private PostsLoadedListener mContextPostsListener;

    private boolean resumeChecker;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyFavoritesFragment fragmentContext;

    private int mTabFragmentNumber = 0;

    private static final String ARG_PARAM_TYPE_POST = "type_post_arg";
    private static final String ARG_PARAM_IS_RATE = "is_rate_arg";
    private static final String ARG_PARAM_TAB_NUM = "tab_number_degree";
    private static final String MAGIC_DBID_DEGREE = "-999";


    private View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public static MyFavoritesFragment newInstance( int tab_num) {
        MyFavoritesFragment fragment = new MyFavoritesFragment();
        Bundle args = new Bundle();

        if (tab_num == 0){
            args.putInt(ARG_PARAM_TAB_NUM, tab_num);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public MyFavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getInt(ARG_PARAM_TAB_NUM) != 0) {
                mTabFragmentNumber = getArguments().getInt(ARG_PARAM_TAB_NUM);
            }

        }

        // SqLite database handler
        SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

        // session manager
        SessionManager session = new SessionManager(MyApplication.getAppContext());
        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            mUserUid= user.get("uid");
        }

        mContextPostsListener = this;
        fragmentContext = this;
        resumeChecker = false;

        //TODO: getFollowes List
        //   mDegree = mCommunicator.getDegree();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mRoot = (RelativeLayout) view.findViewById(R.id.myNewsFeedFragmentRoot);

        recyclerView = (RecyclerView) view.findViewById(R.id.myNewsFeedRecycle);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        rvAdapter = new AdapterNewsFeed(getActivity());
        //rvAdapter.setClickListener(this);

        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecycleTouchListenerPosts(getActivity(),
                recyclerView,
                data.size() == 0 ? -1 : data.size() + 1,
                true,
                new ClickListenerPosts() {
                    @Override
                    public void onClickPlusButton(View view, int position) {

                        Post post = data.get(position);

                        if ( post != null ) {
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
                                new TaskVotePost(mContextPostsListener, user_uid, name, post.getDbid_post() + "", "1").execute();

                                if (post.getUser_choice() == 1) {
                                    data.get(position).setTotalScore(data.get(position).getTotalScore() - 1);
                                    data.get(position).setUser_choice(0);
                                }
                                else if (post.getUser_choice() == 0) {
                                    data.get(position).setTotalScore(data.get(position).getTotalScore() + 1);
                                    data.get(position).setUser_choice(1);
                                }
                                else if (post.getUser_choice() == -1){
                                    data.get(position).setTotalScore(data.get(position).getTotalScore() + 2);
                                    data.get(position).setUser_choice(1);
                                }
                                rvAdapter.notifyDataSetChanged();
                            }
                            else{
                                // Log in Dialog
//                                Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                                if (mCommunicator != null){
                                    mCommunicator.dialog_log_in();
                                }
                            }
                        }
                        else{
                            //Problem
                        }
                    }

                    @Override
                    public void onClickMinusButton(View view, int position) {
                        Post post = data.get(position);

                        if ( post != null ) {
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
                                new TaskVotePost(mContextPostsListener, user_uid, name, post.getDbid_post()+"","-1").execute();

                                if (post.getUser_choice() == 1) {
                                    data.get(position).setTotalScore(data.get(position).getTotalScore() - 2);
                                    data.get(position).setUser_choice(-1);
                                }
                                else if (post.getUser_choice() == 0) {
                                    data.get(position).setTotalScore(data.get(position).getTotalScore() - 1);
                                    data.get(position).setUser_choice(-1);
                                }
                                else if (post.getUser_choice() == -1){
                                    data.get(position).setTotalScore(data.get(position).getTotalScore() + 1);
                                    data.get(position).setUser_choice(0);
                                }

                                rvAdapter.notifyDataSetChanged();
                            }
                            else{
                                // Log in Dialog
//                                Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                                if (mCommunicator != null){
                                    mCommunicator.dialog_log_in();
                                }
                            }
                        }
                        else{
                            //Problem
                        }
                    }

                    @Override
                    public void onClickCommentButton(View view, int position) {
//                mCommunicator.dialog_post_comments();
                        Post post = data.get(position);
                        if (post != null){
                            new TaskLoadComments(mCommunicator, String.valueOf(post.getDbid_post()), post,mTabFragmentNumber, position).execute();
                        }

                    }

                    @Override
                    public void onClickFetchMoreButton(View view, int position) {
                        if ( mUserUid != null && mContextPostsListener != null && position > 0) {


                        }
                        else{
                            L.m("Error - Degree or TypePost not fetched to the fragment");
                        }

                    }

                    @Override
                    public void onClickDegreeTitleButton(View view, int position) {
                        Post post = data.get(position);
                        if (post != null){
                            if(post.getDegree_id() != 0) {
                                String sql_statment = " " + COLUMN_ID_DEGREE + " = " + post.getDegree_id() + "";
                                ArrayList<Degree> mListDegrees = new ArrayList<>();

                                mListDegrees = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, sql_statment, true);
                                //if the database is empty, trigger an AsycnTask to download movie list from the web
                                if (mListDegrees.isEmpty()) {
                                    // need to handle it
                                }
                                else{
                                    Degree degree = mListDegrees.get(0);

                                    mCommunicator.CheckEnabledInternet();
                                    Intent i = new Intent(getActivity(), DegreeActivity.class);
                                    i.putExtra(SELECTED_DEGREE, degree);
                                    startActivity(i);
                                }
                            }
                        }
                    }

                    @Override
                    public void onClickAddToFavoritesButton(View view, int position) {
                        Post post = data.get(position);
                        if (post != null){
                            mCommunicator.dialog_add_to_favorite(post);
                            //MyApplication.getWritableCommentsDatabase().insertComment(post, false);
                        }
                    }
                }));

        mProgressBar = (ProgressBar) view.findViewById(R.id.myNewsFeedProgressBar);
        if(savedInstanceState!= null && savedInstanceState.getParcelableArrayList(STATE_POSTS_FAVORITES)!= null){
            data = savedInstanceState.getParcelableArrayList(STATE_POSTS_FAVORITES);
            rvAdapter.setPosts(data);
        }
        else {
            if (mUserUid != null) {
                readPosts();
            }
            else{
                L.m("Error - mUserUId is Null - NewsFeed");
            }
        }

        return view;

    }


    private void readPosts(){
        ArrayList<Post> listPosts = new ArrayList<Post>();
        listPosts = MyApplication.getWritableCommentsDatabase().readPosts(Util.checkIfLoggedIn().getUser_id());

        if (listPosts.size() > 0){
            data.clear();
            data.addAll(listPosts);

            rvAdapter.setPosts(data);
//            recyclerView.getAdapter().notifyItemRangeChanged(0, data.size() - 1);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        else{
            //TODO other title
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCommunicator = (DialogCommunicatorNewsFeed) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogCommunicator");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_POSTS_FAVORITES, data);
    }


    @Override
    public void makeSnackBarText(String text) {
        Snackbar.make(mRoot, text, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.text_dismiss), mSnackBarClickListener)
                .setActionTextColor(getResources().getColor(R.color.snack_bar_color))
                .show();
    }

    public void addCommentToPostList(int position){
        data.get(position).setTotalComments(data.get(position).getTotalComments() + 1);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void clickPostManualy(int position){
        Post post = data.get(position);
        if (post != null){
            new TaskLoadComments(mCommunicator, String.valueOf(post.getDbid_post()), post,mTabFragmentNumber, position).execute();
        }
    }


    //-------PostsLoadedListener

    @Override
    public void onPostsLoadedVolley(ArrayList<Post> listPosts) {
        //update the Adapter to contain the new movies downloaded from the web
    }

    @Override
    public void onMorePostsLoadedVolley(ArrayList<Post> listPosts) {
        //update the Adapter to contain the new movies downloaded from the web
    }

    @Override
    public void setProgressBarVisible() {mProgressBar.setVisibility(View.VISIBLE);}
    @Override
    public void setProgressBarGone() {mProgressBar.setVisibility(View.GONE);}
    @Override
    public int getRows_to_fetch() {return 0; }
    @Override
    public void setRows_to_fetch(int num) {}
    @Override
    public int getStart_from_row() {return 0;}
    @Override
    public void setStart_from_row(int num) {}
    @Override
    public void setIsLoadingTrue() {}
    @Override
    public void setIsLoadingFalse() {}
    @Override
    public void setFetchingFalse() {}
    @Override
    public void setIsFinishFetch(boolean temp){}

}

