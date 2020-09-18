package com.studentadvisor.noam.studentadvisor.services;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
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
public class MySubjectsService extends JobService {

    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        L.t(this, "onStartJobSubjects");
        L.m("onStartJobSubjects");
        this.jobParameters = jobParameters;
        new TaskLoadSubjects().execute();
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


    private class TaskLoadSubjects extends AsyncTask<Void, Void, ArrayList<Subject>> {
        private VolleySingleton volleySingleton;
        private RequestQueue requestQueue;

        public TaskLoadSubjects() {

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
            if (listSubjects.isEmpty()) {
                jobFinished(jobParameters, true);
            }
            else{
                jobFinished(jobParameters, false);
            }
        }

    }
}

