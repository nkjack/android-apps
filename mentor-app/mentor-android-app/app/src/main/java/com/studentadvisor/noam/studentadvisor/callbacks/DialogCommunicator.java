package com.studentadvisor.noam.studentadvisor.callbacks;

import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;

import java.util.ArrayList;

/**
 * Created by Noam on 12/6/2015.
 */
public interface DialogCommunicator {
    public Degree getDegree();
    public void dialog_post_comments(ArrayList<Comment> listComments, Post post, int tab, int position);
    public void onPostComment(int success, int tab, int position);
    public void startTaskDegreeExtra();
    public void dialog_add_to_favorite(Post post);
    public boolean CheckEnabledInternet();
    public void dialog_log_in();

}
