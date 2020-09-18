package com.studentadvisor.noam.studentadvisor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.studentadvisor.noam.studentadvisor.logging.L;

import java.util.Date;

/**
 * Created by Noam on 11/28/2015.
 */
public class Post implements Parcelable {
    public static final Parcelable.Creator<Post> CREATOR
            = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            L.m("create from parcel :Post");
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    private int dbid_post;
    private String body_content;
    private Date date_added;
    private String user_name;
    private String user_create_id;
    private int dbid_user_pic;
    private int degree_id;
    private int rate;
    private String typePost;
    private int totalScore;
    private int totalComments;
    private int user_choice;

    private String degree_name;
    private String faculty_name;
    private String school_name;

    public Post() {

    }

    public Post(Parcel input) {
        dbid_post = input.readInt();
        body_content = input.readString();

        long dateMillis=input.readLong();
        date_added = (dateMillis == -1 ? null : new Date(dateMillis));

        user_create_id = input.readString();
        dbid_user_pic = input.readInt();
        user_name = input.readString();
        degree_id = input.readInt();
        rate = input.readInt();
        typePost = input.readString();
        totalScore = input.readInt();
        totalComments = input.readInt();
        user_choice = input.readInt();

        degree_name = input.readString();
        faculty_name = input.readString();
        school_name = input.readString();

    }

    public Post( int dbid_post,
             String body_content,
             Date date_added,
             String user_create_id,
             int dbid_user_pic,
             String user_name,
             int degree_id,
             int rate,
             String typePost,
             int totalScore,
             int totalComments,
             int user_choice,
             String degree_name,
             String faculty_name,
             String school_name) {

        this.dbid_post = dbid_post;
        this.body_content = body_content;
        this.date_added = date_added;
        this.user_create_id = user_create_id;
        this.dbid_user_pic = dbid_user_pic;
        this.user_name = user_name;
        this.degree_id = degree_id;
        this.rate = rate;
        this.typePost = typePost;
        this.totalScore = totalScore;
        this.totalComments = totalComments;
        this.user_choice = user_choice;

        this.degree_name = degree_name;
        this.faculty_name = faculty_name;
        this.school_name = school_name;
    }

    public int getDbid_user_pic() {
        return dbid_user_pic;
    }

    public void setDbid_user_pic(int dbid_user_pic) {
        this.dbid_user_pic = dbid_user_pic;
    }

    public String getDegree_name() {
        return degree_name;
    }

    public void setDegree_name(String degree_name) {
        this.degree_name = degree_name;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public int getUser_choice() {
        return user_choice;
    }

    public void setUser_choice(int user_choice) {
        this.user_choice = user_choice;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public static Creator<Post> getCREATOR() {
        return CREATOR;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getUser_create_id() {
        return user_create_id;
    }

    public void setUser_create_id(String user_create_id) {
        this.user_create_id = user_create_id;
    }

    public int getDbid_post() {
        return dbid_post;
    }

    public void setDbid_post(int dbid_post) {
        this.dbid_post = dbid_post;
    }

    public String getBody_content() {
        return body_content;
    }

    public void setBody_content(String body_content) {
        this.body_content = body_content;
    }

    public Date getDate_added() {
        return date_added;
    }

    public void setDate_added(Date date_added) {
        this.date_added = date_added;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getDegree_id() {
        return degree_id;
    }

    public void setDegree_id(int degree_id) {
        this.degree_id = degree_id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getTypePost() {
        return typePost;
    }

    public void setTypePost(String typePost) {
        this.typePost = typePost;
    }

    @Override
    public String toString() {
        return "\ndbid_post: " + dbid_post +
                "\nbody_content " + body_content +
                "\ndate_added " + date_added +
                "\nuser_id " + user_create_id +
                "\ndbid_user_pic" + dbid_user_pic +
                "\nuser_name " + user_name +
                "\ndegree_id " + degree_id +
                "\nrate  " + rate +
                "\ntypePost " + typePost +
                "\ntotalScore " + totalScore +
                "\ntotalComments " + totalComments +
                "\nuser choice " + user_choice +
                "\ndegree name " + degree_name +
                "\nfaculty name " + faculty_name +
                "\nschool name " + school_name +
                "\n";


    }

    @Override
    public int describeContents() {
//        L.m("describe Contents Movie");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dbid_post);
        dest.writeString(body_content);
        dest.writeLong(date_added == null ? -1 : date_added.getTime());
        dest.writeString(user_create_id);
        dest.writeInt(dbid_user_pic);
        dest.writeString(user_name);
        dest.writeInt(degree_id);
        dest.writeInt(rate);
        dest.writeString(typePost);
        dest.writeInt(totalScore);
        dest.writeInt(totalComments);
        dest.writeInt(user_choice);

        dest.writeString(degree_name);
        dest.writeString(faculty_name);
        dest.writeString(school_name);
    }
}
