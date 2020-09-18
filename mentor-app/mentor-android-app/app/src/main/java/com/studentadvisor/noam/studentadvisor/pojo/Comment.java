package com.studentadvisor.noam.studentadvisor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.studentadvisor.noam.studentadvisor.logging.L;

import java.util.Date;

/**
 * Created by Noam on 12/11/2015.
 */
public class Comment implements Parcelable {
    public static final Parcelable.Creator<Comment> CREATOR
            = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            L.m("create from parcel :Comment");
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    private int dbid_comment;
    private int uid_post;
    private String user_id;
    private int dbid_user_pic;
    private String user_name;
    private String body_content;
    private Date date_added;

    public Comment() {

    }

    public Comment(Parcel input) {
        dbid_comment = input.readInt();
        uid_post = input.readInt();
        user_id = input.readString();
        dbid_user_pic = input.readInt();
        user_name = input.readString();
        body_content = input.readString();

        long dateMillis = input.readLong();
        date_added = (dateMillis == -1 ? null : new Date(dateMillis));
    }

    public Comment(int dbid_comment,
                   int uid_post,
                   String user_id,
                   int dbid_user_pic,
                   String user_name,
                   String body_content,
                   Date date_added) {

        this.dbid_comment = dbid_comment;
        this.uid_post = uid_post;
        this.user_id = user_id;
        this.dbid_user_pic = dbid_user_pic;
        this.user_name = user_name;
        this.body_content = body_content;
        this.date_added = date_added;
    }

    public int getDbid_user_pic() {
        return dbid_user_pic;
    }

    public void setDbid_user_pic(int dbid_user_pic) {
        this.dbid_user_pic = dbid_user_pic;
    }

    public static Creator<Comment> getCREATOR() {
        return CREATOR;
    }

    public int getDbid_comment() {
        return dbid_comment;
    }

    public void setDbid_comment(int dbid_comment) {
        this.dbid_comment = dbid_comment;
    }

    public int getUid_post() {
        return uid_post;
    }

    public void setUid_post(int uid_post) {
        this.uid_post = uid_post;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    @Override
    public String toString() {
        return "\ndbid_comment: " + dbid_comment +
                "\nuid_post " + uid_post +
                "\nuser_id " + user_id +
                "\ndbid_user_pic " + dbid_user_pic +
                "\nuser_name " + user_name +
                "\nbody_content " + body_content +
                "\ndate_added " + date_added +
                "\n";
    }

    @Override
    public int describeContents() {
//        L.m("describe Contents Movie");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dbid_comment);
        dest.writeInt(uid_post);
        dest.writeString(user_id);
        dest.writeInt(dbid_user_pic);
        dest.writeString(user_name);
        dest.writeString(body_content);
        dest.writeLong(date_added == null ? -1 : date_added.getTime());
    }
}
