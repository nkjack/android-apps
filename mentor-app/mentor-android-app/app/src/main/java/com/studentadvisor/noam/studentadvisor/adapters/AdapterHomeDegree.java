package com.studentadvisor.noam.studentadvisor.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.extras.Constants;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.ExtraInfoDegree;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;

import java.util.ArrayList;

/**
 * Created by Noam on 11/28/2015.
 */
public class AdapterHomeDegree extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    private ArrayList<String> list = new ArrayList<>();
    private LayoutInflater inflater;
    private ExtraInfoDegree mExtraInfoDegree;
    private Degree mDegree;
    private final static int NEW_POST_POSITION = 0;
    private final static int HOME_CONTENT_POSITION = 1;
    private final static int POPULAR_POST_POSITION = 2;
    private int amountChildren = 0;
    private ImageLoader mImageLoader;
    private VolleySingleton mVolleySingleton;


    public AdapterHomeDegree(Context context) {
        inflater = LayoutInflater.from(context);
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();

    }

    public void setExtraInfoDegree(ExtraInfoDegree extraInfoDegree, Degree degree) {
        this.mExtraInfoDegree = extraInfoDegree;
        this.mDegree = degree;

        Post temp = mExtraInfoDegree.getPost();
        if (temp.getBody_content().contentEquals(Constants.NA)) {
            amountChildren = 2;
        }
        else{
            amountChildren = 3;
        }
        //update the adapter to reflect the new set of Degrees
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = -1;
        switch (position) {
            case 0:
                itemType = NEW_POST_POSITION;
                break;
            case 1:
                itemType = HOME_CONTENT_POSITION;
                break;
            case 2:
                itemType = POPULAR_POST_POSITION;
                break;
        }
        return itemType;
//        return position == 0 ? NEW_POST_POSITION : HOME_CONTENT_POSITION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == NEW_POST_POSITION) {
            View root = inflater.inflate(R.layout.degree_home_new_post_component, viewGroup, false);
            RecyclerView.ViewHolder holder = new MyHomeDegreeNewPostViewHolder(root);
            return holder;
        } else if (i == HOME_CONTENT_POSITION) {
            View root = inflater.inflate(R.layout.degree_home_component_header, viewGroup, false);
            RecyclerView.ViewHolder holder = new MyHomeDegreeViewHolder(root);
            return holder;
        } else {
            View root = inflater.inflate(R.layout.degree_popular_post_card_component, viewGroup, false);
            RecyclerView.ViewHolder holder = new ViewHolderHomePost(root);
            return holder;
        }
    }

    public void changeNumLikes(int amount) {
        mExtraInfoDegree.setNumLikes(mExtraInfoDegree.getNumLikes() + amount);
        if (mExtraInfoDegree.getNumLikes() < 0) {
            mExtraInfoDegree.setNumLikes(0);
        }
        notifyDataSetChanged();
    }

    public void changeNumFollows(int amount) {
        mExtraInfoDegree.setNumFollowes(mExtraInfoDegree.getNumFollowes() + amount);
        if (mExtraInfoDegree.getNumFollowes() < 0) {
            mExtraInfoDegree.setNumFollowes(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int i) {
        if (mHolder instanceof MyHomeDegreeViewHolder) {

            MyHomeDegreeViewHolder holder = (MyHomeDegreeViewHolder) mHolder;

            holder.degreeName.setText(mDegree.getDegree_name());
            holder.facultyName.setText(mDegree.getFaculty_name());
            holder.numLikes.setText(mExtraInfoDegree.getNumLikes() + "");
            holder.numFollowes.setText(mExtraInfoDegree.getNumFollowes() + "");
            holder.numRated.setText(mExtraInfoDegree.getNumRated() + "");
            holder.numPosts.setText(mExtraInfoDegree.getNumPosts() + "");

            Post mPost = mExtraInfoDegree.getPost();

            //LIKE
            if (mExtraInfoDegree.isIfLiked()) {
                holder.btnLike.setBackground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.flat_pressed_toggle_follow));
                holder.btnLike.setText("LIKED");
                holder.btnLike.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.green_follow));
                holder.btnLike.setGravity(Gravity.CENTER);

                holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            Drawable img = MyApplication.getAppContext().getResources().getDrawable( R.drawable.ic_s_hurt_gr );
//            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                // TODO: need to handle version problem
                holder.btnLike.setBackground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.flat_toggle_follow));
                holder.btnLike.setText("LIKE");
                holder.btnLike.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.white));
                holder.btnLike.setGravity(Gravity.CENTER);

                Drawable img = MyApplication.getAppContext().getResources().getDrawable(R.drawable.ic_hurt_wh);
                holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }

            //FOLLOW
            if (mExtraInfoDegree.isIfFollowed()) {
                holder.btnFollow.setBackground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.flat_pressed_toggle_follow));
                holder.btnFollow.setText("FOLLOWED");
                holder.btnFollow.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.green_follow));
                holder.btnFollow.setGravity(Gravity.CENTER);

//                holder.btnFollow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            Drawable img = MyApplication.getAppContext().getResources().getDrawable( R.drawable.ic_s_hurt_gr );
//            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                // TODO: need to handle version problem
                holder.btnFollow.setBackground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.flat_toggle_follow));
                holder.btnFollow.setText("FOLLOW");
                holder.btnFollow.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.white));
                holder.btnFollow.setGravity(Gravity.CENTER);

//                Drawable img = MyApplication.getAppContext().getResources().getDrawable(R.drawable.ic_hurt_wh);
//                holder.btnFollow.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }


            float score_rate = mExtraInfoDegree.getScoreRate();
            if (score_rate == -1) {
                holder.ratingBar.setRating(0.0F);
                holder.ratingBar.setAlpha(0.5F);
            } else {
                holder.ratingBar.setRating(score_rate / 1.0F);
                holder.ratingBar.setAlpha(1.0F);
            }
        } else if (mHolder instanceof MyHomeDegreeNewPostViewHolder) {
            MyHomeDegreeNewPostViewHolder holder = (MyHomeDegreeNewPostViewHolder) mHolder;

            //TODO - profile image and clickable to post
        } else {
            ViewHolderHomePost holder = (ViewHolderHomePost) mHolder;
            Post currentPost = mExtraInfoDegree.getPost();
            //one or more fields of the Degree object may be null since they are fetched from the web
            holder.profileName.setText(currentPost.getUser_name());
            holder.bodyContent.setText("\"" + currentPost.getBody_content() + "\"");
//            holder.bodyContent.setTypeface(null, Typeface.ITALIC);
            holder.scoreRate.setText(String.valueOf(currentPost.getRate()));

            int id = currentPost.getDbid_user_pic();
            if (id != -1) {
                String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id=" + id + "";
                loadImages(url, holder);
            }


        }
    }

    private void loadImages(String urlThumbnail, final ViewHolderHomePost holder) {
        if (!urlThumbnail.equals(Constants.NA)) {
            mImageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.degreeThumbnail.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return amountChildren;
    }

    static class MyHomeDegreeViewHolder extends RecyclerView.ViewHolder {

        TextView degreeName, facultyName, numLikes, numFollowes, numRated, numPosts;
        Button btnLike, btnFollow;
        RatingBar ratingBar;

        public MyHomeDegreeViewHolder(View itemView) {
            super(itemView);
            degreeName = (TextView) itemView.findViewById(R.id.degreeName);
            facultyName = (TextView) itemView.findViewById(R.id.degreeFaculty);
            numLikes = (TextView) itemView.findViewById(R.id.homeFragmentLikeNum);
            numFollowes = (TextView) itemView.findViewById(R.id.favoritesNumberCard);
            numRated = (TextView) itemView.findViewById(R.id.numberRatesHome);
            numPosts = (TextView) itemView.findViewById(R.id.numPostsTV);
            btnLike = (Button) itemView.findViewById(R.id.toggleLikeButton);
            btnFollow = (Button) itemView.findViewById(R.id.followButton);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBarHome);

        }
    }

    static class MyHomeDegreeNewPostViewHolder extends RecyclerView.ViewHolder {

        ImageView mProfileImage;
        RelativeLayout mTextLayout;

        public MyHomeDegreeNewPostViewHolder(View itemView) {
            super(itemView);
            mProfileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            mTextLayout = (RelativeLayout) itemView.findViewById(R.id.newPostLayout);
        }
    }

    static class ViewHolderHomePost extends RecyclerView.ViewHolder {

        ImageView degreeThumbnail;
        TextView profileName, bodyContent;
        TextView scoreRate;
        ImageButton rateImage;


        public ViewHolderHomePost(View itemView) {
            super(itemView);
            degreeThumbnail = (ImageView) itemView.findViewById(R.id.profileImage);
            profileName = (TextView) itemView.findViewById(R.id.textViewProfileName);
            bodyContent = (TextView) itemView.findViewById(R.id.bodyContent);
            scoreRate = (TextView) itemView.findViewById(R.id.scoreRateText);
            rateImage = (ImageButton) itemView.findViewById(R.id.rateImage);

//            List<String> list = new ArrayList<>();
//            list.add("noam");
//            mTagGroup.setData(list , Transformers.HASH);
        }
    }
}