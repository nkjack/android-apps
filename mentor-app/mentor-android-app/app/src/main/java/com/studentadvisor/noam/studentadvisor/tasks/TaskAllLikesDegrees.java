package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DrawerLayoutCallBack;
import com.studentadvisor.noam.studentadvisor.callbacks.SubjectsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;

import java.util.ArrayList;

/**
 * Created by Noam on 12/18/2015.
 */
public class TaskAllLikesDegrees extends AsyncTask<Void, Void, ArrayList<LikeObj>> {
    private DrawerLayoutCallBack myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String unique_id;


    public TaskAllLikesDegrees(DrawerLayoutCallBack myComponent, String unique_id) {

        this.myComponent = myComponent;
        this.unique_id = unique_id;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ArrayList<LikeObj> doInBackground(Void... params) {

        ArrayList<LikeObj> listLikedDegrees = DegreeUtils.loadAllLikedDegrees(requestQueue,
                unique_id);
        return listLikedDegrees;
    }

    @Override
    protected void onPostExecute(ArrayList<LikeObj> likedDegrees) {
        if (myComponent != null) {
            myComponent.onGetLikedDegrees(likedDegrees);
        }
    }


}
