package com.noam.ewallet.extras;

/**
 * Created by Noam on 3/29/2017.
 */
public interface Keys {
    public interface DBWallet{

        // fields for buys_table
    public static final String COLUMN_TB_WALLET_ID = "purchase_id";
        public static final String COLUMN_CATEGORY_KEY = "category_key";
        public static final String COLUMN_SUB_CATEGORY_KEY = "sub_category_key";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_COMMENT = "comment";

        // fields for category_table
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_CATEGORY_NAME = "name_category";
        public static final String COLUMN_CATEGORY_BUDGET = "budget_category";

        // fields for sub_category_table
        public static final String COLUMN_SUB_CATEGORY_ID = "sub_category_id";
        public static final String COLUMN_NAME_SUB_CAT = "name_sub_category";
        public static final String COLUMN_CATEGORY_ID_FK= "category_id_fk";
        public static final String COLUMN_SUB_BUDGET= "budget_sub_category";

        public static final String DATABASE_WALLET_NAME = "ewallet_db";
        public static final String TABLE_NAME_WALLET = "wallet_table";
        public static final String TABLE_NAME_CATEGORY = "category_table";
        public static final String TABLE_NAME_SUB_CATEGORY = "sub_category_table";
    }

    public interface EndpointsBundles{
        public static final String SELECTED_CATEGORY = "selected_category";

    }
}
