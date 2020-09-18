package com.studentadvisor.noam.studentadvisor.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.studentadvisor.noam.studentadvisor.extras.Constants;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.FollowObj;
import com.studentadvisor.noam.studentadvisor.pojo.LikeObj;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.pojo.Subject;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointDegrees.*;


/**
 * Created by Windows on 02-03-2015.
 */
public class Parser {
    public static ArrayList<Degree> parseDegreesJSON(JSONObject response) {
        ArrayList<Degree> listDegrees = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                // Checking for SUCCESS KEY
                int success = response.getInt(TAG_SUCCESS);
                if (success == 1) {

                    JSONArray arrayDegrees = response.getJSONArray(KEY_ALL_DEGREES);
                    for (int i = 0; i < arrayDegrees.length(); i++) {
                        int id_degree = -1;
                        String facultyName = Constants.NA;
                        String degree_name = Constants.NA;
                        String schoolNameHe = Constants.NA;
                        String schoolNameEn = Constants.NA;
                        String school_url_web = Constants.NA;
                        int likes = -1;
                        int follows = -1;
                        String subject_1 = Constants.NA;
                        String subject_2 = Constants.NA;
                        String subject_3 = Constants.NA;
                        String subject_4 = Constants.NA;
                        String urlLogoPic = Constants.NA;
                        String urlHeaderPic = Constants.NA;
                        JSONObject currentDegree = arrayDegrees.getJSONObject(i);
                        if (Utils.contains(currentDegree, KEY_DEGREE_ID)) {
                            id_degree = currentDegree.getInt(KEY_DEGREE_ID);
                        }
                        if (Utils.contains(currentDegree, KEY_FACULTY_NAME)) {
                            facultyName = currentDegree.getString(KEY_FACULTY_NAME);
                        }

                        if (Utils.contains(currentDegree, KEY_DEGREE_NAME)) {
                            degree_name = currentDegree.getString(KEY_DEGREE_NAME);
                        }

                        if (Utils.contains(currentDegree, KEY_SCHOOL_NAME_HE)) {
                            schoolNameHe = currentDegree.getString(KEY_SCHOOL_NAME_HE);
                        }

                        if (Utils.contains(currentDegree, KEY_SCHOOL_URL_WEBSITE)) {
                            schoolNameEn = currentDegree.getString(KEY_SCHOOL_URL_WEBSITE);
                        }

                        if (Utils.contains(currentDegree, KEY_SCHOOL_NAME_EN)) {
                            schoolNameEn = currentDegree.getString(KEY_SCHOOL_NAME_EN);
                        }

                        if (Utils.contains(currentDegree, KEY_DEGREE_LIKES)) {
                            likes = currentDegree.getInt(KEY_DEGREE_LIKES);
                        }

                        if (Utils.contains(currentDegree, KEY_DEGREE_FOLLOWS)) {
                            follows = currentDegree.getInt(KEY_DEGREE_FOLLOWS);
                        }

                        if (Utils.contains(currentDegree, KEY_SUBJECT_1)) {
                            subject_1 = currentDegree.getString(KEY_SUBJECT_1);
                        }
                        if (Utils.contains(currentDegree, KEY_SUBJECT_2)) {
                            subject_2 = currentDegree.getString(KEY_SUBJECT_2);
                        }
                        if (Utils.contains(currentDegree, KEY_SUBJECT_3)) {
                            subject_3 = currentDegree.getString(KEY_SUBJECT_3);
                        }
                        if (Utils.contains(currentDegree, KEY_SUBJECT_4)) {
                            subject_4 = currentDegree.getString(KEY_SUBJECT_4);
                        }

                        if (Utils.contains(currentDegree, KEY_URL_LOGO)) {
                            urlLogoPic = currentDegree.getString(KEY_URL_LOGO);
                        }

                        if (Utils.contains(currentDegree, KEY_URL_HEADER)) {
                            urlHeaderPic = currentDegree.getString(KEY_URL_HEADER);
                        }
                    /*
                    //get the url for the thumbnail to be displayed inside the current movie result
                    if (Utils.contains(currentDegree, KEY_POSTERS)) {
                        JSONObject objectPosters = currentDegree.getJSONObject(KEY_POSTERS);

                        if (Utils.contains(objectPosters, KEY_THUMBNAIL)) {
                            urlThumbnail = objectPosters.getString(KEY_THUMBNAIL);
                        }
                    }
                    */
                        Degree degree = new Degree();
                        degree.setDbid_degree(id_degree);
                        degree.setFaculty_name(facultyName);
                        degree.setDegree_name(degree_name);
                        degree.setSchool_name_he(schoolNameHe);
                        degree.setSchool_name_en(schoolNameEn);
                        degree.setSchool_url_web(school_url_web);
                        degree.setLikes(likes);
                        degree.setFollowes(follows);
                        degree.setSubject_1(subject_1);
                        degree.setSubject_2(subject_2);
                        degree.setSubject_3(subject_3);
                        degree.setSubject_4(subject_4);
                        degree.setUrlLogoPic(urlLogoPic);
                        degree.setUrlHeaderPic(urlHeaderPic);

                        if (id_degree != -1 && !degree_name.equals(Constants.NA)) {
                            listDegrees.add(degree);
                        }
                    }
                } else if (success == 0) {
                    String message = Constants.NA;
                    if (Utils.contains(response, TAG_MESSAGE)) {
                        message = response.getString(TAG_MESSAGE);
                        L.m("message " + message);
                    }
                    if (message.equals("No products found")) {
                        //Toast.makeText(getActivity(), "No Products Founded", Toast.LENGTH_LONG).show();
                        L.m("No Products Founded - List EMPTY - ALL_DEGREES");
                    }

                }


            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return listDegrees;
    }

    public static ArrayList<Post> parsePostsJSON(JSONObject response) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        ArrayList<Post> listPosts = new ArrayList<>();
        if (response != null && response.length() > 0) {
//            L.m("Post Response - " + response.toString());
            try {
                // Checking for SUCCESS KEY
                int success = response.getInt(TAG_SUCCESS);
                if (success == 1) {

                    JSONArray arrayPosts = response.getJSONArray(KEY_ALL_POSTS);
                    for (int i = 0; i < arrayPosts.length(); i++) {
                        int dbid_post = -1;
                        String body_content = Constants.NA;
                        String date_added = Constants.NA;
                        String user_create_id = Constants.NA;
                        int dbid_user_pic = -1;
                        String user_name = Constants.NA;
                        int degree_id = -1;
                        int rate = -1;
                        int totalScore = -9999;
                        int totalComments = -1;
                        String typePost = Constants.NA;
                        int userChoice = -2; // -1 is negative and + 1 is positive 0 is nutral

                        String degree_name = Constants.NA;
                        String faculty_name = Constants.NA;
                        String school_name = Constants.NA;

                        JSONObject currentPost = arrayPosts.getJSONObject(i);
                        if (Utils.contains(currentPost, TAG_DBID_POST)) {
                            dbid_post = currentPost.getInt(TAG_DBID_POST);
                        }
                        if (Utils.contains(currentPost, TAG_DEGREE_ID)) {
                            degree_id = currentPost.getInt(TAG_DEGREE_ID);
                        }
                        if (Utils.contains(currentPost, TAG_USER_CREATE_ID)) {
                            //NA
                            if (!currentPost.getString(TAG_USER_CREATE_ID).contentEquals("")) {
                                user_create_id = currentPost.getString(TAG_USER_CREATE_ID);
                            }
                        }

                        if (Utils.contains(currentPost, TAG_DBID_PIC)) {
                            dbid_user_pic = currentPost.getInt(TAG_DBID_PIC);
                        }

                        if (Utils.contains(currentPost, TAG_CREATED_AT)) {
                            date_added = currentPost.getString(TAG_CREATED_AT);
                        }
                        if (Utils.contains(currentPost, TAG_BODY_CONTENT)) {
                            //NA
                            if (!currentPost.getString(TAG_BODY_CONTENT).contentEquals("")) {
                                body_content = currentPost.getString(TAG_BODY_CONTENT);
                                body_content = "\" " + body_content + "\" ";
                            }
                        }
                        if (Utils.contains(currentPost, TAG_RATE)) {
                            rate = currentPost.getInt(TAG_RATE);
                        }
                        if (Utils.contains(currentPost, TAG_TOTAL_SCORE)) {
                            totalScore = currentPost.getInt(TAG_TOTAL_SCORE);
                        }
                        if (Utils.contains(currentPost, TAG_TOTAL_COMMENTS)) {
                            totalComments = currentPost.getInt(TAG_TOTAL_COMMENTS);
                        }
                        if (Utils.contains(currentPost, TAG_NAME)) {
                            user_name = currentPost.getString(TAG_NAME);
                        }
                        if (Utils.contains(currentPost, TAG_TYPE_POST)) {
                            typePost = currentPost.getString(TAG_TYPE_POST);
                        }
                        if(Utils.contains(currentPost, TAG_VOTE_CHOICE)){
                            userChoice = currentPost.getInt(TAG_VOTE_CHOICE);
                        }

                        if(Utils.contains(currentPost, TAG_POST_DEGREE_NAME)){
                            degree_name = currentPost.getString(TAG_POST_DEGREE_NAME);
                        }
                        if(Utils.contains(currentPost, TAG_POST_FACULTY_NAME)){
                            faculty_name = currentPost.getString(TAG_POST_FACULTY_NAME);
                        }
                        if(Utils.contains(currentPost, TAG_POST_SCHOOL_NAME)){
                            school_name = currentPost.getString(TAG_POST_SCHOOL_NAME);
                        }

                    /*
                    //get the url for the thumbnail to be displayed inside the current movie result
                    if (Utils.contains(currentDegree, KEY_POSTERS)) {
                        JSONObject objectPosters = currentDegree.getJSONObject(KEY_POSTERS);

                        if (Utils.contains(objectPosters, KEY_THUMBNAIL)) {
                            urlThumbnail = objectPosters.getString(KEY_THUMBNAIL);
                        }
                    }
                    */
                        Post post = new Post();

                        post.setBody_content(body_content);
                        Date date = null;
                        try {
                            date = dateFormat.parse(date_added);
                        } catch (ParseException e) {
                            //a parse exception generated here will store null in the release date, be sure to handle it
                            L.m(e.toString());
                        }
                        post.setDate_added(date);
                        post.setDbid_post(dbid_post);
                        post.setDegree_id(degree_id);
                        post.setRate(rate);
                        post.setTotalScore(totalScore);
                        post.setUser_name(user_name);
                        post.setUser_create_id(user_create_id);
                        post.setDbid_user_pic(dbid_user_pic);
                        post.setTypePost(typePost);
                        post.setTotalComments(totalComments);
                        post.setUser_choice(userChoice);

                        post.setDegree_name(degree_name);
                        post.setFaculty_name(faculty_name);
                        post.setSchool_name(school_name);

                        L.m("POST ------------------------------");
                        L.m("body - " + body_content);
                        L.m("date_added - " + date_added);
                        L.m("dbid_post - " + dbid_post);
                        L.m("degree_id - " + degree_id);
                        L.m("rate - " + rate);
                        L.m("totalScore - " + totalScore);
                        L.m("user_name - " + user_name);
                        L.m("user_create_id - " + user_create_id);
                        L.m("dbid_user_pic - " + dbid_user_pic);
                        L.m("typePost - " + typePost);
                        L.m("userChoice - " + userChoice);

                        L.m("degree_name - " + degree_name);
                        L.m("faculty_name - " + faculty_name);
                        L.m("school_name - " + school_name);


                        if (dbid_post != -1
                                && !body_content.contentEquals(Constants.NA)
                                && !user_name.contentEquals(Constants.NA)
                                && degree_id != -1
                                && totalComments != -1
                                && totalScore != -9999
                                && date != null) {
                            listPosts.add(post);
                        }
                    }
                } else if (success == 0) {
                    String message = Constants.NA;
                    if (Utils.contains(response, TAG_MESSAGE)) {
                        message = response.getString(TAG_MESSAGE);
                        L.m("message " + message);
                    }
                    if (message.equals("No products found")) {
                        //Toast.makeText(getActivity(), "No Products Founded", Toast.LENGTH_LONG).show();
                        L.m("No Products Founded - List EMPTY - ALL_POSTS");
                    }

                }

            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return listPosts;

    }

    public static ArrayList<Comment> parseCommentsJSON(JSONObject response) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        ArrayList<Comment> listComments = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                // Checking for SUCCESS KEY
                int success = response.getInt(TAG_SUCCESS);
                L.m("comments " + response.toString());
                if (success == 1) {

                    JSONArray arrayComments = response.getJSONArray(KEY_ALL_COMMENTS);
                    for (int i = 0; i < arrayComments.length(); i++) {
                        int dbid_comment = -1;
                        int uid_post = -1;
                        String user_id = Constants.NA;
                        int dbid_user_pic = -1;
                        String user_name = Constants.NA;
                        String body_content = Constants.NA;
                        String date_added = Constants.NA;

                        JSONObject currentComment = arrayComments.getJSONObject(i);
                        if (Utils.contains(currentComment, TAG_COMMENT_ID)) {
                            dbid_comment = currentComment.getInt(TAG_COMMENT_ID);
                        }
                        if (Utils.contains(currentComment, TAG_COMMENT_ID_POST)) {
                            uid_post = currentComment.getInt(TAG_COMMENT_ID_POST);
                        }
                        if (Utils.contains(currentComment, TAG_COMMENT_ID_USER)) {
                            //NA
                            if (!currentComment.getString(TAG_COMMENT_ID_USER).contentEquals("")) {
                                user_id = currentComment.getString(TAG_COMMENT_ID_USER);
                            }
                        }

                        if (Utils.contains(currentComment, TAG_DBID_PIC)) {
                            dbid_user_pic = currentComment.getInt(TAG_DBID_PIC);
                        }

                        if (Utils.contains(currentComment, TAG_COMMENT_USER_NAME)) {
                            user_name = currentComment.getString(TAG_COMMENT_USER_NAME);
                        }
                        if (Utils.contains(currentComment, TAG_COMMENT_BODY)) {
                            //NA
                            if (!currentComment.getString(TAG_COMMENT_BODY).contentEquals("")) {
                                body_content = currentComment.getString(TAG_COMMENT_BODY);
//                                body_content = "\" " + body_content + "\" ";
                            }
                        }
                        if (Utils.contains(currentComment, TAG_COMMENT_DATE)) {
                            date_added = currentComment.getString(TAG_COMMENT_DATE);
                        }

                        Comment comment = new Comment();

                        Date date = null;
                        try {
                            date = dateFormat.parse(date_added);
                        } catch (ParseException e) {
                            //a parse exception generated here will store null in the release date, be sure to handle it
                            L.m(e.toString());
                        }
                        comment.setDate_added(date);
                        comment.setDbid_comment(dbid_comment);
                        comment.setUid_post(uid_post);
                        comment.setUser_id(user_id);
                        comment.setDbid_user_pic(dbid_user_pic);
                        comment.setUser_name(user_name);
                        comment.setBody_content(body_content);

                        if (dbid_comment != -1
                                && !body_content.contentEquals(Constants.NA)
                                && !user_name.contentEquals(Constants.NA)
                                && uid_post != -1 )
                        {
                            listComments.add(comment);
                        }
                    }
                } else if (success == 0) {
                    String message = Constants.NA;
                    if (Utils.contains(response, TAG_MESSAGE)) {
                        message = response.getString(TAG_MESSAGE);
                        L.m("message " + message);
                    }
                    if (message.equals("No products found")) {
                        //Toast.makeText(getActivity(), "No Products Founded", Toast.LENGTH_LONG).show();
                        L.m("No Products Founded - List EMPTY - ALL_POSTS");
                    }

                }

            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return listComments;

    }

    public static ArrayList<Subject> parseSubjectJSON(JSONObject response) {
        ArrayList<Subject> listSubjects = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                JSONArray arrayDegrees = response.getJSONArray(KEY_ALL_SUBJECTS);
                for (int i = 0; i < arrayDegrees.length(); i++) {
                    int dbid_subject = -1;
                    String subject_name = Constants.NA;

                    JSONObject currentDegree = arrayDegrees.getJSONObject(i);
                    if (Utils.contains(currentDegree, KEY_SUBJECT_ID)) {
                        dbid_subject = currentDegree.getInt(KEY_SUBJECT_ID);
                    }

                    if (Utils.contains(currentDegree, KEY_SUBJECT_NAME)) {
                        subject_name = currentDegree.getString(KEY_SUBJECT_NAME);
                    }


                    Subject subject = new Subject();
                    subject.setDbid_subject(dbid_subject);
                    subject.setSubject_name(subject_name);


                    if (dbid_subject != -1 && !subject_name.equals(Constants.NA)) {
                        listSubjects.add(subject);
                    }
                }

            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }

        return listSubjects;
    }

    public static int parseUpdateVoteJSON(JSONObject response) {
        int success = -1;
        if (response != null && response.length() > 0) {
            try {
                if (response != null) {
                    if (Utils.contains(response, TAG_SUCCESS)) {
                        success = response.getInt(TAG_SUCCESS);
                    }

                    if (Utils.contains(response, TAG_MESSAGE)) {
                        L.m(response.getString(TAG_MESSAGE));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static ExtraInfoDegree parseDegreeExtraInfoJSON(JSONObject response) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        ExtraInfoDegree objToReturn = null;
        if (response != null && response.length() > 0) {
            try {
                L.m("noam test " + response.toString());
                // Checking for SUCCESS KEY
                int success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    L.m("Success");

                    int numFollwes = -1;
                    int numRated = -1;
                    int numPosts = -1;
                    int numLikes = -1;
                    boolean if_liked = false;
                    boolean if_post_rate = false;
                    boolean if_followed = false;
                    int ifLiked = -1;
                    int ifPostRate = -1;
                    int ifFollowed = -1;
                    float scoreRate = -1;

                    int dbid_post = -1;
                    String body_content = Constants.NA;
                    String date_added = Constants.NA;
                    String user_create_id = Constants.NA;
                    String user_name = Constants.NA;
                    int degree_id = -1;
                    int rate = -1;
                    int totalScore = -1;
                    int totalComments = -1;
                    int dbid_user_pic = -1;
                    String typePost = Constants.NA;
                    String degree_name = Constants.NA;
                    String faculty_name = Constants.NA;
                    String school_name = Constants.NA;

                    //MyPost if I have
                    int my_dbid_post = -1;
                    String my_body_content = Constants.NA;
                    String my_date_added = Constants.NA;
                    String my_user_create_id = Constants.NA;
                    String my_user_name = Constants.NA;
                    int my_degree_id = -1;
                    int my_rate = -1;
                    int my_totalScore = -1;
                    int my_totalComments = -1;
                    String my_typePost = Constants.NA;
                    String my_degree_name = Constants.NA;
                    String my_faculty_name = Constants.NA;
                    String my_school_name = Constants.NA;


                    JSONArray arrayMyPost = response.getJSONArray(KEY_MY_POST);
                    JSONObject myCurrentPost = arrayMyPost.getJSONObject(0);

                    L.m("objectttt " + myCurrentPost.toString() );

                    if (Utils.contains(myCurrentPost, TAG_IF_POST_RATE)) {
                        ifPostRate = myCurrentPost.getInt(TAG_IF_POST_RATE);
                        if_post_rate = ifPostRate == 1;

                        if (if_post_rate){
                            if (Utils.contains(myCurrentPost, TAG_DBID_POST)){
                                my_dbid_post = myCurrentPost.getInt(TAG_DBID_POST);
                            }
                            if (Utils.contains(myCurrentPost, TAG_DEGREE_ID)) {
                                my_degree_id = myCurrentPost.getInt(TAG_DEGREE_ID);
                            }
                            if (Utils.contains(myCurrentPost, TAG_USER_CREATE_ID)) {
                                //NA
                                if (!myCurrentPost.getString(TAG_USER_CREATE_ID).contentEquals("")) {
                                    my_user_create_id = myCurrentPost.getString(TAG_USER_CREATE_ID);
                                }
                            }
                            if (Utils.contains(myCurrentPost, TAG_CREATED_AT)) {
                                my_date_added = myCurrentPost.getString(TAG_CREATED_AT);
                            }
                            if (Utils.contains(myCurrentPost, TAG_BODY_CONTENT)) {
                                //NA
                                if (!myCurrentPost.getString(TAG_BODY_CONTENT).contentEquals("")) {
                                    my_body_content = myCurrentPost.getString(TAG_BODY_CONTENT);
                                }
                            }
                            if (Utils.contains(myCurrentPost, TAG_RATE)) {
                                my_rate = myCurrentPost.getInt(TAG_RATE);
                            }
                            if (Utils.contains(myCurrentPost, TAG_TYPE_POST)) {
                                my_typePost = myCurrentPost.getString(TAG_TYPE_POST);
                            }
                        }


//                        if_post_rate = ifPostRate == 1 ? true : false;
                    }


                    if (Utils.contains(response, TAG_NUM_RATED)) {
                        numRated = response.getInt(TAG_NUM_RATED);
                    }
                    if (Utils.contains(response, TAG_NUM_POSTS)) {
                        numPosts = response.getInt(TAG_NUM_POSTS);

                    }
                    if (Utils.contains(response, TAG_NUM_LIKES)) {
                        numLikes = response.getInt(TAG_NUM_LIKES);
                    }
                    if (Utils.contains(response, TAG_IF_LIKED)) {
                        ifLiked = response.getInt(TAG_IF_LIKED);
                        if_liked = ifLiked == 1;
                    }

                    if (Utils.contains(response, TAG_DBID_POST)) {
                        dbid_post = response.getInt(TAG_DBID_POST);
                    }
                    if (Utils.contains(response, TAG_DEGREE_ID)) {
                        degree_id = response.getInt(TAG_DEGREE_ID);
                    }
                    if (Utils.contains(response, TAG_USER_CREATE_ID)) {
                        //NA
                        if (!response.getString(TAG_USER_CREATE_ID).contentEquals("")) {
                            user_create_id = response.getString(TAG_USER_CREATE_ID);
                        }
                    }
                    if (Utils.contains(response, TAG_CREATED_AT)) {
                        date_added = response.getString(TAG_CREATED_AT);
                    }
                    if (Utils.contains(response, TAG_BODY_CONTENT)) {
                        //NA
                        if (!response.getString(TAG_BODY_CONTENT).contentEquals("")) {
                            body_content = response.getString(TAG_BODY_CONTENT);
                        }
                    }
                    if (Utils.contains(response, TAG_RATE)) {
                        rate = response.getInt(TAG_RATE);
                    }
                    if (Utils.contains(response, TAG_TOTAL_SCORE)) {
                        totalScore = response.getInt(TAG_TOTAL_SCORE);
                    }
                    if (Utils.contains(response, TAG_TOTAL_COMMENTS)) {
                        totalComments = response.getInt(TAG_TOTAL_COMMENTS);
                    }
                    if (Utils.contains(response, TAG_NAME)) {
                        user_name = response.getString(TAG_NAME);
                    }
                    if (Utils.contains(response, TAG_TYPE_POST)) {
                        typePost = response.getString(TAG_TYPE_POST);
                    }
                    if (Utils.contains(response, TAG_NUM_FOLLOWES)) {
                        numFollwes = response.getInt(TAG_NUM_FOLLOWES);
                    }
                    if (Utils.contains(response, TAG_IF_FOLLOWED)) {
                        ifFollowed = response.getInt(TAG_IF_FOLLOWED);
                        if_followed = ifFollowed == 1;
                    }
                    if (Utils.contains(response, TAG_SCORE_RATE)) {
                        scoreRate = (float)response.getDouble(TAG_SCORE_RATE);
                    }

                    if (Utils.contains(response, TAG_DBID_PIC)) {
                        dbid_user_pic = response.getInt(TAG_DBID_PIC);
                    }

                    L.m("nommmmmm - " + my_dbid_post);
//                    L.m("body - " + body_content);
//                    L.m("date_added - " + date_added);
//                    L.m("dbid_post - " + dbid_post);
//                    L.m("degree_id - " + degree_id);
//                    L.m("rate - " + rate);
//                    L.m("totalScore - " + totalScore);
//                    L.m("user_name - " + user_name);
//                    L.m("user_create_id - " + user_create_id);
//                    L.m("typePost - " + typePost);
                    Post popularPost = new Post();

                    popularPost.setBody_content(body_content);
                    Date date = null;
                    try {
                        date = dateFormat.parse(date_added);
                    } catch (ParseException e) {
                        //a parse exception generated here will store null in the release date, be sure to handle it
                        L.m(e.toString());
                    }
                    popularPost.setDate_added(date);
                    popularPost.setDbid_post(dbid_post);
                    popularPost.setDegree_id(degree_id);
                    popularPost.setRate(rate);
                    popularPost.setTotalScore(totalScore);
                    popularPost.setUser_name(user_name);
                    popularPost.setUser_create_id(user_create_id);
                    popularPost.setTypePost(typePost);
                    popularPost.setTotalComments(totalComments);
                    popularPost.setUser_choice(0);
                    popularPost.setDegree_name(degree_name);
                    popularPost.setFaculty_name(faculty_name);
                    popularPost.setSchool_name(school_name);
                    popularPost.setDbid_user_pic(dbid_user_pic);

                    Post myPost = new Post();

                    myPost.setBody_content(my_body_content);
                    Date my_date = null;
                    try {
                        my_date = dateFormat.parse(my_date_added);
                    } catch (ParseException e) {
                        //a parse exception generated here will store null in the release date, be sure to handle it
                        L.m(e.toString());
                    }
                    myPost.setDate_added(my_date);
                    myPost.setDbid_post(my_dbid_post);
                    myPost.setDegree_id(my_degree_id);
                    myPost.setRate(my_rate);
                    myPost.setTotalScore(my_totalScore);
                    myPost.setUser_name(my_user_name);
                    myPost.setUser_create_id(my_user_create_id);
                    myPost.setTypePost(my_typePost);
                    myPost.setTotalComments(my_totalComments);
                    myPost.setUser_choice(0);
                    myPost.setDegree_name(my_degree_name);
                    myPost.setFaculty_name(my_faculty_name);
                    myPost.setSchool_name(my_school_name);

                    L.m(response.toString());

                    ExtraInfoDegree extraInfoDegree = new ExtraInfoDegree();

                    extraInfoDegree.setIfFollowed(if_followed);
                    extraInfoDegree.setIfLiked(if_liked);
                    extraInfoDegree.setIfPostRate(if_post_rate);
                    extraInfoDegree.setNumLikes(numLikes);
                    extraInfoDegree.setNumPosts(numPosts);
                    extraInfoDegree.setNumRated(numRated);
                    extraInfoDegree.setNumFollowes(numFollwes);
                    extraInfoDegree.setPost(popularPost);
                    extraInfoDegree.setMyUserPost(myPost);
                    extraInfoDegree.setScoreRate(scoreRate);

                    objToReturn = extraInfoDegree;
                    L.m("extra info : " + extraInfoDegree.toString());
//                if (id_degree != -1 && !degree_name.equals(Constants.NA)) {
//                    listDegrees.add(degree);
//                }
                } else if (success == 0) {
                    L.m("not Success");
                    String message = Constants.NA;
                    if (Utils.contains(response, TAG_MESSAGE)) {
                        message = response.getString(TAG_MESSAGE);
                        L.m("message " + message);
                    }

                }

            } catch (JSONException e) {
                L.m(e.toString());
            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return objToReturn;
    }

    public static ArrayList<FollowObj> parseAllFollowedDegreeJSON(JSONObject response) {

        ArrayList<FollowObj> listFollowes = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                // Checking for SUCCESS KEY
                int success = response.getInt(TAG_SUCCESS);
                if (success == 1) {

                    JSONArray arrayFollowes = response.getJSONArray(KEY_ALL_FOLLOWES);
                    for (int i = 0; i < arrayFollowes.length(); i++) {
                        int dbid_degree = -1;
                        int total_followes = -1;

                        JSONObject currentFollow = arrayFollowes.getJSONObject(i);
                        if (Utils.contains(currentFollow, TAG_FOLLOW_FOLLOWED_DEGREE)) {
                            dbid_degree = currentFollow.getInt(TAG_FOLLOW_FOLLOWED_DEGREE);
                        }
                        if (Utils.contains(currentFollow, TAG_FOLLOW_TOTAL_FOLLOWES)) {
                            total_followes = currentFollow.getInt(TAG_FOLLOW_TOTAL_FOLLOWES);
                        }

                        FollowObj followObj = new FollowObj();

                        followObj.setDbid_degree(dbid_degree);
                        followObj.setTotal_followes(total_followes);

                        L.m("Followed Degree ------------------------------");
                        L.m("dbid_degree - " + dbid_degree);
                        L.m("total_followes - " + total_followes);


                        if (dbid_degree != -1
                                && total_followes != -1
                                ) {
                            listFollowes.add(followObj);
                        }
                    }
                } else if (success == 0) {
                    String message = Constants.NA;
                    if (Utils.contains(response, TAG_MESSAGE)) {
                        message = response.getString(TAG_MESSAGE);
                        L.m("message " + message);
                    }
                    if (message.equals("No products found")) {
                        //Toast.makeText(getActivity(), "No Products Founded", Toast.LENGTH_LONG).show();
                        L.m("No Products Founded - List EMPTY - ALL_POSTS");
                    }

                }

            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return listFollowes;
    }

    public static ArrayList<LikeObj> parseAllLikedDegreeJSON(JSONObject response) {

        ArrayList<LikeObj> listLikes = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                // Checking for SUCCESS KEY
                int success = response.getInt(TAG_SUCCESS);
                if (success == 1) {

                    JSONArray arraylikes = response.getJSONArray(KEY_ALL_LIKES);
                    for (int i = 0; i < arraylikes.length(); i++) {
                        int dbid_degree = -1;
                        int total_likes = -1;

                        JSONObject currentLike = arraylikes.getJSONObject(i);
                        if (Utils.contains(currentLike, TAG_LIKE_LIKED_DEGREE)) {
                            dbid_degree = currentLike.getInt(TAG_LIKE_LIKED_DEGREE);
                        }
                        if (Utils.contains(currentLike, TAG_LIKE_TOTAL_LIKES)) {
                            total_likes = currentLike.getInt(TAG_LIKE_TOTAL_LIKES);
                        }

                        LikeObj likeObj = new LikeObj();

                        likeObj.setDbid_degree(dbid_degree);
                        likeObj.setTotal_likes(total_likes);

                        L.m("Liked Degree ------------------------------");
                        L.m("dbid_degree - " + dbid_degree);
                        L.m("total_likes - " + total_likes);


                        if (dbid_degree != -1
                                && total_likes != -1
                                ) {
                            listLikes.add(likeObj);
                        }
                    }
                } else if (success == 0) {
                    String message = Constants.NA;
                    if (Utils.contains(response, TAG_MESSAGE)) {
                        message = response.getString(TAG_MESSAGE);
                        L.m("message " + message);
                    }
                    if (message.equals("No products found")) {
                        //Toast.makeText(getActivity(), "No Products Founded", Toast.LENGTH_LONG).show();
                        L.m("No Products Founded - List EMPTY - ALL_POSTS");
                    }

                }

            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return listLikes;
    }

}
