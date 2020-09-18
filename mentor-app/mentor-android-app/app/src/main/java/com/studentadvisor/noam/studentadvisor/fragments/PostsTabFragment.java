package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.activities.DegreeActivity;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterPosts;
import com.studentadvisor.noam.studentadvisor.adapters.SearchAdapter;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.ExtraInfoDegreeLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
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
import com.studentadvisor.noam.studentadvisor.tasks.TaskLikeDegree;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadComments;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadPosts;
import com.studentadvisor.noam.studentadvisor.tasks.TaskVotePost;

import java.util.ArrayList;
import java.util.HashMap;


public class PostsTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        PostsLoadedListener {
    private DialogCommunicator mCommunicator;

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private AdapterPosts rvAdapter;
    private ArrayList<Post> data = new ArrayList<>();
    private RelativeLayout mRoot;
    private Degree mDegree;
    private String mTypePost;
    private boolean mIsRate;
    private String mUserUid = "0";

    private int rows_to_fetch = 10;
    private int start_from_row = 0;
    private static final String STATE_POSTS = "state_posts";
    private static final String STATE_ROWS_TO_FETCH = "rows_to_fetch";
    private static final String STATE_START_FROM_ROW = "start_from_row";
    private static final boolean FIRSTS_POSTS = true;
    private PostsLoadedListener mContextPostsListener;

    private static boolean isLoading = false;
    private boolean isFinishFetch = false;
    private boolean isFetching = false;
    private boolean resumeChecker;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PostsTabFragment fragmentContext;

    private int mTabFragmentNumber = 0;

    private static final String ARG_PARAM_TYPE_POST = "type_post_arg";
    private static final String ARG_PARAM_IS_RATE = "is_rate_arg";
    private static final String ARG_PARAM_TAB_NUM = "tab_number_degree";


    private View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public static PostsTabFragment newInstance(String type_post, boolean is_rate, int tab_num) {
        PostsTabFragment fragment = new PostsTabFragment();
        Bundle args = new Bundle();

        if (type_post != null) {
            args.putString(ARG_PARAM_TYPE_POST, type_post);
            args.putBoolean(ARG_PARAM_IS_RATE, is_rate);
            args.putInt(ARG_PARAM_TAB_NUM, tab_num);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public PostsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString(ARG_PARAM_TYPE_POST) != null) {
                mTypePost = getArguments().getString(ARG_PARAM_TYPE_POST);
                mIsRate = getArguments().getBoolean(ARG_PARAM_IS_RATE);
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
        mDegree = mCommunicator.getDegree();

        if (savedInstanceState != null){
            rows_to_fetch = savedInstanceState.getInt(STATE_ROWS_TO_FETCH);
            start_from_row = savedInstanceState.getInt(STATE_START_FROM_ROW);
        }
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
        View view = inflater.inflate(R.layout.fragment_posts_tab, container, false);
        mRoot = (RelativeLayout) view.findViewById(R.id.myPostsRoot);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeAllPostsRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.myPostsRecycle);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        rvAdapter = new AdapterPosts(getActivity());
        rvAdapter.setIS_RATING(mIsRate);
        //rvAdapter.setClickListener(this);

        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecycleTouchListenerPosts(getActivity(),
                recyclerView,
                data.size() == 0 ? -1 : data.size() + 1,
                false,
                new ClickListenerPosts() {
                    @Override
                    public void onClickPlusButton(View view, int position) {

                        Post post = data.get(position);

                        if (mDegree != null && post != null) {
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
//                                Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                                if (mCommunicator != null){
                                    mCommunicator.dialog_log_in();
                                }
                            }
                        } else {
                            //Problem
                        }
                    }

                    @Override
                    public void onClickMinusButton(View view, int position) {
                        Post post = data.get(position);

                        if (mDegree != null && post != null) {
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
//                                Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                                if (mCommunicator != null){
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
                        Post post = data.get(position);
                        if (post != null) {
                            if (mCommunicator.CheckEnabledInternet()) {
                                new TaskLoadComments(mCommunicator, String.valueOf(post.getDbid_post()), post, mTabFragmentNumber, position).execute();
                            }
                        }

                    }

                    @Override
                    public void onClickFetchMoreButton(View view, int position) {
                        if (mDegree != null && mTypePost != null && mContextPostsListener != null && position > 0) {
                            if (!isLoading && !isFinishFetch && !isFetching && mCommunicator.CheckEnabledInternet()) {
                                isLoading = true;
                                isFetching = true;

                                new TaskLoadPosts(mContextPostsListener, !FIRSTS_POSTS,
                                        mTypePost,
                                        String.valueOf(mDegree.getDbid_degree()),
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
                        //nothing
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

        mProgressBar = (ProgressBar) view.findViewById(R.id.myPostsProgressBar);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(STATE_POSTS) != null) {
            data = savedInstanceState.getParcelableArrayList(STATE_POSTS);
            rvAdapter.setPosts(data);
        } else {
            if (mDegree != null && mTypePost != null) {
                if (mCommunicator.CheckEnabledInternet()) {
                    new TaskLoadPosts(this, FIRSTS_POSTS,
                            mTypePost,
                            String.valueOf(mDegree.getDbid_degree()),
                            mUserUid)
                            .execute();
                }
            } else {
                L.m("Error - Degree or TypePost not fetched to the fragment");
            }
        }

        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCommunicator = (DialogCommunicator) activity;
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
        if (mDegree != null && mTypePost != null) {
            if (mCommunicator.CheckEnabledInternet()) {
                new TaskLoadPosts(this, FIRSTS_POSTS,
                        mTypePost,
                        String.valueOf(mDegree.getDbid_degree()),
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
        outState.putParcelableArrayList(STATE_POSTS, data);
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
            new TaskLoadComments(mCommunicator, String.valueOf(post.getDbid_post()), post, mTabFragmentNumber, position).execute();
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

        data.clear();
        data.addAll(listPosts);

        rvAdapter.setPosts(data);
        recyclerView.getAdapter().notifyItemRangeChanged(0, data.size() - 1);
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
