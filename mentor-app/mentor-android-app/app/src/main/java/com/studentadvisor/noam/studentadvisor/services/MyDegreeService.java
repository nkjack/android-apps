package com.studentadvisor.noam.studentadvisor.services;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.SubjectsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import java.util.ArrayList;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by Noam on 1/13/2016.
 */
public class MyDegreeService extends JobService {

    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        L.t(this, "onStartJobDegree");
        L.m("onStartJobDegree");
        this.jobParameters = jobParameters;
        new TaskLoadDegrees().execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        L.t(this, "onStopJob");
        return false;
    }


    //    @Override
//    public void onBoxOfficeMoviesLoaded(ArrayList<Movie> listMovies) {
//        L.t(this, "onBoxOfficeMoviesLoaded");
//        jobFinished(jobParameters, false);
//    }
    private class TaskLoadDegrees extends AsyncTask<Void, Void, ArrayList<Degree>> {
        private VolleySingleton volleySingleton;
        private RequestQueue requestQueue;


        public TaskLoadDegrees() {
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
            if (listDegrees.isEmpty()) {
                jobFinished(jobParameters, true);
            }
            else{
                jobFinished(jobParameters, false);
            }
        }

    }


}
