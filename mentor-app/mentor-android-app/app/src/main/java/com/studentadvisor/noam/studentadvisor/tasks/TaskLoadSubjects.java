package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.SubjectsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import java.util.ArrayList;

/**
 * Created by Noam on 11/8/2015.
 */
public class TaskLoadSubjects extends AsyncTask<Void, Void, ArrayList<Subject>> {
    private SubjectsLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;


    public TaskLoadSubjects(SubjectsLoadedListener myComponent) {

        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ArrayList<Subject> doInBackground(Void... params) {

        ArrayList<Subject> listSubjects = DegreeUtils.loadAllSubjects(requestQueue);
        return listSubjects;
    }

    @Override
    protected void onPostExecute(ArrayList<Subject> listSubjects) {
        if (myComponent != null) {
            myComponent.onAllSubjectsLoaded(listSubjects);
        }
    }

}
