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
import com.studentadvisor.noam.studentadvisor.activities.ExploreActivity;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterNewsFeed;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterPosts;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerNewsFeed;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.MarginDecoration;
import com.studentadvisor.noam.studentadvisor.extras.RecycleTouchListenerNewsFeed;
import com.studentadvisor.noam.studentadvisor.extras.RecycleTouchListenerPosts;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadComments;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadPosts;
import com.studentadvisor.noam.studentadvisor.tasks.TaskVotePost;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Noam on 12/19/2015.
 */
public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        PostsLoadedListener {
    private DialogCommunicatorNewsFeed mCommunicator;

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private AdapterNewsFeed rvAdapter;
    private ArrayList<Post> data = new ArrayList<>();
    private RelativeLayout mRoot;
    private String mTypePost;
    private boolean mIsRate;
    private String mUserUid = "0";

    private int rows_to_fetch = 10;
    private int start_from_row = 0;
    private static final String STATE_POSTS_NEWS_FEED = "state_posts_news_feed";
    private static final String STATE_ROWS_TO_FETCH = "rows_to_fetch";
    private static final String STATE_START_FROM_ROW = "start_from_row";
    private static final boolean FIRSTS_POSTS = true;
    private PostsLoadedListener mContextPostsListener;

    private static boolean isLoading = false;
    private boolean isFinishFetch = false;
    private boolean isFetching = false;
    private boolean resumeChecker;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsFeedFragment fragmentContext;

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

    public static NewsFeedFragment newInstance(int tab_num) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();

        if (tab_num == 0) {
            args.putInt(ARG_PARAM_TAB_NUM, tab_num);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public NewsFeedFragment() {
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

            mUserUid = user.get("uid");
        }

        mContextPostsListener = this;
        fragmentContext = this;
        resumeChecker = false;
//        rows_to_fetch = 10;
//        start_from_row = 0;

        if (savedInstanceState != null) {
            rows_to_fetch = savedInstanceState.getInt(STATE_ROWS_TO_FETCH);
            start_from_row = savedInstanceState.getInt(STATE_START_FROM_ROW);
        }

        //TODO: getFollowes List
        //   mDegree = mCommunicator.getDegree();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mRoot = (RelativeLayout) view.findViewById(R.id.myNewsFeedFragmentRoot);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeNewsFeedRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.myNewsFeedRecycle);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        rvAdapter = new AdapterNewsFeed(getActivity());
        //rvAdapter.setClickListener(this);

        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecycleTouchListenerNewsFeed(getActivity(),
                recyclerView,
                data.size() == 0 ? -1 : data.size() + 1,
                true,
                new ClickListenerNewsFeed() {
                    @Override
                    public void onClickPlusButton(View view, int position) {

                        Post post = data.get(position - 1);

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
                                if (mCommunicator.CheckEnabledInternet()) {
                                    new TaskVotePost(mContextPostsListener, user_uid, name, post.getDbid_post() + "", "1").execute();

                                    if (post.getUser_choice() == 1) {
                                        data.get(position - 1).setTotalScore(data.get(position - 1).getTotalScore() - 1);
                                        data.get(position - 1).setUser_choice(0);
                                    } else if (post.getUser_choice() == 0) {
                                        data.get(position - 1).setTotalScore(data.get(position - 1).getTotalScore() + 1);
                                        data.get(position - 1).setUser_choice(1);
                                    } else if (post.getUser_choice() == -1) {
                                        data.get(position - 1).setTotalScore(data.get(position - 1).getTotalScore() + 2);
                                        data.get(position - 1).setUser_choice(1);
                                    }

                                    rvAdapter.notifyDataSetChanged();
                                    mCommunicator.updatePostsActionButton(post.getDbid_post(), true, true);
                                }
                            } else {
                                // Log in Dialog
//                                Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                                if (mCommunicator != null) {
                                    mCommunicator.dialog_log_in();
                                }
                            }
                        } else {
                            //Problem
                        }
                    }

                    @Override
                    public void onClickMinusButton(View view, int position) {
                        Post post = data.get(position - 1);

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
                                if (mCommunicator.CheckEnabledInternet()) {

                                    new TaskVotePost(mContextPostsListener, user_uid, name, post.getDbid_post() + "", "-1").execute();

                                    if (post.getUser_choice() == 1) {
                                        data.get(position - 1).setTotalScore(data.get(position - 1).getTotalScore() - 2);
                                        data.get(position - 1).setUser_choice(-1);
                                    } else if (post.getUser_choice() == 0) {
                                        data.get(position - 1).setTotalScore(data.get(position - 1).getTotalScore() - 1);
                                        data.get(position - 1).setUser_choice(-1);
                                    } else if (post.getUser_choice() == -1) {
                                        data.get(position - 1).setTotalScore(data.get(position - 1).getTotalScore() + 1);
                                        data.get(position - 1).setUser_choice(0);
                                    }
                                    rvAdapter.notifyDataSetChanged();
                                    mCommunicator.updatePostsActionButton(post.getDbid_post(), false, true);
                                }
                            } else {
                                // Log in Dialog
//                                Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                                if (mCommunicator != null) {
                                    mCommunicator.dialog_log_in();
                                }
                            }
                        } else {
                            //Problem
                        }
                    }

                    @Override
                    public void onClickCommentButton(View view, int position) {
//                mCommunicator.dialog_post_comments();
                        Post post = data.get(position - 1);
                        if (post != null) {
                            if (mCommunicator.CheckEnabledInternet()) {
                                new TaskLoadComments(mCommunicator, String.valueOf(post.getDbid_post()), post, mTabFragmentNumber, position - 1).execute();
                            }
                        }

                    }

                    @Override
                    public void onClickFetchMoreButton(View view, int position) {
                        //TODO: maybe problem with position
                        if (mUserUid != null && mContextPostsListener != null && position > 0) {
                            if (!isLoading && !isFinishFetch && !isFetching && mCommunicator.CheckEnabledInternet()) {
                                isLoading = true;
                                isFetching = true;

                                new TaskLoadPosts(mContextPostsListener, !FIRSTS_POSTS,
                                        MAGIC_DBID_DEGREE, //type_post
                                        MAGIC_DBID_DEGREE, //dbid_degree
                                        mUserUid)
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
                        Post post = data.get(position - 1);
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

                                    if (mCommunicator.CheckEnabledInternet()) {
                                        Intent i = new Intent(getActivity(), DegreeActivity.class);
                                        i.putExtra(SELECTED_DEGREE, degree);
                                        startActivity(i);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onClickAddToFavoritesButton(View view, int position) {
                        Post post = data.get(position - 1);
                        if (post != null) {
                            mCommunicator.dialog_add_to_favorite(post);
                            //MyApplication.getWritableCommentsDatabase().insertComment(post, false);
                        }
                    }

                    @Override
                    public void onClickExploreHeaderButton(View view, int position) {
                        Intent intent = new Intent(getActivity(), ExploreActivity.class);
                        startActivity(intent);
                    }
                }));

        mProgressBar = (ProgressBar) view.findViewById(R.id.myNewsFeedProgressBar);

        if (mCommunicator != null && !mCommunicator.getListPost().isEmpty()){
            data = mCommunicator.getListPost();
            rvAdapter.setPosts(data);
            recyclerView.getAdapter().notifyItemRangeChanged(0, data.size() - 1);
        }
        else if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(STATE_POSTS_NEWS_FEED) != null) {
            data = savedInstanceState.getParcelableArrayList(STATE_POSTS_NEWS_FEED);
            rvAdapter.setPosts(data);
        }
        else {
            if (mUserUid != null) {
                if (mCommunicator.CheckEnabledInternet()) {
                    new TaskLoadPosts(this, FIRSTS_POSTS,
                            MAGIC_DBID_DEGREE, //type_post
                            MAGIC_DBID_DEGREE, // dbid_Degree
                            mUserUid)
                            .execute();
                }
            } else {
                L.m("Error - mUserUId is Null - NewsFeed");
            }
        }

        return view;

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
    public void onRefresh() {
        start_from_row = 0;
        setIsFinishFetch(false);
        if (mUserUid != null) {
            if (mCommunicator.CheckEnabledInternet()) {
                new TaskLoadPosts(this, FIRSTS_POSTS,
                        MAGIC_DBID_DEGREE, //post
                        MAGIC_DBID_DEGREE, //dbid_degree
                        mUserUid)
                        .execute();
            }
        } else {
            L.m("Error - Degree or TypePost not fetched to the fragment");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_POSTS_NEWS_FEED, data);
        outState.putInt(STATE_ROWS_TO_FETCH, rows_to_fetch);
        outState.putInt(STATE_START_FROM_ROW, start_from_row);
    }


    @Override
    public void makeSnackBarText(String text) {
        Snackbar.make(mRoot, text, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.text_dismiss), mSnackBarClickListener)
                .setActionTextColor(getResources().getColor(R.color.snack_bar_color))
                .show();
    }

    public void addCommentToPostList(int position) {
        data.get(position).setTotalComments(data.get(position).getTotalComments() + 1);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void clickPostManualy(int position) {
        Post post = data.get(position);
        if (post != null) {
            if (mCommunicator.CheckEnabledInternet()) {
                new TaskLoadComments(mCommunicator, String.valueOf(post.getDbid_post()), post, mTabFragmentNumber, position).execute();
            }
        }
    }


    //-------PostsLoadedListener

    @Override
    public void onPostsLoadedVolley(ArrayList<Post> listPosts) {
        //update the Adapter to contain the new movies downloaded from the web
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isFetching = false;

        if (!listPosts.isEmpty()) {
            data.clear();
            data.addAll(listPosts);

            rvAdapter.setPosts(data);
            recyclerView.getAdapter().notifyItemRangeChanged(0, data.size() - 1);

            if (mCommunicator != null) {
                mCommunicator.setListPosts(listPosts, false);
            }
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

        if (mCommunicator != null) {
            mCommunicator.setListPosts(listPosts, true);
        }

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

}

