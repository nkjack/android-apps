package com.studentadvisor.noam.studentadvisor.json;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.studentadvisor.noam.studentadvisor.logging.L;

public class Requestor {
    public static JSONObject requestRegularJSON(RequestQueue requestQueue, String url) {
        JSONObject response = null;
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                (String)null, requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestLikeDegreeJSON(RequestQueue requestQueue,
                                                    String url,
                                                    String uid_liked_degree,
                                                    String user_uid,
                                                    String user_name,
                                                    String map_uid_action) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        L.m(uid_liked_degree + " " + user_uid + " " + user_name);
        if (uid_liked_degree !=null && user_uid !=null && user_name != null ) {
            map.put(map_uid_action ,uid_liked_degree); // because there are like and follow action.
            map.put("user_uid",user_uid);
            map.put("user_name",user_name);
        }

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestDegreeExtraInfo(RequestQueue requestQueue,
                                                     String url,
                                                     String dbid_degree,
                                                     String unique_id) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        L.m(dbid_degree + " " + unique_id );
        if (dbid_degree !=null && unique_id !=null) {
            map.put("dbid_degree",dbid_degree);
            map.put("unique_id",unique_id);
        }

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestInsertNewPost(RequestQueue requestQueue,
                                                    String url,
                                                    String degree_id,
                                                    String user_create_id,
                                                    String body_content,
                                                    String rate,
                                                    String type_post) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        if (degree_id !=null && user_create_id !=null && body_content != null && rate != null && type_post != null) {
            map.put("degree_id",degree_id);
            map.put("user_create_id",user_create_id);
            map.put("body_content",body_content);
            map.put("rate",rate);
            map.put("type_post",type_post);
        }

        L.m("degree_id "+degree_id);
        L.m("user_create_id "+user_create_id);
        L.m("body_content "+body_content);
        L.m("rate "+rate);
        L.m("type_post "+type_post);


        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestAllPostsJSON(RequestQueue requestQueue,
                                                 String url,
                                                 int amountToFetch,
                                                 int fromRow,
                                                 String type_post,
                                                 String dbid_degree,
                                                 String unique_id) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        L.m("" + amountToFetch + "   " + fromRow);
        if ( amountToFetch > 0 && fromRow >= 0){
            map.put("amount_to_fetch", ""+amountToFetch);
            map.put("from_row", ""+fromRow);
            map.put("type_post", type_post);
            map.put("dbid_degree", dbid_degree);
            map.put("unique_id", unique_id);
        }

        L.m("amount_to_fetch " + amountToFetch);
        L.m("from_row "+ fromRow);
        L.m("type_post "+ type_post);
        L.m("dbid_degree "+dbid_degree);
        L.m("unique_id "+ unique_id);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestAllExplorePostsJSON(RequestQueue requestQueue,
                                                 String url,
                                                 int amountToFetch,
                                                 int fromRow,
                                                 String unique_id) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        L.m("" + amountToFetch + "   " + fromRow);
        if ( amountToFetch > 0 && fromRow >= 0){
            map.put("amount_to_fetch", ""+amountToFetch);
            map.put("from_row", ""+fromRow);
            map.put("unique_id", unique_id);
        }

        L.m("amount_to_fetch " + amountToFetch);
        L.m("from_row "+ fromRow);
        L.m("unique_id "+ unique_id);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestAllFavoritePostsJSON(RequestQueue requestQueue,
                                                 String url,
                                                 int amountToFetch,
                                                 int fromRow,
                                                 String unique_id,
                                                 String sql_query) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        L.m("" + amountToFetch + "   " + fromRow);
        if ( amountToFetch > 0 && fromRow >= 0){
            map.put("amount_to_fetch", ""+amountToFetch);
            map.put("from_row", ""+fromRow);
            map.put("unique_id", unique_id);
            map.put("sql_string", sql_query);
        }

        L.m("amount_to_fetch " + amountToFetch);
        L.m("from_row "+ fromRow);
        L.m("unique_id "+ unique_id);
        L.m("sql_string" + sql_query);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestAllCommentsJSON(RequestQueue requestQueue,
                                                 String url,
                                                 String uid_post) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        if ( uid_post != null){
            map.put("dbid_post", uid_post);
        }

        L.m("uid_post " + uid_post);


        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestAllFollowedDegreesJSON(RequestQueue requestQueue,
                                                 String url,
                                                 String unique_id) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        if ( unique_id != null){
            map.put("unique_id", ""+unique_id);
        }

        L.m("unique_id "+ unique_id);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }


    public static JSONObject requestVotePostJSON(RequestQueue requestQueue,
                                                 String url,
                                                 String uid_user,
                                                 String user_name,
                                                 String uid_post,
                                                 String vote_decision) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        if (uid_user !=null && user_name !=null && uid_post != null && vote_decision != null ) {
            map.put("uid_user",uid_user);
            map.put("user_name",user_name);
            map.put("uid_post",uid_post);
            map.put("vote_decision",vote_decision);

        }

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestInsertComment(RequestQueue requestQueue,
                                                  String url,
                                                  String uid_post,
                                                  String user_id,
                                                  String user_name,
                                                  String body_content) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        if (uid_post !=null && user_id !=null && user_name !=null && body_content != null ) {
            map.put("uid_post",uid_post);
            map.put("user_id",user_id);
            map.put("user_name",user_name);
            map.put("body_content",body_content);
        }

        L.m("uid_post "+uid_post);
        L.m("user_id "+user_id);
        L.m("user_name "+user_name);
        L.m("body_content "+body_content);


        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }

    public static JSONObject requestInsertUserExtraInfo(RequestQueue requestQueue,
                                                  String url,
                                                String user_unique_id,
                                                String user_type,
                                                String user_year,
                                                String user_degree_1,
                                                String user_degree_2,
                                                String user_degree_3) {
        JSONObject response = null;
        Map<String, String> map = new HashMap<String, String>();

        if (user_unique_id !=null && user_type !=null && user_year !=null
                && user_degree_1 != null && user_degree_2 != null  && user_degree_3 != null) {
            map.put("user_unique_id",user_unique_id);
            map.put("user_type",user_type);
            map.put("user_year",user_year);
            map.put("user_degree_1",user_degree_1);
            map.put("user_degree_2",user_degree_2);
            map.put("user_degree_3",user_degree_3);

        }

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(map), requestFuture, requestFuture);

        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.m(e + "");
        } catch (ExecutionException e) {
            L.m(e + "");
        } catch (TimeoutException e) {
            L.m(e + "");
        }
        return response;
    }
}
