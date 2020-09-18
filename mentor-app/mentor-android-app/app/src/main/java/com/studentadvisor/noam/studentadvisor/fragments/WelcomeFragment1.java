package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;

/**
 * Created by Noam on 12/26/2015.
 */
public class WelcomeFragment1 extends Fragment {

    final static String LAYOUT_ID = "layoutId";
    final static String USER_NAME = "userName";
    final static String IF_BABY_CHOSEN = "ifbabyChosen";
    private static final String STATE_IF_BABY = "if_baby_token";

    private WelcomeScreenDialogCommunicator mCommunicator;
    private ImageButton babyImage, studentImage;
    private TextView mTVName;
    private String mName;
    private Button btnContinue;
    private boolean ifChosen = true;
    private boolean ifBaby = true;
    private int layoutID = 0;
    private ImageButton exitWelcomeButton;

    public static WelcomeFragment1 newInstance(int layoutId, String user_name, boolean ifBaby) {
        WelcomeFragment1 pane = new WelcomeFragment1();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
        bundle.putString(USER_NAME, user_name);
        bundle.putBoolean(IF_BABY_CHOSEN, ifBaby);
        pane.setArguments(bundle);
        return pane;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getInt(LAYOUT_ID) != 0) {
                layoutID = getArguments().getInt(LAYOUT_ID);
            }
            if (getArguments().getString(USER_NAME) != null) {
                mName = getArguments().getString(USER_NAME);
            }

            ifBaby = getArguments().getBoolean(IF_BABY_CHOSEN);

        }

        if (savedInstanceState != null){
            ifBaby = savedInstanceState.getBoolean(STATE_IF_BABY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_fragment_1, container, false);

        babyImage = (ImageButton)view.findViewById(R.id.babyImage);
        studentImage = (ImageButton)view.findViewById(R.id.studentImage);
        btnContinue = (Button)view.findViewById(R.id.btnContinue);
        exitWelcomeButton = (ImageButton)view.findViewById(R.id.exitWelcomeButton);
        mTVName = (TextView) view.findViewById(R.id.name);
        mTVName.setText("Welcome, " + mName);
        studentImage.setPadding(16, 16, 16, 16);
        babyImage.setPadding(16, 16, 16, 16);

        if (ifBaby) {
            if (!babyImage.isPressed()) {
                babyImage.setPressed(true);
            }
        }
        else{
            if (!studentImage.isPressed()) {
                studentImage.setPressed(true);
            }
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifChosen) {
                    if (mCommunicator != null) {
                        if (ifBaby) {
                            mCommunicator.continueToNext(layoutID + 1);
                        } else {
                            mCommunicator.continueToNext(layoutID);
                        }
                    }
                } else {
                    //Toast. need to choose
                }
            }
        });

        babyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifBaby = true;
                if (mCommunicator != null){
                    mCommunicator.setIfBaby(ifBaby);
                }
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    babyImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_clicked));
                    studentImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_normal));
                } else {
                    babyImage.setBackground(getResources().getDrawable(R.drawable.border_button_clicked));
                    studentImage.setBackground(getResources().getDrawable(R.drawable.border_button_normal));

                }

                studentImage.setPadding(16,16,16,16);
                babyImage.setPadding(16,16,16,16);
            }
        });

        studentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifBaby = false;
                if (mCommunicator != null){
                    mCommunicator.setIfBaby(ifBaby);
                }
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    studentImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_clicked));
                    babyImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_button_normal));
                } else {
                    studentImage.setBackground(getResources().getDrawable(R.drawable.border_button_clicked));
                    babyImage.setBackground(getResources().getDrawable(R.drawable.border_button_normal));
                }
                studentImage.setPadding(16,16,16,16);
                babyImage.setPadding(16,16,16,16);
            }
        });

        exitWelcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommunicator != null){
                    mCommunicator.backToLoginActivity();
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCommunicator = (WelcomeScreenDialogCommunicator) activity;
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
        outState.putBoolean(STATE_IF_BABY, ifBaby);
    }
}
