package com.studentadvisor.noam.studentadvisor.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreeNewPostLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;

/**
 * Created by Noam on 12/5/2015.
 */
public class TaskInsertNewPost extends AsyncTask<Degree, Void, Integer> {
    private DegreeNewPostLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String user_id;
    private String user_name;
    private String body_content;
    private String rate;
    private String type_post;
    private Context mContext;

    public TaskInsertNewPost(DegreeNewPostLoadedListener myComponent,
                             String user_id,
                             String user_name,
                             String body_content,
                             String rate,
                             String type_post) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.body_content = body_content;
        this.rate = rate;
        this.type_post = type_post;
        this.myComponent = myComponent;
        this.mContext = (Context) myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Integer doInBackground(Degree... params) {
        int if_success = -1;
        Degree degree = params[0];
        String degree_id = String.valueOf(degree.getDbid_degree());


        if (degree_id != null && user_id != null && user_name != null && body_content != null &&
                rate != null && type_post != null) {
            if_success = DegreeUtils.insertNewPost(requestQueue,
                    degree_id,
                    user_id,
                    body_content,
                    rate,
                    type_post);
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
                Toast.makeText(mContext, "POSTED", Toast.LENGTH_SHORT).show();
                if (myComponent != null) {
                    myComponent.onInsertNewPostListener(success);
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
            } else {
                // failed to create product
                Toast.makeText(mContext, "FAILED POSTED", Toast.LENGTH_SHORT).show();
                if (myComponent != null) {
                    myComponent.onInsertNewPostListener(success);
//                    myComponent.makeSnackBarText("Sorry, Please try to Refresh BNS");
                }
            }
        }
//        if (myComponent != null) {
//            myComponent.onAllDegreesLoaded(if_success);
//        }
    }
}
