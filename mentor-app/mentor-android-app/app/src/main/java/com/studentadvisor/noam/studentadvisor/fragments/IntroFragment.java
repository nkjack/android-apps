package com.studentadvisor.noam.studentadvisor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.greenfrvr.hashtagview.HashtagView;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.Tags.Transformers;
import com.studentadvisor.noam.studentadvisor.activities.LoginActivity;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noam on 1/12/2016.
 */
public class IntroFragment extends Fragment {

    final static String LAYOUT_ID = "layoutId";
    private static final String PAGE = "page";

    private int mPage;
    public static IntroFragment newInstance(int layoutId, int page) {
        IntroFragment pane = new IntroFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
        bundle.putInt(PAGE, page);

        pane.setArguments(bundle);
        return pane;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_ID, -1), container, false);
        root.setTag(mPage);

        if (getArguments().getInt(LAYOUT_ID, -1) == R.layout.intro_fragment_1){
            //TAGS GROUP
            HashtagView mTagGroup = (HashtagView) root.findViewById(R.id.intro_tag_search);
            final List<String> list = new ArrayList<String>();
            list.add("מדעי המחשב");
            mTagGroup.setData(list, Transformers.HASH);

            HashtagView mTagGroupSubjects = (HashtagView) root.findViewById(R.id.intro_tag_subject_group);
            final List<String> listSubjects = new ArrayList<String>();
            listSubjects.add("מדעים מדויקים");
            listSubjects.add("מדעי המוח");
            listSubjects.add("מדעי החיים");
            listSubjects.add("הנדסה");
            listSubjects.add("מדעי הטבע");
            listSubjects.add("מדעי החברה");

            mTagGroupSubjects.setData(listSubjects, Transformers.HASH);

            HashtagView mTagGroupSchools = (HashtagView) root.findViewById(R.id.intro_tag_school_group);
            final List<String> listSchools = new ArrayList<String>();
            listSchools.add("הטכניון - מכון טכנולוגי לישראל");
            listSchools.add("האוניברסיטה הפתוחה");
            listSchools.add("המרכז הבינתחומי הרצליה");
            listSchools.add("אוניברסיטה חיפה");
            listSchools.add("המכללה האקדמית ספיר");
            listSchools.add("אוניברסיטת תל אביב");
            listSchools.add("האוניברסיטה העברית ירושלים");
            listSchools.add("אוניברסיטת בן-גוריון בנגב");
            listSchools.add("אוניברסיטת בר-אילן");

            mTagGroupSchools.setData(listSchools, Transformers.HASH);
        }
        else if (getArguments().getInt(LAYOUT_ID, -1) == R.layout.intro_fragment_0){
            Button skipButton = (Button)root.findViewById(R.id.skipButton);
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    getActivity().finish();
                }
            });
        }

        return root;
    }
}