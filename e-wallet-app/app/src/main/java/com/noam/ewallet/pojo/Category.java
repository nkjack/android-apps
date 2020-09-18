package com.noam.ewallet.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.noam.ewallet.logging.L;

/**
 * Created by Noam on 3/29/2017.
 */
public class Category implements Parcelable {
    public static final Parcelable.Creator<Category> CREATOR
            = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            L.m("create from parcel :Degree");
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private int dbid_category;
    private String category_name;
    private int category_budget;

    public Category() {

    }
    public Category(Parcel input){
        dbid_category = input.readInt();
        category_name = input.readString();
        category_budget = input.readInt();
    }

    public Category(int idCategory,
                    String categoryName,
                    int categoryBudget) {
        this.dbid_category = idCategory;
        this.category_name = categoryName;
        this.category_budget = categoryBudget;
    }

    public int getDbid_category() {
        return dbid_category;
    }

    public void setDbid_category(int dbid_category) {
        this.dbid_category = dbid_category;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCategory_budget() {
        return category_budget;
    }

    public void setCategory_budget(int category_budget) {
        this.category_budget = category_budget;
    }

    @Override
    public String toString() {
        return "\nDBID Category: " + dbid_category +
                "\ncategory name " + category_name +
                "\ncategory budget " + category_budget +
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
        dest.writeInt(dbid_category);
        dest.writeString(category_name);
        dest.writeInt(category_budget);


    }

}
