package com.studentadvisor.noam.studentadvisor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.studentadvisor.noam.studentadvisor.logging.L;

/**
 * Created by Noam on 11/8/2015.
 */
public class Subject implements Parcelable{
    public static final Parcelable.Creator<Subject> CREATOR
            = new Parcelable.Creator<Subject>() {
        public Subject createFromParcel(Parcel in) {
            L.m("create from parcel :Movie");
            return new Subject(in);
        }

        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    private int dbid_subject;
    private String subject_name;

    public Subject() {
    }

    public Subject(int dbid_subject, String subject_name) {
        this.dbid_subject = dbid_subject;
        this.subject_name = subject_name;
    }

    public Subject(Parcel input) {
        this.dbid_subject = input.readInt();
        this.subject_name = input.readString();
    }

    public int getDbid_subject() {
        return dbid_subject;
    }

    public void setDbid_subject(int dbid_subject) {
        this.dbid_subject = dbid_subject;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "dbid_subject=" + dbid_subject +
                ", subject_name='" + subject_name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dbid_subject);
        dest.writeString(subject_name);
    }
}
