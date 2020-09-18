package com.studentadvisor.noam.studentadvisor.extras;

/**
 * Created by Noam on 11/7/2015.
 */
public interface Keys {
    public interface EndpointDegrees{
        public static final String KEY_ALL_DEGREES="degrees";
        public static final String KEY_ALL_POSTS="posts";
        public static final String KEY_ALL_COMMENTS="comments";
        public static final String KEY_ALL_SUBJECTS="subjects";
        public static final String KEY_DEGREE_EXTRA_INFO="extra_degree";
        public static final String KEY_MY_POST="post_rate_extra";
        public static final String KEY_ALL_FOLLOWES="followed_degrees";
        public static final String KEY_ALL_LIKES="liked_degrees";


        //Degrees
        public static final String KEY_DEGREE_ID="dbid_degree";
        public static final String KEY_FACULTY_NAME="faculty_hebrew";
        public static final String KEY_DEGREE_NAME="degree_name";
        public static final String KEY_SCHOOL_NAME_HE="school_name_he";
        public static final String KEY_SCHOOL_NAME_EN="school_name_en";
        public static final String KEY_SCHOOL_URL_WEBSITE = "school_url_website";
        public static final String KEY_DEGREE_LIKES="likes";
        public static final String KEY_DEGREE_FOLLOWS="total_follows";
        public static final String KEY_SUBJECT_1="subject_1";
        public static final String KEY_SUBJECT_2="subject_2";
        public static final String KEY_SUBJECT_3="subject_3";
        public static final String KEY_SUBJECT_4="subject_4";
        public static final String KEY_URL_LOGO = "logo_pic";
        public static final String KEY_URL_HEADER= "header_pic";

        //Subjects
        public static final String KEY_SUBJECT_ID="dbid_subject";
        public static final String KEY_SUBJECT_NAME="subject_name";

        //likes
        public static final String TAG_SUCCESS = "success";
        public static final String TAG_MESSAGE = "message";

        //extra info
        public static final String TAG_NUM_FOLLOWES = "num_follows";
        public static final String TAG_NUM_RATED = "num_rated";
        public static final String TAG_NUM_POSTS = "num_posts";
        public static final String TAG_NUM_LIKES = "num_likes";
        public static final String TAG_IF_LIKED = "if_liked";
        public static final String TAG_IF_POST_RATE = "if_post_rate";
        public static final String TAG_IF_FOLLOWED = "if_followed";
        public static final String TAG_DBID_POST = "dbid_post";
        public static final String TAG_DEGREE_ID = "degree_id";
        public static final String TAG_USER_CREATE_ID = "user_create_id";
        public static final String TAG_CREATED_AT = "created_at";
        public static final String TAG_BODY_CONTENT = "body_content";
        public static final String TAG_RATE = "rate";
        public static final String TAG_TOTAL_SCORE = "total_score";
        public static final String TAG_TOTAL_COMMENTS = "counter_comments";
        public static final String TAG_NAME = "name";
        public static final String TAG_TYPE_POST = "type_post";
        public static final String TAG_VOTE_CHOICE = "vote_choice";
        public static final String TAG_SCORE_RATE = "score_rate";
        public static final String TAG_DBID_PIC = "dbid_user_pic";

        //posts
        public static final String TAG_POST_DEGREE_NAME = "degree_name";
        public static final String TAG_POST_FACULTY_NAME= "faculty_name";
        public static final String TAG_POST_SCHOOL_NAME = "school_name";


        //comments
        public static final String TAG_COMMENT_ID = "dbid_comment";
        public static final String TAG_COMMENT_ID_POST = "uid_post";
        public static final String TAG_COMMENT_ID_USER = "user_id";
        public static final String TAG_COMMENT_USER_NAME = "user_name";
        public static final String TAG_COMMENT_BODY = "body_content";
        public static final String TAG_COMMENT_DATE = "created_at";

        //followes
        public static final String TAG_FOLLOW_FOLLOWED_DEGREE = "uid_followed_degree";
        public static final String TAG_FOLLOW_TOTAL_FOLLOWES = "total_follows";

        //likes
        public static final String TAG_LIKE_LIKED_DEGREE = "uid_liked_degree";
        public static final String TAG_LIKE_TOTAL_LIKES = "total_likes";

    }

    public interface EndpointsBundles{
        public static final String LIST_SELECTED_SUBJECTS = "list_selected_subjects";
        public static final String SELECTED_DEGREE = "selected_degree";
        public static final String LIST_NUM_SELECTED_SUBJECTS = "num_list_selected_subjects";

        public static final String PARCEL_POST_IN_EXTRA_DEGREE = "parcel_post_in_extra_degree";

        public static final String PARCEL_NEW_POST_DEGREE = "parcel_post_in_degree";
        public static final String PARCEL_NEW_POST_EXTRA_DEGREE = "parcel_post_in_extra_degree";
        public static final String PARCEL_WRITE_POST_TYPE = "write_post_type";


        public static final String USER_ID = "user_id_shared";
        public static final String NEWS_FEED_INITIAL_TAB = "news_feed_initial_tab";

    }

    public interface DBDegrees{
        //        public static final String TABLE_UPCOMING = " Degrees_upcoming";
        public static final String TABLE_ALL_DEGREES = "tb_all_degrees";
        public static final String COLUMN_ID_DEGREE = "dbid_degree";
        public static final String COLUMN_FACULTY_NAME = "faculty_name";
        public static final String COLUMN_SCHOOL_NAME_HE = "school_name_he";
        public static final String COLUMN_SCHOOL_NAME_EN = "school_name_en";
        public static final String COLUMN_DEGREE_NAME = "degree_name";
        //        public static final String COLUMN_URL_THUMBNAIL = "url_thumbnail";
        public static final String COLUMN_SUBJECT_1 = "subject_1";
        public static final String COLUMN_SUBJECT_2 = "subject_2";
        public static final String COLUMN_SUBJECT_3 = "subject_3";
        public static final String COLUMN_SUBJECT_4 = "subject_4";
        public static final String COLUMN_DEGREE_LIKES = "degree_likes";
        public static final String COLUMN_DEGREE_FOLLOWES = "degree_total_followes";
        public static final String COLUMN_URL_LOGO_SCHOOL = "url_logo_school";
        public static final String COLUMN_URL_HEADER_SCHOOL = "url_header_school";

        //subjects
        public static final String TABLE_ALL_SUBJECTS = "tb_all_subjects";
        public static final String COLUMN_ID_SUBJECT = "id_subject";
        public static final String COLUMN_SUBJECT_NAME = "subject_name";

        //schools
        public static final String TABLE_ALL_SCHOOLS = "tb_all_schools";
        public static final String COLUMN_ID_SCHOOL = "id_school";
        public static final String COLUMN_URL_WEB_SCHOOL = "web_site";

    }

    public interface DBComments{
        //        public static final String TABLE_UPCOMING = " Degrees_upcoming";
        public static final String TABLE_ALL_COMMENTS = "tb_all_comments";
        public static final String COLUMN_COMMENT_POST_ID = "dbid_post";
        public static final String COLUMN_COMMENT_BODY_CONTENT = "body_content";
        public static final String COLUMN_COMMENT_DATE_ADDED = "date_added";
        public static final String COLUMN_COMMENT_USER_NAME = "user_name";
        public static final String COLUMN_COMMENT_USER_ID = "user_create_id";
        public static final String COLUMN_COMMENT_USER_PIC_ID = "dbid_user_pic";
        public static final String COLUMN_COMMENT_DEGREE_ID = "degree_id";
        public static final String COLUMN_COMMENT_RATE = "rate";
        public static final String COLUMN_COMMENT_TYPE_POST = "typePost";
        public static final String COLUMN_COMMENT_TOTAL_SCORE = "totalScore";
        public static final String COLUMN_COMMENT_TOTAL_COMMENTS = "totalComments";
        public static final String COLUMN_COMMENT_USER_CHOICE = "user_choice";
        public static final String COLUMN_COMMENT_DEGREE_NAME = "degree_name";
        public static final String COLUMN_COMMENT_FACULTY_NAME = "faculty_name";
        public static final String COLUMN_COMMENT_SCHOOL_NAME = "school_name";
        public static final String COLUMN_COMMENT_USER_LOGIN_ID = "user_login_id";

    }

    public interface SettingsKeys {
        public static final String KEY_BUNDLE_USER_TYPE = "key_bundle_user_type";
        public static final String KEY_BUNDLE_USER_YEAR = "key_bundle_user_year";
        public static final String KEY_BUNDLE_PROFILE_IMAGE_ID = "key_bundle_profile_image_id";
        public static final String KEY_BUNDLE_USER_DEGREE_1 = "key_bundle_user_degree_1";
        public static final String KEY_BUNDLE_USER_DEGREE_2 = "key_bundle_user_degree_2";
        public static final String KEY_BUNDLE_USER_DEGREE_3 = "key_bundle_user_degree_3";

    }

    public interface SharedPreferencesKeys{
        public static final String KEY_PREFERENCES_EMAIL = "key_preferences_email";
    }
}
