package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.activities.DegreeActivity;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterDegrees;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerDegreeCard;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.MarginDecoration;
import com.studentadvisor.noam.studentadvisor.extras.RecycleTouchListenerPosts;
import com.studentadvisor.noam.studentadvisor.extras.RecyclerTouchListenerDegreeCard;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.User;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskAllFollowedDegrees;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadComments;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadPosts;
import com.studentadvisor.noam.studentadvisor.tasks.TaskVotePost;

import java.util.ArrayList;
import java.util.HashMap;

import static com.studentadvisor.noam.studentadvisor.extras.Constants.STATE_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_ID_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.LIST_SELECTED_SUBJECTS;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;

/**
 * Created by Noam on 12/21/2015.
 */
public class FollowesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private DialogCommunicatorNewsFeed mCommunicator;
    private DrawerLayoutCallBack mDrawerLayoutCallback;
    private LinearLayout mEmergencyLayout;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private AdapterDegrees rvAdapter;
    private ArrayList<Degree> mListDegrees = new ArrayList<>();
    private RelativeLayout mRoot;
    private String mUserUid = "0";
    private String mSqlStatment = "";

    private static final boolean FIRSTS_POSTS = true;

    private static boolean isLoading = false;
    private boolean isFinishFetch = false;
    private boolean isFetching = false;
    private boolean resumeChecker;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FollowesFragment fragmentContext;

    private int mTabFragmentNumber = 0;

    private static final String ARG_PARAM_TAB_NUM = "tab_number_degree";


    public static FollowesFragment newInstance(int tab_num) {
        FollowesFragment fragment = new FollowesFragment();
        Bundle args = new Bundle();

        if (tab_num == 0) {
            args.putInt(ARG_PARAM_TAB_NUM, tab_num);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public FollowesFragment() {
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

        fragmentContext = this;
        resumeChecker = false;

        //TODO: getFollowes List
        //   mDegree = mCommunicator.getDegree();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumeChecker == false) {
            resumeChecker = true;
        } else {
//            onRefresh();
        }
//        onRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_followes, container, false);
        mEmergencyLayout = (LinearLayout) view.findViewById(R.id.emergencyLayout);
        mRoot = (RelativeLayout) view.findViewById(R.id.myFollowesFragmentRoot);

        recyclerView = (RecyclerView) view.findViewById(R.id.listAllFollowes);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        mProgressBar = (ProgressBar) view.findViewById(R.id.myFollowesProgressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#112C4B"), PorterDuff.Mode.MULTIPLY);

        setProgressBarVisible();

        rvAdapter = new AdapterDegrees(getActivity());
        //rvAdapter.setClickListener(this);

        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListenerDegreeCard(getActivity(), recyclerView, new ClickListenerDegreeCard() {
            @Override
            public void onClick(View view, int position) {
                if (rvAdapter.getDegree(position) != null) {
                    Degree degree = rvAdapter.getDegree(position);

                    if (mCommunicator.CheckEnabledInternet()) {
                        Intent i = new Intent(getActivity(), DegreeActivity.class);
                        i.putExtra(SELECTED_DEGREE, degree);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onClickLike(View view, int position) {
                if (rvAdapter.getDegree(position) != null) {
                    Degree degree = rvAdapter.getDegree(position);

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
//                        new TaskLikeDegree(getApplicationContext(), user_uid, name).execute(degree);
                    }


                }
            }
        }));

        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            mListDegrees = savedInstanceState.getParcelableArrayList(STATE_DEGREE);
            rvAdapter.setDegrees(mListDegrees);

        } else if (mCommunicator != null && !mCommunicator.getListFollows().isEmpty()){
            mListDegrees = mCommunicator.getListFollows();
            rvAdapter.setDegrees(mListDegrees);
            recyclerView.getAdapter().notifyItemRangeChanged(0, mListDegrees.size() - 1);
        }
        else {
            User user = Util.checkIfLoggedIn();
            if (user != null && user.isLoggedIn() && mDrawerLayoutCallback != null) {
                if (mCommunicator.CheckEnabledInternet()) {
                    new TaskAllFollowedDegrees(mDrawerLayoutCallback, user.getUser_id()).execute();
                }
            } else {
//                Toast.makeText(getActivity(), "Log In Issues", Toast.LENGTH_SHORT).show();
            }

        }

        //update your Adapter to containg the retrieved movies
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeFollowesRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (mListDegrees != null && !mListDegrees.isEmpty()){
            setProgressBarGone();
            setEmergencyLayoutGoneRecyclerVisible();
        }
        else{
            setProgressBarGone();
            setEmergencyLayoutVisibleRecyclerGone();
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
        try {
            mDrawerLayoutCallback = (DrawerLayoutCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DrawerLayoutCallBack");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
        mDrawerLayoutCallback = null;
    }


    @Override
    public void onRefresh() {
        if (recyclerView.getVisibility() == View.GONE){
            mProgressBar.setVisibility(View.VISIBLE);
            mEmergencyLayout.setVisibility(View.GONE);
        }
        User user = Util.checkIfLoggedIn();
        if (user != null && user.isLoggedIn() && mDrawerLayoutCallback != null) {
            if (mCommunicator.CheckEnabledInternet()) {
                new TaskAllFollowedDegrees(mDrawerLayoutCallback, user.getUser_id()).execute();
            }
        } else {
//            Toast.makeText(getActivity(), "Log In Issues", Toast.LENGTH_SHORT).show();
            if (recyclerView.getVisibility() == View.GONE){
                mProgressBar.setVisibility(View.GONE);
                mEmergencyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_DEGREE, mListDegrees);
    }

    public void setmListDegrees(ArrayList<Degree> list) {
        if (list != null) {
            mListDegrees = list;
            rvAdapter.setDegrees(mListDegrees);
            recyclerView.setAdapter(rvAdapter);
            setProgressBarGone();
            setEmergencyLayoutGoneRecyclerVisible();
            stopRefresh();
        }
    }

    public void stopRefresh(){
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        setProgressBarGone();
    }

    public void setProgressBarVisible(){mProgressBar.setVisibility(View.VISIBLE);}
    public void setProgressBarGone(){mProgressBar.setVisibility(View.GONE);}
    public void setEmergencyLayoutVisibleRecyclerGone(){mEmergencyLayout.setVisibility(View.VISIBLE); recyclerView.setVisibility(View.GONE);}
    public void setEmergencyLayoutGoneRecyclerVisible(){mEmergencyLayout.setVisibility(View.GONE); recyclerView.setVisibility(View.VISIBLE);}


}


