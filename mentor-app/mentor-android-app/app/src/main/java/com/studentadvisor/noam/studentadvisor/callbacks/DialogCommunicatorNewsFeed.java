package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 12/19/2015.
 */
public interface DialogCommunicatorNewsFeed {
    public void dialog_post_comments(ArrayList<Comment> listComments, Post post, int tab, int position);
    public void onPostComment(int success, int tab, int position);
    public void dialog_add_to_favorite(Post post);
    public boolean CheckEnabledInternet();
    public void dialog_log_in();
    public void setListPosts(ArrayList<Post> listPosts, boolean fetch);
    public ArrayList<Post> getListPost();
    public void setListExplorePosts(ArrayList<Post> listPosts, boolean fetch);
    public ArrayList<Post> getListExplorePost();
    public ArrayList<Degree> getListLikes();
    public ArrayList<Degree> getListFollows();
    public void updatePostsActionButton(int dbid_post, boolean ifPlus, boolean ifNewsFeed);
}
