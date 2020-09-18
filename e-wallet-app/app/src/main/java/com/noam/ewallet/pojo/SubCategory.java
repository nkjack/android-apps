package com.noam.ewallet.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.noam.ewallet.logging.L;

/**
 * Created by Noam on 3/29/2017.
 */
public class SubCategory implements Parcelable {
    public static final Parcelable.Creator<SubCategory> CREATOR
            = new Parcelable.Creator<SubCategory>() {
        public SubCategory createFromParcel(Parcel in) {
            L.m("create from parcel :Degree");
            return new SubCategory(in);
        }

        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    private int dbid_sub_category;
    private String sub_category_name;
    private int category_id;
    private int sub_category_budget;

    public SubCategory() {

    }
    public SubCategory(Parcel input){
        dbid_sub_category = input.readInt();
        sub_category_name = input.readString();
        category_id = input.readInt();
        sub_category_budget = input.readInt();
    }

    public SubCategory(int idSubCategory,
                    String SubCategoryName,
                       int categoryID,
                       int subCategoryBudget) {
        this.dbid_sub_category= idSubCategory;
        this.sub_category_name = SubCategoryName;
        this.category_id = categoryID;
        this.sub_category_budget = subCategoryBudget;
    }

    public int getDbid_sub_category() {
        return dbid_sub_category;
    }

    public void setDbid_sub_category(int dbid_sub_category) {
        this.dbid_sub_category = dbid_sub_category;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getSub_category_budget() {
        return sub_category_budget;
    }

    public void setSub_category_budget(int sub_category_budget) {
        this.sub_category_budget = sub_category_budget;
    }

    @Override
    public String toString() {
        return "\nDBID SubCategory: " + dbid_sub_category +
                "\nSubCategory name " + sub_category_name +
                "\nSubCategory budget " + sub_category_budget+
                "\nCategory ID " + category_id+
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
        dest.writeInt(dbid_sub_category);
        dest.writeString(sub_category_name);
        dest.writeInt(category_id);
        dest.writeInt(sub_category_budget);


    }

}
