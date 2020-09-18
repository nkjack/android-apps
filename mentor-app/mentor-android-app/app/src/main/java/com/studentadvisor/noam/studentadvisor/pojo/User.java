package com.studentadvisor.noam.studentadvisor.pojo;

/**
 * Created by Noam on 12/18/2015.
 */
public class User {
    private boolean loggedIn;
    private String user_id;
    private String user_name;
    private int user_pic_id;
    private String user_type;
    private int user_year;
    private int degree_1;
    private int degree_2;
    private int degree_3;

    public User(boolean loggedIn, String user_id, String user_name, int user_pic_id,
                String user_type, int user_year, int degree_1, int degree_2, int degree_3) {
        this.loggedIn = loggedIn;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_pic_id = user_pic_id;
        this.user_type = user_type;
        this.user_year = user_year;
        this.degree_1 = degree_1;
        this.degree_2 = degree_2;
        this.degree_3 = degree_3;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
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

    public int getUser_pic_id() {
        return user_pic_id;
    }

    public void setUser_pic_id(int user_pic_id) {
        this.user_pic_id = user_pic_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public int getUser_year() {
        return user_year;
    }

    public void setUser_year(int user_year) {
        this.user_year = user_year;
    }

    public int getDegree_1() {
        return degree_1;
    }

    public void setDegree_1(int degree_1) {
        this.degree_1 = degree_1;
    }

    public int getDegree_2() {
        return degree_2;
    }

    public void setDegree_2(int degree_2) {
        this.degree_2 = degree_2;
    }

    public int getDegree_3() {
        return degree_3;
    }

    public void setDegree_3(int degree_3) {
        this.degree_3 = degree_3;
    }
}
