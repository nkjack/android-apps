package com.studentadvisor.noam.studentadvisor.json;


import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_DEGREES;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_SUBJECTS;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_INSERT_COMMENT;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_LIKE_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_DEGREE_EXTRA;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_INSERT_NEW_POST;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_POSTS;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_VOTE_POST;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_FOLLOW_DEGREE;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_COMMENTS;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_FOLLOWED_DEGREES;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_LIKED_DEGREES;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_POSTS_NEWS_FEED;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_INSERT_USER_EXTRA_INFO;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_FAVORITE_POSTS;
import static com.studentadvisor.noam.studentadvisor.extras.UrlEndpoints.URL_ALL_POSTS_EXPLORE;

public class Endpoints {
    public static String getRequestUrlAllDegrees(int limit) {
        return URL_ALL_DEGREES;
    }
    public static String getRequestUrlAllSubjects(int limit) {return URL_ALL_SUBJECTS;}
    public static String getRequestUrlAllSchools(int limit) {return URL_ALL_SUBJECTS;}
    public static String getRequestUrlLikeDegree(int limit) {return URL_LIKE_DEGREE;}
    public static String getRequestUrlDegreeExtraInfo(int limit) {return URL_DEGREE_EXTRA;}
    public static String getRequestUrlInsertNewPost(int limit) {return URL_INSERT_NEW_POST;}
    public static String getRequestUrlAllPosts(int limit) {return URL_ALL_POSTS;}
    public static String getRequestUrlAllPostsNewsFeed(int limit) {return URL_ALL_POSTS_NEWS_FEED;}
    public static String getRequestUrlAllFavoritePosts(int limit) {return URL_ALL_FAVORITE_POSTS;}
    public static String getRequestUrlVotePost(int limit) {return URL_VOTE_POST;}
    public static String getRequestUrlFollowDegree(int limit){return URL_FOLLOW_DEGREE;}
    public static String getRequestUrlAllComments(int limit){return URL_ALL_COMMENTS;}
    public static String getRequestUrlInsertComment(int limit){return URL_INSERT_COMMENT;}
    public static String getRequestUrlAllFollowedDegrees(int limit){return URL_ALL_FOLLOWED_DEGREES;}
    public static String getRequestUrlAllLikedDegrees(int limit){return URL_ALL_LIKED_DEGREES;}
    public static String getRequestUrlInsertUserExtraInfo(int limit){return URL_INSERT_USER_EXTRA_INFO;}
    public static String getRequestUrlAllExplorePosts(int limit) {return URL_ALL_POSTS_EXPLORE;}







}
