package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import java.util.ArrayList;

/**
 * Created by Noam on 11/8/2015.
 */
public interface SubjectsLoadedListener {
    public void onAllSubjectsLoaded(ArrayList<Subject> listSubjects);
}
