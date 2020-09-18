package com.studentadvisor.noam.studentadvisor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.studentadvisor.noam.studentadvisor.logging.L;

/**
 * Created by Noam on 11/12/2015.
 */
public class School implements Parcelable {
    public static final Parcelable.Creator<School> CREATOR
            = new Parcelable.Creator<School>() {
        public School createFromParcel(Parcel in) {
            L.m("create from parcel :School");
            return new School(in);
        }

        public School[] newArray(int size) {
            return new School[size];
        }
    };

    private int dbid_school;
    private String school_name_he;
    private String school_name_en;
    private String scool_url_website;

    public School() {
    }

    public School(int dbid_school, String school_name_en, String school_name_he, String scool_url_website) {
        this.dbid_school = dbid_school;
        this.school_name_he = school_name_he;
        this.school_name_en = school_name_en;
        this.scool_url_website = scool_url_website;
    }

    public School(Parcel input) {
        this.dbid_school = input.readInt();
        this.school_name_he = input.readString();
        this.school_name_en = input.readString();
        this.scool_url_website = input.readString();
    }

    public String getScool_url_website() {
        return scool_url_website;
    }

    public void setScool_url_website(String scool_url_website) {
        this.scool_url_website = scool_url_website;
    }

    public int getDbid_school() {
        return dbid_school;
    }

    public void setDbid_school(int dbid_school) {
        this.dbid_school = dbid_school;
    }

    public String getSchool_name_he() {
        return school_name_he;
    }

    public void setSchool_name_he(String school_name_he) {
        this.school_name_he = school_name_he;
    }

    public String getSchool_name_en() {
        return school_name_en;
    }

    public void setSchool_name_en(String school_name_en) {
        this.school_name_en = school_name_en;
    }

    @Override
    public String toString() {
        return "School{" +
                "dbid_school=" + dbid_school +
                ", school_name_he ='" + school_name_he + '\'' +
                ", school_name_en ='" + school_name_en + '\'' +
                ", url_website ='" + scool_url_website + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dbid_school);
        dest.writeString(school_name_he);
        dest.writeString(school_name_en);
        dest.writeString(scool_url_website);
    }
}
