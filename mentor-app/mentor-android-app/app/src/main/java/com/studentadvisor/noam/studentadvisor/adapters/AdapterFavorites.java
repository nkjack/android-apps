package com.studentadvisor.noam.studentadvisor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.extras.Constants;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Post;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Noam on 1/2/2016.
 */
public class AdapterFavorites extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //contains the list of Degrees
    private ArrayList<Post> mListPosts = new ArrayList<>();
    private LayoutInflater mInflater;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    //formatter for parsing the dates in the specified format below
    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up
    private int mPreviousPosition = 0;
    private final static int POST_POSITION = 0;
    private final static int FETCH_MORE_POSITION = 1;
    private boolean FETCHING = false;


    public AdapterFavorites(Context context) {
        mInflater = LayoutInflater.from(context);
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
    }

    public void setPosts(ArrayList<Post> listPosts) {
        this.mListPosts = listPosts;
        //update the adapter to reflect the new set of Degrees
        notifyDataSetChanged();
    }

    public void setFETCHING(boolean bool){this.FETCHING = bool;}

    @Override
    public int getItemViewType(int position) {
        return position == mListPosts.size() ? FETCH_MORE_POSITION : POST_POSITION;
    }

    public void addMorePosts(List<Post> mordData) { this.mListPosts.addAll(mordData);}


    public Post getPosts(int position){
        Post post = null;
        if (mListPosts.get(position) != null){
            post = mListPosts.get(position);
        }
        return post;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == POST_POSITION){
            View root = mInflater.inflate(R.layout.degree_post_card_component_news_feed, parent, false);
            RecyclerView.ViewHolder holder = new ViewHolderPost(root);
            return holder;
        }
        else {
            View root = mInflater.inflate(R.layout.custom_row_fetch_more, parent, false);
            RecyclerView.ViewHolder holder = new ViewHolderFetchMore(root);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position) {
        if (mHolder instanceof ViewHolderPost) {

            ViewHolderPost holder = (ViewHolderPost) mHolder;
            Post currentPost = mListPosts.get(position);
            //one or more fields of the Degree object may be null since they are fetched from the web
            holder.profileName.setText(currentPost.getUser_name());
            holder.bodyContent.setText(currentPost.getBody_content());
            holder.scorePost.setText(currentPost.getTotalScore() + "");
            holder.countComments.setText(currentPost.getTotalComments() + "");
            holder.postedAtTextView.setText(currentPost.getDegree_name() + " - " + currentPost.getSchool_name() );

            holder.removeButton.setImageResource(R.drawable.ic_close_grey600_24dp);

            Date date = currentPost.getDate_added();
            L.m(date.toString());

            DateTime dateTime = new DateTime(date); //or simple DateTime.now()
            String result = DateUtils.getRelativeTimeSpanString(MyApplication.getAppContext(), dateTime, true).toString();
            holder.timeText.setText(result);

//            holder.bodyContent.setTypeface(null, Typeface.ITALIC);

            if (currentPost.getTypePost().contentEquals("rate")) {
                holder.rateImage.setVisibility(View.VISIBLE);
                holder.scoreRate.setText(String.valueOf(currentPost.getRate()));
            }
            else{
                holder.rateImage.setVisibility(View.GONE);
                holder.scoreRate.setText("");
            }


            if (currentPost.getUser_choice() == 1){
                holder.plusButton.setImageResource(R.mipmap.ic_arrow_up_green);
                holder.minusButton.setImageResource(R.mipmap.ic_arrow_down);
            }
            else if (currentPost.getUser_choice() == 0){
                holder.plusButton.setImageResource(R.mipmap.ic_arrow_up);
                holder.minusButton.setImageResource(R.mipmap.ic_arrow_down);
            }
            else if (currentPost.getUser_choice() == -1){
                holder.plusButton.setImageResource(R.mipmap.ic_arrow_up);
                holder.minusButton.setImageResource(R.mipmap.ic_arrow_down_red);
            }

//        String urlThumnailLogo = currentDegree.getUrlLogoPic();//currentDegree.getUrlThumbnail();
//        loadImages(urlThumnailLogo, holder);
//            String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id=8";
//            loadImages(url, holder);
            int id = currentPost.getDbid_user_pic();
            if (id != -1) {
                String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id=" + id + "";
                loadImages(url, holder);
            }
        }
        else{
            ViewHolderFetchMore holder = (ViewHolderFetchMore) mHolder;
            if (FETCHING){
                holder.fetchButton.setVisibility(View.GONE);
                holder.fetchingProgressBar.setVisibility(View.VISIBLE);
            }
            else{
                holder.fetchButton.setVisibility(View.VISIBLE);
                holder.fetchingProgressBar.setVisibility(View.GONE);
            }

        }
    }


    private void loadImages(String urlThumbnail, final ViewHolderPost holder) {
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
        return mListPosts.size() + 1;
    }

    static class ViewHolderPost extends RecyclerView.ViewHolder {

        ImageView degreeThumbnail;
        TextView profileName, bodyContent;
        TextView scorePost, countComments, scoreRate;
        ImageButton rateImage, plusButton, minusButton, removeButton;
        TextView timeText;
        TextView postedAtTextView;


        public ViewHolderPost(View itemView) {
            super(itemView);
            degreeThumbnail = (ImageView) itemView.findViewById(R.id.profileImage);
            profileName= (TextView) itemView.findViewById(R.id.textViewProfileName);
            bodyContent = (TextView) itemView.findViewById(R.id.bodyContent);
            scorePost= (TextView) itemView.findViewById(R.id.scorePost);
            countComments = (TextView) itemView.findViewById(R.id.commentCountCard);
            scoreRate = (TextView) itemView.findViewById(R.id.scoreRateText);
            rateImage = (ImageButton) itemView.findViewById(R.id.rateImage);
            plusButton = (ImageButton) itemView.findViewById(R.id.plusButton);
            minusButton = (ImageButton) itemView.findViewById(R.id.minusButton);
            timeText = (TextView) itemView.findViewById(R.id.timePostedTV);
            postedAtTextView = (TextView) itemView.findViewById(R.id.postedAtTV);
            removeButton = (ImageButton) itemView.findViewById(R.id.addToFavoritesButton);
//            List<String> list = new ArrayList<>();
//            list.add("noam");
//            mTagGroup.setData(list , Transformers.HASH);
        }
    }

    static class ViewHolderFetchMore extends RecyclerView.ViewHolder {

        ImageButton fetchButton;
        ProgressBar fetchingProgressBar;


        public ViewHolderFetchMore(View itemView) {
            super(itemView);
            fetchButton = (ImageButton) itemView.findViewById(R.id.fetchButton);

            fetchingProgressBar = (ProgressBar) itemView.findViewById(R.id.fetchMoreProgressBar);
        }
    }



}


