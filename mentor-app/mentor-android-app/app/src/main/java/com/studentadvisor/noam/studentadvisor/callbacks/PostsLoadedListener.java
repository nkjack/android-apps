package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 12/6/2015.
 */
public interface PostsLoadedListener {
    public void onPostsLoadedVolley(ArrayList<Post> listPosts);
    public void onMorePostsLoadedVolley(ArrayList<Post> listPosts);
    public void setProgressBarVisible();
    public void setProgressBarGone();

    public int getRows_to_fetch();
    public void setRows_to_fetch(int num) ;

    public int getStart_from_row();
    public void setStart_from_row(int num) ;

    public void setIsLoadingTrue();
    public void setIsLoadingFalse();

    public void setFetchingFalse();

    public void setIsFinishFetch(boolean temp);
    public void makeSnackBarText(String text);

}
