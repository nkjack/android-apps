package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 1/22/2016.
 */
public class TaskLoadExplorePosts extends AsyncTask<Void, Void, ArrayList<Post>> {
    private PostsLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private boolean if_first;
    private String unique_id;


    public TaskLoadExplorePosts(PostsLoadedListener myComponent, boolean if_first, String userUid) {
        this.if_first = if_first;
        this.unique_id = userUid;
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ArrayList<Post> doInBackground(Void... params) {
        ArrayList<Post> listPosts = new ArrayList<>();
        listPosts = DegreeUtils.loadAllExplorePosts(requestQueue,
                myComponent.getRows_to_fetch(),
                myComponent.getStart_from_row(),
                unique_id);

        return listPosts;
    }

    @Override
    protected void onPostExecute(ArrayList<Post> listPosts) {
        if (myComponent != null) {
            L.m("News Feed size " + listPosts.size() + "");
            if (if_first) {
                myComponent.onPostsLoadedVolley(listPosts);
                if (listPosts != null && listPosts.size() > 0) {
                    myComponent.setStart_from_row(myComponent.getStart_from_row() + listPosts.size());
                } else {
                    myComponent.setIsFinishFetch(true);
                }
            } else {
                myComponent.onMorePostsLoadedVolley(listPosts);
                L.m("size " + listPosts.size());
                if (listPosts != null && listPosts.size() > 0) {
                    myComponent.setStart_from_row(myComponent.getStart_from_row() + listPosts.size());
                } else {
                    myComponent.setIsFinishFetch(true);
//                    myComponent.setStart_from_row(0);
                }
            }

            myComponent.setProgressBarGone();
            myComponent.setIsLoadingFalse();
        }


    }

}

