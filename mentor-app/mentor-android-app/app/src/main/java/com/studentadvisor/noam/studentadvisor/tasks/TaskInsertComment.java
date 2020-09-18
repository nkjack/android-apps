package com.studentadvisor.noam.studentadvisor.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreeNewPostLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

/**
 * Created by Noam on 12/15/2015.
 */
public class TaskInsertComment extends AsyncTask<Post, Void, Integer> {
    private DialogCommunicator myComponent;
    private DialogCommunicatorNewsFeed myComponentNewsFeed;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String user_id;
    private String user_name;
    private String body_content;
    private Context mContext;
    private int tab, position;

    public TaskInsertComment(DialogCommunicator myComponent,
                             String user_id,
                             String user_name,
                             String body_content,
                             int tab,
                             int position) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.body_content = body_content;
        this.myComponent = myComponent;
        this.mContext = (Context) myComponent;
        this.position = position;
        this.tab = tab;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }
    public TaskInsertComment(DialogCommunicatorNewsFeed myComponent,
                             String user_id,
                             String user_name,
                             String body_content,
                             int tab,
                             int position) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.body_content = body_content;
        this.myComponentNewsFeed = myComponent;
        this.mContext = (Context) myComponent;
        this.position = position;
        this.tab = tab;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Integer doInBackground(Post... params) {
        int if_success = -1;
        Post post = params[0];
        String uid_post = String.valueOf(post.getDbid_post());


        if (uid_post != null && user_id != null && user_name != null && body_content != null) {
            if_success = DegreeUtils.insertComment(requestQueue,
                    uid_post,
                    user_id,
                    user_name,
                    body_content);
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
//                Toast.makeText(mContext, "POSTED", Toast.LENGTH_SHORT).show();
                if (myComponent != null) {
                    myComponent.onPostComment(success, tab, position);
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
                if (myComponentNewsFeed != null) {
                    myComponentNewsFeed.onPostComment(success, tab, position);
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
            } else {
                // failed to create product
//                Toast.makeText(mContext, "FAILED POSTED", Toast.LENGTH_SHORT).show();
                if (myComponent != null) {
                    myComponent.onPostComment(success, tab, position);
//                    myComponent.makeSnackBarText("Sorry, Please try to Refresh BNS");
                }
                if (myComponentNewsFeed != null) {
                    myComponentNewsFeed.onPostComment(success, tab, position);
//                    myComponent.makeSnackBarText("Good Job, Keep Going!");
                }
            }
        }
//        if (myComponent != null) {
//            myComponent.onAllDegreesLoaded(if_success);
//        }
    }
}
