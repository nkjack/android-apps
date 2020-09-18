package com.studentadvisor.noam.studentadvisor.extras;

import com.android.volley.RequestQueue;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.json.Endpoints;
import com.studentadvisor.noam.studentadvisor.json.Parser;
import com.studentadvisor.noam.studentadvisor.json.Requestor;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.School;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Noam on 11/7/2015.
 */
public class DegreeUtils {
    public static ArrayList<Degree> loadAllDegrees(RequestQueue requestQueue) {
        JSONObject response = Requestor.requestRegularJSON(requestQueue, Endpoints.getRequestUrlAllDegrees(30));
        ArrayList<Degree> listDegrees = Parser.parseDegreesJSON(response);
        if (!listDegrees.isEmpty()) {
            MyApplication.getWritableDatabase().insertDegrees(DBDegrees.ALL_DEGREES, listDegrees, true);
        }
        else{
            L.m("loadAllDegrees - list is Empty");
        }
        return listDegrees;
    }

    public static ArrayList<Subject> loadAllSubjects(RequestQueue requestQueue) {
        JSONObject response = Requestor.requestRegularJSON(requestQueue, Endpoints.getRequestUrlAllSubjects(30));
        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);

        if (!listSubjects.isEmpty()) {
            MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        }
        else{
            L.m("loadAllSubjects - list is empty");
        }
//        DBDegrees dbDegrees = new DBDegrees(MyApplication.getAppContext());
//        dbDegrees.insertDegrees(dbDegrees, DBDegrees.ALL_DEGREES, listDegrees, false);
        return listSubjects;
    }

    public static ArrayList<Post> loadAllPosts(RequestQueue requestQueue,
                                            int amount,
                                            int fromRow,
                                            String type_post,
                                            String dbid_degree,
                                           String unique_id) {
        String url = dbid_degree.contentEquals("-999") ?
                Endpoints.getRequestUrlAllPostsNewsFeed(30):
                Endpoints.getRequestUrlAllPosts(30);

        JSONObject response = Requestor.requestAllPostsJSON(requestQueue,
                /*Endpoints.getRequestUrlAllPosts(30),*/
                url,
                amount,
                fromRow,
                type_post,
                dbid_degree,
                unique_id);
        ArrayList<Post> listPosts = Parser.parsePostsJSON(response);
        //MyApplication.getWritableDatabase().insertVotes(DBVotes.BOX_OFFICE, listVotes, true);
        return listPosts;
    }

    public static ArrayList<Post> loadAllExplorePosts(RequestQueue requestQueue,
                                                       int amount,
                                                       int fromRow,
                                                       String unique_id) {

        JSONObject response = Requestor.requestAllExplorePostsJSON(requestQueue,
                Endpoints.getRequestUrlAllExplorePosts(30),
                amount,
                fromRow,
                unique_id);
        ArrayList<Post> listPosts = Parser.parsePostsJSON(response);
        //MyApplication.getWritableDatabase().insertVotes(DBVotes.BOX_OFFICE, listVotes, true);
        return listPosts;
    }

    public static ArrayList<Post> loadAllFavoritePosts(RequestQueue requestQueue,
                                               int amount,
                                               int fromRow,
                                               String unique_id,
                                               String sql_query) {
        String url = Endpoints.getRequestUrlAllFavoritePosts(30);

        JSONObject response = Requestor.requestAllFavoritePostsJSON(requestQueue,
                /*Endpoints.getRequestUrlAllPosts(30),*/
                url,
                amount,
                fromRow,
                unique_id,
                sql_query);
        ArrayList<Post> listPosts = Parser.parsePostsJSON(response);
        //MyApplication.getWritableDatabase().insertVotes(DBVotes.BOX_OFFICE, listVotes, true);
        return listPosts;
    }

    public static ArrayList<Comment> loadAllComments(RequestQueue requestQueue,
                                               String uid_post) {
        JSONObject response = Requestor.requestAllCommentsJSON(requestQueue,
                Endpoints.getRequestUrlAllComments(30),
                uid_post);
        ArrayList<Comment> listComments = Parser.parseCommentsJSON(response);
        //MyApplication.getWritableDatabase().insertVotes(DBVotes.BOX_OFFICE, listVotes, true);
        return listComments;
    }

    public static ArrayList<FollowObj> loadAllFollowedDegrees(RequestQueue requestQueue,
                                               String unique_id) {
        JSONObject response = Requestor.requestAllFollowedDegreesJSON(requestQueue,
                Endpoints.getRequestUrlAllFollowedDegrees(30),
                unique_id);
        ArrayList<FollowObj> listFollowes = Parser.parseAllFollowedDegreeJSON(response);
        //MyApplication.getWritableDatabase().insertVotes(DBVotes.BOX_OFFICE, listVotes, true);
        return listFollowes;
    }

    public static ArrayList<LikeObj> loadAllLikedDegrees(RequestQueue requestQueue,
                                                              String unique_id) {
        JSONObject response = Requestor.requestAllFollowedDegreesJSON(requestQueue,
                Endpoints.getRequestUrlAllLikedDegrees(30),
                unique_id);
        ArrayList<LikeObj> listliked = Parser.parseAllLikedDegreeJSON(response);
        //MyApplication.getWritableDatabase().insertVotes(DBVotes.BOX_OFFICE, listVotes, true);
        return listliked;
    }


    public static int pressLikeDegree(RequestQueue requestQueue,
                                                    String uid_liked_degree,
                                                    String user_uid,
                                                    String user_name){
        JSONObject response = Requestor.requestLikeDegreeJSON(requestQueue,
                Endpoints.getRequestUrlLikeDegree(30), uid_liked_degree,
                user_uid,
                user_name,
                "uid_liked_degree");

        int ifSuccess = Parser.parseUpdateVoteJSON(response);

//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return ifSuccess;
    }

    public static int pressFollowDegree(RequestQueue requestQueue,
                                      String uid_liked_degree,
                                      String user_uid,
                                      String user_name){
        JSONObject response = Requestor.requestLikeDegreeJSON(requestQueue,
                Endpoints.getRequestUrlFollowDegree(30), uid_liked_degree,
                user_uid,
                user_name,
                "uid_followed_degree");

        int ifSuccess = Parser.parseUpdateVoteJSON(response);

//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return ifSuccess;
    }

    public static ExtraInfoDegree getExtraInfoDegree(RequestQueue requestQueue,
                                         String dbid_degree,
                                         String unique_id){
        JSONObject response = Requestor.requestDegreeExtraInfo(requestQueue,
                Endpoints.getRequestUrlDegreeExtraInfo(30), dbid_degree, unique_id);

        ExtraInfoDegree extraInfoDegree = Parser.parseDegreeExtraInfoJSON(response);

//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return extraInfoDegree;
    }

    public static int insertNewPost(RequestQueue requestQueue,
                                    String degree_id,
                                    String user_create_id,
                                    String body_content,
                                    String rate,
                                    String type_post){
        JSONObject response = Requestor.requestInsertNewPost(requestQueue,
                Endpoints.getRequestUrlInsertNewPost(30),
                degree_id,
                user_create_id,
                body_content,
                rate,
                type_post);

        int ifSuccess = Parser.parseUpdateVoteJSON(response);
//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return ifSuccess;
    }

    public static int insertComment(RequestQueue requestQueue,
                                    String uid_post,
                                    String user_id,
                                    String user_name,
                                    String body_content){
        JSONObject response = Requestor.requestInsertComment(requestQueue,
                Endpoints.getRequestUrlInsertComment(30),
                uid_post,
                user_id,
                user_name,
                body_content);

        int ifSuccess = Parser.parseUpdateVoteJSON(response);
//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return ifSuccess;
    }

    public static int insertUserExtraInfo(RequestQueue requestQueue,
                                    String user_unique_id,
                                    String user_type,
                                    String user_year,
                                    String user_degree_1,
                                    String user_degree_2,
                                    String user_degree_3){
        JSONObject response = Requestor.requestInsertUserExtraInfo(requestQueue,
                Endpoints.getRequestUrlInsertUserExtraInfo(30),
                user_unique_id,
                user_type,
                user_year,
                user_degree_1,
                user_degree_2,
                user_degree_3);

        int ifSuccess = Parser.parseUpdateVoteJSON(response);
//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return ifSuccess;
    }

    public static int pressVotePost(RequestQueue requestQueue,
                                      String uid_user,
                                      String user_name,
                                      String uid_post,
                                     String vote_decision){
        JSONObject response = Requestor.requestVotePostJSON(requestQueue,
                Endpoints.getRequestUrlVotePost(30),
                uid_user,
                user_name,
                uid_post,
                vote_decision);

        int ifSuccess = Parser.parseUpdateVoteJSON(response);

//        ArrayList<Subject> listSubjects = Parser.parseSubjectJSON(response);
//        MyApplication.getWritableDatabase().insertSubjects(DBDegrees.ALL_SUBJECTS, listSubjects, true);
        return ifSuccess;
    }

}
