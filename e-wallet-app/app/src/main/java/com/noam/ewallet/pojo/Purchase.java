package com.noam.ewallet.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.noam.ewallet.logging.L;

/**
 * Created by Noam on 3/29/2017.
 */
public class Purchase implements Parcelable{
    public static final Parcelable.Creator<Purchase> CREATOR
            = new Parcelable.Creator<Purchase>() {
        public Purchase createFromParcel(Parcel in) {
            L.m("create from parcel :Degree");
            return new Purchase(in);
        }

        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };

    private int dbid_purchase;
    private String category_name;
    private int category_key;
    private String sub_category_name;
    private int sub_category_key;
    private int purchase_price;
    private long date_time;
    private String purchase_comment;

    public Purchase() {

    }
    public Purchase(Parcel input){
        dbid_purchase = input.readInt();
        category_name = input.readString();
        category_key = input.readInt();
        sub_category_name = input.readString();
        sub_category_key = input.readInt();
        purchase_price= input.readInt();
        date_time = input.readLong();
        purchase_comment = input.readString();
    }

    public Purchase(int idPurchase,
                  String categoryName,
                  int categoryKey,
                  String subCategoryName,
                  int subCategoryKey,
                  int purchasePrice,
                  long dateTime,
                  String purchaseComment) {
        this.dbid_purchase = idPurchase;
        this.category_name = categoryName;
        this.category_key = categoryKey;
        this.sub_category_name = subCategoryName;
        this.sub_category_key = subCategoryKey;
        this.purchase_price= purchasePrice;
        this.date_time = dateTime;
        this.purchase_comment = purchaseComment;
    }


    public int getDbid_purchase() {
        return dbid_purchase;
    }

    public void setDbid_purchase(int dbid_purchase) {
        this.dbid_purchase = dbid_purchase;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCategory_key() {
        return category_key;
    }

    public void setCategory_key(int category_key) {
        this.category_key = category_key;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public int getSub_category_key() {
        return sub_category_key;
    }

    public void setSub_category_key(int sub_category_key) {
        this.sub_category_key = sub_category_key;
    }

    public int getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(int purchase_price) {
        this.purchase_price = purchase_price;
    }

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public String getPurchase_comment() {
        return purchase_comment;
    }

    public void setPurchase_comment(String purchase_comment) {
        this.purchase_comment = purchase_comment;
    }

    @Override
    public String toString() {
        return "\nDBID Purchase: " + dbid_purchase +
                "\ncategory name " + category_name +
                "\ncategory id " + category_key +
                "\nsub category name " + sub_category_name +
                "\nsub category id " + sub_category_key +
                "\npurchase price " + purchase_price +
                "\ntime " + date_time +
                "\ncomment " + purchase_comment +
                "\n";
    }

    public void logData(){
        L.m("\nDBID Purchase: " + dbid_purchase +
                "\ncategory name " + category_name +
                "\ncategory id " + category_key +
                "\nsub category name " + sub_category_name +
                "\nsub category id " + sub_category_key +
                "\npurchase price " + purchase_price +
                "\ntime " + date_time +
                "\ncomment " + purchase_comment +
                "\n");
    }

    @Override
    public int describeContents() {
//        L.m("describe Contents Movie");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        L.m("writeToParcel Movie");
        dest.writeInt(dbid_purchase);
        dest.writeString(category_name);
        dest.writeInt(category_key);
        dest.writeString(sub_category_name);
        dest.writeInt(sub_category_key);
        dest.writeInt(purchase_price);
        dest.writeLong(date_time);
        dest.writeString(purchase_comment);


    }

}
