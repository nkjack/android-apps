package com.studentadvisor.noam.studentadvisor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.extras.Constants;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.VolleySingleton;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;

import java.util.ArrayList;

/**
 * Created by Noam on 12/11/2015.
 */
public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ViewHolderComment> {

    //contains the list of Comments
    private ArrayList<Comment> mListComments = new ArrayList<>();
    private LayoutInflater mInflater;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private Context mContext;
    //formatter for parsing the dates in the specified format below
    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up


    public AdapterComments(Context context, ArrayList<Comment> comments) {
        mInflater = LayoutInflater.from(context);
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
        this.mListComments = comments;
        mContext = context;
    }

    public void setComments(ArrayList<Comment> listComments) {
        this.mListComments = listComments;
        //update the adapter to reflect the new set of Comments
        notifyDataSetChanged();
    }

    public Comment getComment(int position){
        Comment Comment = null;
        if (mListComments.get(position) != null){
            Comment = mListComments.get(position);
        }
        return Comment;
    }

    @Override
    public ViewHolderComment onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_comment_row, parent, false);
        ViewHolderComment viewHolder = new ViewHolderComment(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderComment holder, int position) {
        Comment currentComment = mListComments.get(position);
        //one or more fields of the Comment object may be null since they are fetched from the web
        holder.profileName.setText(currentComment.getUser_name());
        holder.bodyContent.setText(currentComment.getBody_content());

//        String urlThumnailLogo = currentComment.getUrlLogoPic();//currentComment.getUrlThumbnail();
//        loadImages(urlThumnailLogo, holder);
        int id = currentComment.getDbid_user_pic();

        L.m("ID is --- " + id);
        if (id != -1) {
            L.m("ID IS NOT -1");
            String url = "http://bullnshit.com/student_advisor/db_scripts/get_user_image.php?id=" + id + "";
//            loadImages(url, holder);
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.ic_person)
                    .error(R.mipmap.ic_person)
                    .into(holder.profileImage);
        }

    }


    private void loadImages(final String urlThumbnail, final ViewHolderComment holder) {
        if (!urlThumbnail.equals(Constants.NA)) {
            mImageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.profileImage.setImageBitmap(response.getBitmap());
                    L.m(urlThumbnail);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListComments.size();
    }

    static class ViewHolderComment extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView profileName, bodyContent;


        public ViewHolderComment(View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            profileName = (TextView) itemView.findViewById(R.id.profileName);
            bodyContent = (TextView) itemView.findViewById(R.id.commentTV);
        }
    }

}

