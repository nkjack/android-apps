package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;

import java.util.ArrayList;

/**
 * Created by Noam on 11/7/2015.
 */
public class TaskLoadDegrees extends AsyncTask<Void, Void, ArrayList<Degree>> {
    private DegreesLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;


    public TaskLoadDegrees(DegreesLoadedListener myComponent) {

        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ArrayList<Degree> doInBackground(Void... params) {

        ArrayList<Degree> listDegrees = DegreeUtils.loadAllDegrees(requestQueue);
        return listDegrees;
    }

    @Override
    protected void onPostExecute(ArrayList<Degree> listDegrees) {
        if (myComponent != null) {
            myComponent.onAllDegreesLoaded(listDegrees);
        }
    }


}
