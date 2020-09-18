package com.studentadvisor.noam.studentadvisor.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;

/**
 * Created by Noam on 12/6/2015.
 */
public class TaskVotePost extends AsyncTask<Void, Void, Integer> {
    private PostsLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String uid_user;
    private String user_name;
    private String uid_post;
    private String vote_decision;
    private Context mContext;

    public TaskVotePost(PostsLoadedListener myComponent,
                        String uid_user,
                        String user_name,
                        String uid_post,
                        String vote_decision) {
        this.uid_user = uid_user;
        this.user_name = user_name;
        this.uid_post = uid_post;
        this.vote_decision = vote_decision;
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Integer doInBackground(Void... params) {
        int if_success = -1;


        if (uid_post != null && uid_user != null && user_name != null && vote_decision != null) {
            if_success = DegreeUtils.pressVotePost(requestQueue,
                    uid_user,
                    user_name,
                    uid_post,
                    vote_decision);
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
                if (myComponent != null) {
//                    myComponent.onVoteUpdated(position);
                    myComponent.makeSnackBarText("Vote");
                }
            } else {
                // failed to create product
                if (myComponent != null) {
                    myComponent.makeSnackBarText("Failed Voted");
                }
            }
        }
//        if (myComponent != null) {
//            myComponent.onAllDegreesLoaded(if_success);
//        }
    }


}
