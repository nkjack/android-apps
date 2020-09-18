package com.noam.ewallet.db;

import android.provider.BaseColumns;

/**
 * Created by Noam on 3/29/2017.
 */

public class TableData {
    public TableData() {
    }

    public static abstract class Library implements BaseColumns
    {
        public static final String TABLE_BUY_ID = "buy_id";
        public static final String CATEGORY_KEY = "category_key";
        public static final String SUB_CATEGORY_KEY = "sub_category_key";
        public static final String PRICE = "price";
        public static final String DATE_TIME = "date_time";
        public static final String COMMENT = "comment";


        public static final String DATABASE_NAME = "ewallet_db";
        public static final String TABLE_NAME_BUYS = "buys_table";
        public static final String TABLE_NAME_CATEGORY = "category_table";
        public static final String TABLE_NAME_SUB_CATEGORY = "sub_category_table";
    }
}
