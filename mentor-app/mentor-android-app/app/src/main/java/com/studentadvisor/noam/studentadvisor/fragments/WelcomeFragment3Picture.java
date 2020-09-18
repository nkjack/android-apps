package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;

/**
 * Created by Noam on 12/26/2015.
 */
public class WelcomeFragment3Picture extends Fragment implements View.OnClickListener{

    final static String LAYOUT_ID = "layoutId";
    private static final String KEY_PARCEL_BITMAP = "bitmap_parcel";

    private WelcomeScreenDialogCommunicator mCommunicator;
    private Button btnChoose, btnUpload, btnFinish;
    private ImageView mProfileImage;
    private int layoutID = 0;
    private Bitmap mBitmap;



    public static WelcomeFragment3Picture newInstance(int layoutId) {
        WelcomeFragment3Picture pane = new WelcomeFragment3Picture();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_fragment_3, container, false);

        btnChoose = (Button) view.findViewById(R.id.buttonChoose);
        btnUpload = (Button) view.findViewById(R.id.buttonUpload);
        btnFinish = (Button) view.findViewById(R.id.btnContinue);
        mProfileImage = (ImageView) view.findViewById(R.id.imageView);

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        if (savedInstanceState != null){
            mBitmap = savedInstanceState.getParcelable(KEY_PARCEL_BITMAP);
            if (mBitmap != null) {
                mProfileImage.setImageBitmap(mBitmap);
            }
            else{
                mProfileImage.setImageResource(R.drawable.profile_big_blue);
            }
        }

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
    public void onClick(View v) {
        if (v == btnChoose) {
//            showFileChooser();
            mCommunicator.showFileChooser();
        }
        if(v == btnUpload){
//            uploadImage();
            if (mBitmap != null) {
                mCommunicator.uploadImage();
            }
            else{
                Toast.makeText(getActivity(), "Please, Choose a Picture", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == btnFinish){
            mCommunicator.finishButton();
//            viewImage();
        }
    }

    public void setProfileImage(Bitmap bmp){
        mBitmap = bmp;
        if (mProfileImage != null){
            mProfileImage.setImageBitmap(bmp);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBitmap != null) {
            outState.putParcelable(KEY_PARCEL_BITMAP, mBitmap);
        }
    }
}

