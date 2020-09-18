package com.studentadvisor.noam.studentadvisor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.studentadvisor.noam.studentadvisor.logging.L;

import java.util.Date;

/**
 * Created by Noam on 11/7/2015.
 */
public class Degree implements Parcelable {
    public static final Parcelable.Creator<Degree> CREATOR
            = new Parcelable.Creator<Degree>() {
        public Degree createFromParcel(Parcel in) {
            L.m("create from parcel :Degree");
            return new Degree(in);
        }

        public Degree[] newArray(int size) {
            return new Degree[size];
        }
    };
    private int dbid_degree;
    private String faculty_name;
    private String degree_name;
    private String school_name_he;
    private String school_name_en;
    private String school_url_web;
    //    private Date releaseDateTheater;
    private int likes;
    private int followes;
    private String subject_1;
    private String subject_2;
    private String subject_3;
    private String subject_4;
    private String urlLogoPic;
    private String urlHeaderPic;


    public Degree() {

    }

    public Degree(Parcel input) {
        dbid_degree = input.readInt();
        faculty_name = input.readString();
        degree_name = input.readString();
        school_name_he = input.readString();
        school_name_en = input.readString();
        school_url_web = input.readString();
//        long dateMillis=input.readLong();
//        releaseDateTheater = (dateMillis == -1 ? null : new Date(dateMillis));
        likes = input.readInt();
        followes = input.readInt();
        subject_1 = input.readString();
        subject_2 = input.readString();
        subject_3 = input.readString();
        subject_4 = input.readString();
        urlLogoPic = input.readString();
        urlHeaderPic = input.readString();
    }

    public Degree(int idDegree,
                  String facultyName,
                  String degree_name,
                  String school_name_he,
                  String school_name_en,
                  String school_url_web,
                  int likes,
                  int followes,
                  String subject_1,
                  String subject_2,
                  String subject_3,
                  String subject_4,
                  String urlLogoPic,
                  String urlHeaderPic) {
        this.dbid_degree = idDegree;
        this.faculty_name = facultyName;
        this.degree_name = degree_name;
        this.school_name_he = school_name_he;
        this.school_name_en = school_name_en;
        this.school_url_web = school_url_web;
        this.likes = likes;
        this.followes = followes;
        this.subject_1 = subject_1;
        this.subject_2 = subject_2;
        this.subject_3 = subject_3;
        this.subject_4 = subject_4;
        this.urlLogoPic = urlLogoPic;
        this.urlHeaderPic = urlHeaderPic;
    }

    public int getFollowes() {
        return followes;
    }

    public void setFollowes(int followes) {
        this.followes = followes;
    }

    public static Creator<Degree> getCREATOR() {
        return CREATOR;
    }

    public int getDbid_degree() {
        return dbid_degree;
    }

    public void setDbid_degree(int dbid_degree) {
        this.dbid_degree = dbid_degree;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }

    public String getDegree_name() {
        return degree_name;
    }

    public void setDegree_name(String degree_name) {
        this.degree_name = degree_name;
    }

    public int getLikes() {
        return likes;
    }

    public String getSchool_name_en() {
        return school_name_en;
    }

    public void setSchool_name_en(String school_name_en) {
        this.school_name_en = school_name_en;
    }

    public String getSchool_name_he() {
        return school_name_he;
    }

    public void setSchool_name_he(String school_name_he) {
        this.school_name_he = school_name_he;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getSubject_1() {
        return subject_1;
    }

    public void setSubject_1(String subject_1) {
        this.subject_1 = subject_1;
    }

    public String getSubject_2() {
        return subject_2;
    }

    public void setSubject_2(String subject_2) {
        this.subject_2 = subject_2;
    }

    public String getSubject_3() {
        return subject_3;
    }

    public void setSubject_3(String subject_3) {
        this.subject_3 = subject_3;
    }

    public String getSubject_4() {
        return subject_4;
    }

    public void setSubject_4(String subject_4) {
        this.subject_4 = subject_4;
    }

    public String getUrlLogoPic() {
        return urlLogoPic;
    }

    public void setUrlLogoPic(String urlLogoPic) {
        this.urlLogoPic = urlLogoPic;
    }

    public String getUrlHeaderPic() {
        return urlHeaderPic;
    }

    public void setUrlHeaderPic(String urlHeaderPic) {
        this.urlHeaderPic = urlHeaderPic;
    }

    public String getSchool_url_web() {
        return school_url_web;
    }

    public void setSchool_url_web(String school_url_web) {
        this.school_url_web = school_url_web;
    }

    @Override
    public String toString() {
        return "\nDBID DEGREE: " + dbid_degree +
                "\nfaculty name " + faculty_name +
                "\nDegree name " + degree_name +
                "\nSchool name hebrew " + school_name_he +
                "\nSchool name english " + school_name_en +
                "\nSchool url " + school_url_web +
                "\nLikes " + likes +
                "\nFollowes " + followes +
                "\nurl logo pic " + urlLogoPic +
                "\nurl header pic " + urlHeaderPic +
                "\nSubject 1 " + subject_1 +
                "\nSubject 2 " + subject_2 +
                "\nSubject 3 " + subject_3 +
                "\nSubject 4 " + subject_4 +
                "\n";
    }

    @Override
    public int describeContents() {
//        L.m("describe Contents Movie");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        L.m("writeToParcel Movie");
        dest.writeInt(dbid_degree);
        dest.writeString(faculty_name);
        dest.writeString(degree_name);
        dest.writeString(school_name_he);
        dest.writeString(school_name_en);
        dest.writeString(school_url_web);
        dest.writeInt(likes);
        dest.writeInt(followes);
//        dest.writeString(urlThumbnail);
        dest.writeString(subject_1);
        dest.writeString(subject_2);
        dest.writeString(subject_3);
        dest.writeString(subject_4);
        dest.writeString(urlLogoPic);
        dest.writeString(urlHeaderPic);

    }
}
