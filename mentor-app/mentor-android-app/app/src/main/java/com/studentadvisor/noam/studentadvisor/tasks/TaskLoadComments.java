package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicator;
import com.studentadvisor.noam.studentadvisor.callbacks.DialogCommunicatorNewsFeed;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 12/11/2015.
 */
public class TaskLoadComments extends AsyncTask<Void, Void, ArrayList<Comment>> {
    private DialogCommunicator myComponent;
    private DialogCommunicatorNewsFeed myComponentNewsFeed;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String uid_post;
    private Post mPost;
    private int tab, position;

    public TaskLoadComments(DialogCommunicator myComponent, String uid_post, Post post, int tab, int position) {
        this.uid_post = uid_post;
        this.myComponent = myComponent;
        this.mPost = post;
        this.tab = tab;
        this.position = position;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public TaskLoadComments(DialogCommunicatorNewsFeed myComponent, String uid_post, Post post, int tab, int position) {
        this.uid_post = uid_post;
        this.myComponentNewsFeed = myComponent;
        this.mPost = post;
        this.tab = tab;
        this.position = position;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ArrayList<Comment> doInBackground(Void... params) {

        ArrayList<Comment> listComments = DegreeUtils.loadAllComments(requestQueue,
                uid_post);
        return listComments;
    }


    @Override
    protected void onPostExecute(ArrayList<Comment> listComments) {
        if (myComponent != null) {
            myComponent.dialog_post_comments(listComments, mPost, tab, position);

        }
        if (myComponentNewsFeed != null) {
            myComponentNewsFeed.dialog_post_comments(listComments, mPost, tab, position);

        }
//        myComponent.setProgressBarGone();
//        myComponent.setIsLoadingFalse();
    }

}
