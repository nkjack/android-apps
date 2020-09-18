package com.studentadvisor.noam.studentadvisor.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;

/**
 * Created by Noam on 12/26/2015.
 */
public class WelcomeFragmentSt2 extends Fragment {

    final static String LAYOUT_ID = "layoutId";

    private WelcomeScreenDialogCommunicator mCommunicator;
    private RadioGroup mRadioGroup;
    private Button btnContinue;
    private String yearStudying = "שנה א";
    private int layoutID = 0;
    private TextView degreeTV1,degreeTV2,degreeTV3;
    private Button btnAdd;
    private ImageButton dltBtn1,dltBtn2,dltBtn3;

    public static WelcomeFragmentSt2 newInstance(int layoutId) {
        WelcomeFragmentSt2 pane = new WelcomeFragmentSt2();
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
        View view = inflater.inflate(R.layout.welcome_fragment_st_2, container, false);

        degreeTV1 = (TextView) view.findViewById(R.id.degreeTv1);
        degreeTV2 = (TextView) view.findViewById(R.id.degreeTv2);
        degreeTV3 = (TextView) view.findViewById(R.id.degreeTv3);
        btnAdd = (Button) view.findViewById(R.id.addBtn);
        dltBtn1 = (ImageButton) view.findViewById(R.id.dltBtn1);
        dltBtn2 = (ImageButton) view.findViewById(R.id.dltBtn2);
        dltBtn3 = (ImageButton) view.findViewById(R.id.dltBtn3);

        setUpButtons();

        mRadioGroup = (RadioGroup) view.findViewById(R.id.RGYear);
        setRadioGroup();
        btnContinue = (Button)view.findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearStudying != null) {
                    if (mCommunicator != null) {
                        mCommunicator.continueToNext(layoutID);
                    }
                } else {
                    //Toast. need to choose
                }
            }
        });

        return view;
    }

    private void setUpButtons() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommunicator != null){
                    mCommunicator.loadToolBarSearch();
                }
            }
        });
        dltBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degreeTV1.setText("תואר להוספה");
                if (mCommunicator != null){
                    mCommunicator.deleteDegreeStudying(1);
                }
//                notifyAll();
            }
        });
        dltBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degreeTV2.setText("תואר להוספה");
                if (mCommunicator != null){
                    mCommunicator.deleteDegreeStudying(2);
                }
//                notifyAll();
            }
        });

        dltBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degreeTV3.setText("תואר להוספה");
                if (mCommunicator != null){
                    mCommunicator.deleteDegreeStudying(3);
                }
//                notifyAll();
            }
        });
    }

    private void setRadioGroup() {
        /* Attach CheckedChangeListener to radio group */
        mRadioGroup.check(R.id.firstYearButton);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
//                    Toast.makeText(SettingsActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    yearStudying = rb.getText().toString();
                    int year = 1;
                    switch (yearStudying){
                        case "שנה א":
                            year = 1;
                            break;
                        case "שנה ב":
                            year = 2;
                            break;
                        case "שנה ג":
                            year = 3;
                            break;
                        case "שנה ד":
                            year = 4;
                            break;
                        case "שנה ה":
                            year = 5;
                            break;
                        case "שנה ו":
                            year = 6;
                            break;
                        case "שנה ז":
                            year = 7;
                            break;
                        case "דיי כבר":
                            year = 99;
                            break;
                    }
                    mCommunicator.setYearsStudying(year);
                }

            }
        });
    }

    public void setTextView(Degree degreeName){
        if (degreeTV1.getText().toString().contentEquals("תואר להוספה")) {
            degreeTV1.setText(degreeName.getDegree_name() + " ב" + degreeName.getSchool_name_he());
            mCommunicator.setDegreeStudying(1, degreeName);
        }
        else if (degreeTV2.getText().toString().contentEquals("תואר להוספה")) {
            degreeTV2.setText(degreeName.getDegree_name() + " ב" + degreeName.getSchool_name_he());
            mCommunicator.setDegreeStudying(2, degreeName);
        }
        else if(degreeTV3.getText().toString().contentEquals("תואר להוספה")) {
            degreeTV3.setText(degreeName.getDegree_name() + " ב" + degreeName.getSchool_name_he());
            mCommunicator.setDegreeStudying(3, degreeName);
        }
        else{

        }
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

