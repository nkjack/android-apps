package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.activities.DegreeActivity;
import com.studentadvisor.noam.studentadvisor.activities.NewPostActivity;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterDegrees;
import com.studentadvisor.noam.studentadvisor.adapters.AdapterHomeDegree;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreeHomeTabLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.ClickListenerHomeTabDegree;
import com.studentadvisor.noam.studentadvisor.extras.Constants;
import com.studentadvisor.noam.studentadvisor.extras.MarginDecoration;
import com.studentadvisor.noam.studentadvisor.extras.RecyclerTouchListenerHomeTabDegree;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskFollowDegree;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLikeDegree;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadPosts;

import java.util.HashMap;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_NEW_POST_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_WRITE_POST_TYPE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_NEW_POST_EXTRA_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.USER_ID;

/**
 * Created by Noam on 11/28/2015.
 */
public class HomeDegreeFragment extends Fragment implements DegreeHomeTabLoadedListener,
        SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private DialogCommunicator mCommunicator;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_EXTRA_INFO = "extra_info";
    private static final String ARG_DEGREE = "degree";

    //the adapter responsible for displaying our degree within a RecyclerView
    private AdapterHomeDegree mAdapter;
    //the recyclerview containing showing all our degrees
    private RecyclerView mRecycleExtraHome;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Degree mDegree;
    private ExtraInfoDegree mExtraInfoDegree;

    private DegreeHomeTabLoadedListener mContextDegreeHomeTabListener;


    public static HomeDegreeFragment newInstance(Degree degree, ExtraInfoDegree extraInfoDegree) {
        HomeDegreeFragment fragment = new HomeDegreeFragment();
        Bundle args = new Bundle();

        if (degree != null && extraInfoDegree != null) {
            args.putParcelable(ARG_DEGREE, degree);
            args.putParcelable(ARG_EXTRA_INFO, extraInfoDegree);
        }
        fragment.setArguments(args);

        return fragment;
    }

    public HomeDegreeFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getParcelable(ARG_DEGREE) != null) {
                mDegree = getArguments().getParcelable(ARG_DEGREE);
            }
            if (getArguments().getParcelable(ARG_EXTRA_INFO) != null) {
                mExtraInfoDegree = getArguments().getParcelable(ARG_EXTRA_INFO);
            }
        }

        mContextDegreeHomeTabListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_degree, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.homeTabProgressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#112C4B"), PorterDuff.Mode.MULTIPLY);
        //mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#303F9F"), PorterDuff.Mode.MULTIPLY);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeHomeTabRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setProgressBarVisible();

        mRecycleExtraHome = (RecyclerView) view.findViewById(R.id.homeDegreeRV);
        mRecycleExtraHome.addItemDecoration(new MarginDecoration(getActivity()));

        mAdapter = new AdapterHomeDegree(getActivity());
        mRecycleExtraHome.setAdapter(mAdapter);
        mRecycleExtraHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (mExtraInfoDegree != null && mDegree != null) {
            mAdapter.setExtraInfoDegree(mExtraInfoDegree, mDegree);
            setProgressBarGone();
        }

        mRecycleExtraHome.addOnItemTouchListener(new RecyclerTouchListenerHomeTabDegree(getActivity(), mRecycleExtraHome, new ClickListenerHomeTabDegree() {
            @Override
            public void onClickLikeButton(View view, int position) {

                if (mDegree != null && mExtraInfoDegree != null) {
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

                            new TaskLikeDegree(mContextDegreeHomeTabListener, MyApplication.getAppContext(), user_uid, name).execute(mDegree);

                            if (mExtraInfoDegree.isIfLiked()) {
                                mAdapter.changeNumLikes(-1);
                                mExtraInfoDegree.setIfLiked(false);
                                mDegree.setLikes(mDegree.getLikes() - 1);
                            } else {
                                mAdapter.changeNumLikes(1);
                                mExtraInfoDegree.setIfLiked(true);
                                mDegree.setLikes(mDegree.getLikes() + 1);
                            }

                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Log in Dialog
//                        Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                        if (mCommunicator != null){
                            mCommunicator.dialog_log_in();
                        }
                    }
                } else {
                    //Problem
                }
            }

            @Override
            public void onClickAskButton(View view, int position) {
//                Toast.makeText(getActivity(), "ASK ACTION", Toast.LENGTH_SHORT).show();
                if (mDegree != null && mExtraInfoDegree != null) {
                    // SqLite database handler
                    SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                    // session manager
                    SessionManager session = new SessionManager(MyApplication.getAppContext());
                    if (session.isLoggedIn()) {
                        // Fetching user details from sqlite
                        HashMap<String, String> user = db.getUserDetails();

                        String name = user.get("name");
                        String user_uid = user.get("uid");

                        if (mCommunicator.CheckEnabledInternet()) {
                            Intent i = new Intent(getActivity(), NewPostActivity.class);
                            i.putExtra(PARCEL_NEW_POST_DEGREE, mDegree);
                            i.putExtra(PARCEL_NEW_POST_EXTRA_DEGREE, mExtraInfoDegree);
                            i.putExtra(PARCEL_WRITE_POST_TYPE, "ask");
                            startActivity(i);
                        }
                    }
                    else {
                        // Log in Dialog
//                        Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                        if (mCommunicator != null){
                            mCommunicator.dialog_log_in();
                        }
                    }

                } else {
                    //Problem
                }
            }

            @Override
            public void onClickRateButton(View view, int position) {
//                Toast.makeText(getActivity(), "RATE ACTION", Toast.LENGTH_SHORT).show();
                if (mDegree != null && mExtraInfoDegree != null) {
                    // SqLite database handler
                    SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                    // session manager
                    SessionManager session = new SessionManager(MyApplication.getAppContext());
                    if (session.isLoggedIn()) {
                        // Fetching user details from sqlite
                        HashMap<String, String> user = db.getUserDetails();

                        String name = user.get("name");
                        String user_uid = user.get("uid");

                        if (!mExtraInfoDegree.isIfPostRate()) {
                            if (mCommunicator.CheckEnabledInternet()) {
                                Intent i = new Intent(getActivity(), NewPostActivity.class);
                                i.putExtra(PARCEL_NEW_POST_DEGREE, mDegree);
                                i.putExtra(PARCEL_NEW_POST_EXTRA_DEGREE, mExtraInfoDegree);
                                i.putExtra(PARCEL_WRITE_POST_TYPE, "rate");
                                startActivity(i);
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "You already Rated This Degree", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Log in Dialog
//                        Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                        if (mCommunicator != null){
                            mCommunicator.dialog_log_in();
                        }
                    }

                } else {
                    //Problem
                }
            }

            @Override
            public void onClickGenericAsk(View view, int position) {
//                Toast.makeText(getActivity(), "GENERIC ACTION", Toast.LENGTH_SHORT).show();

                if (mDegree != null && mExtraInfoDegree != null) {
                    // SqLite database handler
                    SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());

                    // session manager
                    SessionManager session = new SessionManager(MyApplication.getAppContext());
                    if (session.isLoggedIn()) {
                        // Fetching user details from sqlite
                        HashMap<String, String> user = db.getUserDetails();

                        String name = user.get("name");
                        String user_uid = user.get("uid");

                        if (mCommunicator.CheckEnabledInternet()){
                            Intent i = new Intent(getActivity(), NewPostActivity.class);
                            i.putExtra(PARCEL_NEW_POST_DEGREE, mDegree);
                            i.putExtra(PARCEL_NEW_POST_EXTRA_DEGREE, mExtraInfoDegree);
                            i.putExtra(PARCEL_WRITE_POST_TYPE, "rate");
                            startActivity(i);
                        }

                    } else {
                        // Log in Dialog
//                        Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                        if (mCommunicator != null){
                            mCommunicator.dialog_log_in();
                        }
                    }

                } else {
                    //Problem
                }


            }

            @Override
            public void onClickFollowButton(View view, int position) {
                if (mDegree != null && mExtraInfoDegree != null) {
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
                            new TaskFollowDegree(mContextDegreeHomeTabListener, MyApplication.getAppContext(), user_uid, name).execute(mDegree);

                            if (mExtraInfoDegree.isIfFollowed()) {
                                mAdapter.changeNumFollows(-1);
                                mExtraInfoDegree.setIfFollowed(false);
                                mDegree.setFollowes(mDegree.getFollowes() - 1);
                            } else {
                                mAdapter.changeNumFollows(1);
                                mExtraInfoDegree.setIfFollowed(true);
                                mDegree.setFollowes(mDegree.getFollowes() + 1);
                            }

                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Log in Dialog
//                        Toast.makeText(getActivity(), "Need to Log In..", Toast.LENGTH_SHORT).show();
                        if (mCommunicator != null){
                            mCommunicator.dialog_log_in();
                        }
                    }
                } else {
                    //Problem
                }
            }
        }));
//        mAdapter
//        mRecycleExtraHome.setAdapter(mAdapter);
//        mRecycleExtraHome.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
        //        WebView mWebPage = (WebView) view.findViewById(R.id.webViewPage);
//        mWebPage.loadUrl("http://portal.idc.ac.il/he/main/admissions/undergraduate/pages/undergraduateprograms.aspx");
    }

    public void setExtraInfo(ExtraInfoDegree extraInfo, Degree degree) {
        mDegree = degree;
        mExtraInfoDegree = extraInfo;
        if (mExtraInfoDegree != null && mDegree != null) {
            mAdapter.setExtraInfoDegree(mExtraInfoDegree, mDegree);
            mRecycleExtraHome.setAdapter(mAdapter);
            mRecycleExtraHome.setLayoutManager(new LinearLayoutManager(getActivity()));
            setProgressBarGone();
            mRecycleExtraHome.setVisibility(View.VISIBLE);
        }

        stopRefresh();
    }

    public void setProgressBarVisible() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarGone() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLikeLoaded() {
        MyApplication.getWritableDatabase().degreeSetLikes(DBDegrees.ALL_DEGREES, mDegree.getLikes(), String.valueOf(mDegree.getDbid_degree()));
    }

    @Override
    public void onFollowLoaded() {
        MyApplication.getWritableDatabase().degreeSetFollowes(DBDegrees.ALL_DEGREES, mDegree.getFollowes(), String.valueOf(mDegree.getDbid_degree()));
    }

    @Override
    public void onRefresh() {
        if (mRecycleExtraHome.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (mDegree != null && mCommunicator != null) {
            if (mCommunicator.CheckEnabledInternet()) {
                mCommunicator.startTaskDegreeExtra();
            }
        } else {
            L.m("Error - Home Tab not refeshed to the fragment");
            if (mRecycleExtraHome.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.GONE);
//                mEmergencyLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public void stopRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}

