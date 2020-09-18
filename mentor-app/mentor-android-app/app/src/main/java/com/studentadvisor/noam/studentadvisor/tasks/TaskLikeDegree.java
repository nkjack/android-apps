package com.studentadvisor.noam.studentadvisor.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreeHomeTabLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Noam on 11/25/2015.
 */
public class TaskLikeDegree extends AsyncTask<Degree, Void, Integer> {
    private DegreeHomeTabLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String user_id;
    private String user_name;
    private Context mContext;
    private String mDbidDegree;

    public TaskLikeDegree(DegreeHomeTabLoadedListener myComponent, Context context ,String user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.myComponent = myComponent;
        this.mContext = context;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Integer doInBackground(Degree... params) {
        int if_success = -1;
        Degree degree = params[0];
        String degree_id = String.valueOf(degree.getDbid_degree());
        mDbidDegree = degree_id;

        if (degree_id != null && user_id != null && user_name != null) {
            if_success = DegreeUtils.pressLikeDegree(requestQueue,
                    degree_id,
                    user_id,
                    user_name);
        }

        L.m("if success : " + if_success);
        Integer toReturn = Integer.valueOf(if_success);
        return toReturn;
    }


    @Override
    protected void onPostExecute(Integer success) {
        if (success != null) {
            if (success.intValue() == 1) {
                // successfully created product
//                Toast.makeText(mContext, "Like", Toast.LENGTH_SHORT).show();
//                MyApplication.getWritableDatabase().likeDegree(DBDegrees.ALL_DEGREES, dbid_degree, true);

                if (myComponent != null) {
                    myComponent.onLikeLoaded();
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
            } else {
                // failed to create product
                if (myComponent != null) {
//                    myComponent.makeSnackBarText("Sorry, Please try to Refresh BNS");
                }
            }
        }
//        if (myComponent != null) {
//            myComponent.onAllDegreesLoaded(if_success);
//        }
    }


}
