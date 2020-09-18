package com.studentadvisor.noam.studentadvisor.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.ExtraInfoDegreeLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.PostsLoadedListener;
import com.studentadvisor.noam.studentadvisor.extras.DegreeUtils;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 12/6/2015.
 */
public class TaskLoadPosts extends AsyncTask<Void, Void, ArrayList<Post>> {
    private PostsLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private boolean if_first;
    private String type_post, dbid_degree, unique_id;
    private String sql_query;


    public TaskLoadPosts(PostsLoadedListener myComponent, boolean if_first, String type_post, String dbid_degree, String userUid)
    {
        this.type_post = type_post;
        this.dbid_degree = dbid_degree;
        this.if_first = if_first;
        this.unique_id = userUid;
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public TaskLoadPosts(PostsLoadedListener myComponent, boolean if_first, String type_post, String dbid_degree, String userUid, String sql_query)
    {
        this.type_post = type_post;
        this.dbid_degree = dbid_degree;
        this.if_first = if_first;
        this.unique_id = userUid;
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.sql_query = sql_query;
    }


    @Override
    protected ArrayList<Post> doInBackground(Void... params) {
        ArrayList<Post> listPosts = new ArrayList<>();
        if (sql_query == null) {
            listPosts = DegreeUtils.loadAllPosts(requestQueue,
                    myComponent.getRows_to_fetch(),
                    myComponent.getStart_from_row(),
                    type_post,
                    dbid_degree,
                    unique_id);
        }
        else{
            listPosts = DegreeUtils.loadAllFavoritePosts(requestQueue,
                    myComponent.getRows_to_fetch(),
                    myComponent.getStart_from_row(),
                    unique_id,
                    sql_query);
        }
        return listPosts;
    }

    @Override
    protected void onPostExecute(ArrayList<Post> listPosts) {
        if (myComponent != null) {
        L.m("News Feed size " + listPosts.size() + "");
            if (if_first) {
                myComponent.onPostsLoadedVolley(listPosts);
                if (listPosts != null && listPosts.size()>0){
                    myComponent.setStart_from_row(myComponent.getStart_from_row() + listPosts.size());
                }
                else{
                    myComponent.setIsFinishFetch(true);
                }
            }
            else {
                myComponent.onMorePostsLoadedVolley(listPosts);
                L.m("size " + listPosts.size());
                if (listPosts != null && listPosts.size()>0){
                    myComponent.setStart_from_row(myComponent.getStart_from_row() + listPosts.size());            }
                else{
                    myComponent.setIsFinishFetch(true);
//                    myComponent.setStart_from_row(0);
                }
            }
        }
        myComponent.setProgressBarGone();
        myComponent.setIsLoadingFalse();
    }

}
