package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.SubjectsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import java.util.ArrayList;

/**
 * Created by Noam on 12/18/2015.
 */
public class TaskAllFollowedDegrees extends AsyncTask<Void, Void, ArrayList<FollowObj>> {
    private DrawerLayoutCallBack myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String unique_id;


    public TaskAllFollowedDegrees(DrawerLayoutCallBack myComponent, String unique_id) {

        this.myComponent = myComponent;
        this.unique_id = unique_id;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected  ArrayList<FollowObj> doInBackground(Void... params) {

        ArrayList<FollowObj> listFollowedDegrees = DegreeUtils.loadAllFollowedDegrees(requestQueue,
                unique_id);
        return listFollowedDegrees;
    }

    @Override
    protected void onPostExecute( ArrayList<FollowObj> followedDegrees) {
        if (myComponent != null) {
            myComponent.onGetFollowedDegrees(followedDegrees);
        }
    }


}

