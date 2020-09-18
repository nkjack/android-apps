package com.studentadvisor.noam.studentadvisor.extras;

import android.graphics.Color;

/**
 * Created by Noam on 11/7/2015.
 */
public interface Constants {
    String NA = "NA";
    String TAG_CODE = "TAG";

    //----------------- separator TagView-----------------//
    public static final float DEFAULT_LINE_MARGIN = 5;
    public static final float DEFAULT_TAG_MARGIN = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;

    public static final float LAYOUT_WIDTH_OFFSET = 2;

    //----------------- separator Tag Item-----------------//
    public static final float DEFAULT_TAG_TEXT_SIZE = 12f;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f;
    public static final float DEFAULT_TAG_RADIUS = 100;
    public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#AED374");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#88363636");
    public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;

    //-------------------STATES --------------------------//

    //The key used to store arraylist of degrees objects to and from parcelable
    public static final String STATE_DEGREE = "state_degree";
    public static final String STATE_DEGREE_PARCEL = "state_degree_parcel";
    public static final String STATE_EXTRA_DEGREE_PARCEL = "state_extra_degree_parcel";


    //--------------------TABS-------------------------//

    public static final int TAB_HOME = 0;
    public static final int TAB_COMMENTS = 1;
    public static final int TAB_WEB = 2;
    public static final int TAB_EXPLORE_POSTS = 3;
    public static final int TAB_COUNT = 3;

}
