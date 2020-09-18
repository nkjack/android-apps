package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;

/**
 * Created by Noam on 1/6/2016.
 */
public class ConfirmWelcomeDialogFragment extends DialogFragment {

    private ImageButton exitButton;
    private ImageView profileImage, typeUserImage;
    private TextView nameTV, degreeTV1, degreeTV2, degreeTV3, yearsStudy;
    private WelcomeScreenDialogCommunicator mCommunicator;
    private Button confirmButton;


    private static final String NAME_DIALOG_PARAM = "nameDialogParam";
    private static final String PROFILE_IMAGE_DIALOG_PARAM = "profileImageDialogParam";
    private static final String TYPE_USER_IMAGE_DIALOG_PARAM = "typeUserImageDialogParam";
    private static final String IF_BABY_BOOLEAN_DIALOG_PARAM = "ifBabyBooleanDialogParam";
    private static final String YEARS_STUDY_DIALOG_PARAM = "yearsStudy";
    private static final String DEGREE_1_DIALOG_PARAM = "degree1DialogParam";
    private static final String DEGREE_2_DIALOG_PARAM = "degree2DialogParam";
    private static final String DEGREE_3_DIALOG_PARAM = "degree3DialogParam";
    private static final String USER_TYPE_STR_DIALOG_PARAM ="userTypeStringDialogParam";

    private String profileNameSTR, yearsStudySTR, degree1STR, degree2STR, degree3STR;
    private boolean ifBaby;
    private Bitmap mProfileImageBitmap;
    private String userTypeSTR;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_welcome_login, null);

        exitButton = (ImageButton) view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //setCancelable(false); must be true when setCanceledOnTouchOutside is called
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);


        profileImage = (ImageView)view.findViewById(R.id.profileImage);
        typeUserImage = (ImageView)view.findViewById(R.id.imageTypeUser);
        nameTV = (TextView) view.findViewById(R.id.textViewProfileName);
        yearsStudy = (TextView) view.findViewById(R.id.yearStudyTV);
        degreeTV1 = (TextView) view.findViewById(R.id.degreeStudy1);
        degreeTV2 = (TextView) view.findViewById(R.id.degreeStudy2);
        degreeTV3 = (TextView) view.findViewById(R.id.degreeStudy3);
        confirmButton = (Button)view.findViewById(R.id.confirmButton);

        if (getArguments() != null){
            profileNameSTR = getArguments().getString(NAME_DIALOG_PARAM);
            yearsStudySTR = getArguments().getString(YEARS_STUDY_DIALOG_PARAM);
            degree1STR = getArguments().getString(DEGREE_1_DIALOG_PARAM);
            degree2STR = getArguments().getString(DEGREE_2_DIALOG_PARAM);
            degree3STR = getArguments().getString(DEGREE_3_DIALOG_PARAM);

            mProfileImageBitmap = getArguments().getParcelable(PROFILE_IMAGE_DIALOG_PARAM);
            ifBaby = getArguments().getBoolean(IF_BABY_BOOLEAN_DIALOG_PARAM);
            userTypeSTR = getArguments().getString(USER_TYPE_STR_DIALOG_PARAM);
        }
        else{
            getDialog().dismiss();
        }

        nameTV.setText(profileNameSTR);

        if (mProfileImageBitmap != null){
            profileImage.setImageBitmap(mProfileImageBitmap);
        }

        if (ifBaby){
            typeUserImage.setImageResource(R.drawable.baby_welcome_logo);
        }
        else{
            typeUserImage.setImageResource(R.drawable.student_welcome_logo);
        }

        if (!ifBaby){
            yearsStudy.setText("השנה ה- "+ yearsStudySTR + " שלי");
            yearsStudy.setVisibility(View.VISIBLE);

            if (!degree1STR.contentEquals("")){
                degreeTV1.setText(degree1STR);
                degreeTV1.setVisibility(View.VISIBLE);
            }
            if (!degree2STR.contentEquals("")){
                degreeTV2.setText(degree2STR);
                degreeTV2.setVisibility(View.VISIBLE);
            }
            if (!degree3STR.contentEquals("")){
                degreeTV3.setText(degree3STR);
                degreeTV3.setVisibility(View.VISIBLE);
            }
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommunicator != null){
                    mCommunicator.finishConfirmDialog(userTypeSTR);
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
}