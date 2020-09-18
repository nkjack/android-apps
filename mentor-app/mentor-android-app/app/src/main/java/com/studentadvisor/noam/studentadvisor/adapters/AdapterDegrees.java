package com.studentadvisor.noam.studentadvisor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.greenfrvr.hashtagview.HashtagView;
//import com.pkmmte.view.CircularImageView;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.Tags.Tag;
import com.studentadvisor.noam.studentadvisor.Tags.TagClass;
import com.studentadvisor.noam.studentadvisor.Tags.TagView;
import com.studentadvisor.noam.studentadvisor.Tags.Transformers;
import com.studentadvisor.noam.studentadvisor.extras.Constants;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.security.Policy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Noam on 11/7/2015.
 */
public class AdapterDegrees extends RecyclerView.Adapter<AdapterDegrees.ViewHolderDegree> {

    //contains the list of Degrees
    private ArrayList<Degree> mListDegrees = new ArrayList<>();
    private LayoutInflater mInflater;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    //formatter for parsing the dates in the specified format below
    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up
    private int mPreviousPosition = 0;


    public AdapterDegrees(Context context) {
        mInflater = LayoutInflater.from(context);
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
    }

    public void setDegrees(ArrayList<Degree> listDegrees) {
        this.mListDegrees = listDegrees;
        //update the adapter to reflect the new set of Degrees
        notifyDataSetChanged();
    }

    public Degree getDegree(int position){
        Degree degree = null;
        if (mListDegrees.get(position) != null){
            degree = mListDegrees.get(position);
        }
        return degree;
    }

    @Override
    public ViewHolderDegree onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_degree_row, parent, false);
        ViewHolderDegree viewHolder = new ViewHolderDegree(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderDegree holder, int position) {
        Degree currentDegree = mListDegrees.get(position);
        //one or more fields of the Degree object may be null since they are fetched from the web
        holder.degreeName.setText(currentDegree.getDegree_name());
        holder.schoolNameHE.setText(currentDegree.getSchool_name_he());
        holder.schoolNameEN.setText(currentDegree.getSchool_name_en());
        holder.facultyName.setText("הפקולטה ל" + currentDegree.getFaculty_name());
        holder.degreeLikes.setText(String.valueOf(currentDegree.getLikes()));
        holder.degreeFollowes.setText(String.valueOf(currentDegree.getFollowes()));

        List<String> infoList = new ArrayList<String>();
        infoList.add(currentDegree.getLikes() + " Likes");

        if (!currentDegree.getSubject_1().contentEquals(Constants.NA)) {
            infoList.add(currentDegree.getSubject_1());
        }
        if (!currentDegree.getSubject_2().contentEquals(Constants.NA)) {
            infoList.add(currentDegree.getSubject_2());
        }
        if (!currentDegree.getSubject_3().contentEquals(Constants.NA)) {
            infoList.add(currentDegree.getSubject_3());
        }
        if (!currentDegree.getSubject_4().contentEquals(Constants.NA)) {
            infoList.add(currentDegree.getSubject_4());
        }

        // TODO: RATING BAR need a solution
        /*
        int audienceScore = 87;//currentDegree.getAudienceScore();
        if (audienceScore == -1) {
            holder.degreeRating.setRating(0.0F);
            holder.degreeRating.setAlpha(0.5F);
        } else {
            holder.degreeRating.setRating(audienceScore / 20.0F);
            holder.degreeRating.setAlpha(1.0F);
        }
        */

//        new LoadTags(holder).execute(infoList);




//        ArrayList<Tag> tags = new ArrayList<>();
//        Tag tag;
//        for (int i = 0; i < infoList.size(); i++) {
//            tag = new Tag(infoList.get(i));
//            tag.radius = 10f;
//            tag.layoutColor = (Color.parseColor("#303F9F"));
//            tag.isDeletable = false;
//            tags.add(tag);
//
//            /**
//             * counter++;
//             * if you don't want show all tags. You can set a limit.
//             if (counter == 10)
//             break;
//             */
//        }

//        if (tags.size() > 0) {
//            holder.mTagGroup.addTags(tags);
//        }
//        holder.mTagGroup.setData(info, Transformers.HASH);
//        holder.mTagGroup.setTags(info);

        /*
        if (likes == -1) {
            holder.DegreeAudienceScore.setRating(0.0F);
            holder.DegreeAudienceScore.setAlpha(0.5F);
        } else {
            holder.DegreeAudienceScore.setRating(audienceScore / 20.0F);
            holder.DegreeAudienceScore.setAlpha(1.0F);
        }
        */
        /*
        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animate1(holder, true);
//            AnimationUtils.animate(holder,true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animate1(holder, false);
//            AnimationUtils.animate(holder, false);
        }
        mPreviousPosition = position;
        */

        String urlThumnailLogo = currentDegree.getUrlLogoPic();//currentDegree.getUrlThumbnail();
        loadImages(urlThumnailLogo, holder);

    }


    private void loadImages(String urlThumbnail, final ViewHolderDegree holder) {
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
        return mListDegrees.size();
    }

    static class ViewHolderDegree extends RecyclerView.ViewHolder {

        ImageView degreeThumbnail;
        TextView degreeName, facultyName;
        TextView schoolNameEN, schoolNameHE;
//        TagView mTagGroup;
        //        RatingBar DegreeAudienceScore;
        TextView degreeLikes, degreeFollowes;
//        RatingBar degreeRating;

        public ViewHolderDegree(View itemView) {
            super(itemView);
            degreeThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            degreeName = (TextView) itemView.findViewById(R.id.degreeName);
            facultyName = (TextView) itemView.findViewById(R.id.degreeFaculty);
//            mTagGroup = (TagView) itemView.findViewById(R.id.tag_degree_info);
            schoolNameEN = (TextView) itemView.findViewById(R.id.schoolNameEn);
            schoolNameHE = (TextView) itemView.findViewById(R.id.schoolNameHe);
//            degreeRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
            degreeLikes = (TextView) itemView.findViewById(R.id.likeNumberCard);
            degreeFollowes = (TextView) itemView.findViewById(R.id.favoritesNumberCard);

//            List<String> list = new ArrayList<>();
//            list.add("noam");
//            mTagGroup.setData(list , Transformers.HASH);
        }
    }

    private class LoadTags extends AsyncTask<List<String>,Void, ArrayList<Tag>>{
        ViewHolderDegree holderDegree;

        public LoadTags(ViewHolderDegree holderDegree) {
            this.holderDegree = holderDegree;
        }

        @Override
        protected ArrayList<Tag> doInBackground(List<String>... params) {
            List<String> listTagsStrings = params[0];

            ArrayList<Tag> tags = new ArrayList<>();
            Tag tag;
            for (int i = 0; i < listTagsStrings.size(); i++) {
                tag = new Tag(listTagsStrings.get(i));
                tag.radius = 10f;
                tag.layoutColor = (Color.parseColor("#303F9F"));
                tag.isDeletable = false;
                tags.add(tag);

                /**
                 * counter++;
                 * if you don't want show all tags. You can set a limit.
                 if (counter == 10)
                 break;
                 */
            }
            return tags;
        }

        @Override
        protected void onPostExecute(ArrayList<Tag> tags) {
            super.onPostExecute(tags);
            if(tags != null || tags.size()>0) {
//                holderDegree.mTagGroup.addTags(tags);
            }
        }
    }
}
