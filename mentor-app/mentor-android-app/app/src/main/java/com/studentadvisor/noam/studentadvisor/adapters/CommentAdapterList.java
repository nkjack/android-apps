package com.studentadvisor.noam.studentadvisor.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.pojo.Comment;

import java.util.ArrayList;

/**
 * Created by Noam on 12/11/2015.
 */
public class CommentAdapterList extends BaseAdapter {

    private Context mContext;
    private ArrayList<Comment> mComments;
    private LayoutInflater mLayoutInflater;

    public CommentAdapterList(Context context, ArrayList<Comment> comments) {
        this.mContext = context;
        this.mComments =comments;
    }

    public void addComment(Comment comment){
        mComments.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolderComment holder = null;
        if(v==null){

            holder = new ViewHolderComment();

            mLayoutInflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            v = mLayoutInflater.inflate(R.layout.custom_comment_row, parent, false);
            holder.profileImage = (ImageView)v.findViewById(R.id.profileImage);
            holder.profileName = (TextView)v.findViewById(R.id.profileName);
            holder.bodyContent = (TextView)v.findViewById(R.id.commentTV);


            v.setTag(holder);
        }else{

            holder = (ViewHolderComment) v.getTag();
        }

        Comment comment = mComments.get(position);
        holder.profileName.setText(comment.getUser_name());
        holder.bodyContent.setText(comment.getBody_content());

        int id = comment.getDbid_user_pic();

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

        return v;
    }

}

class ViewHolderComment{

    ImageView profileImage;
    TextView profileName, bodyContent;

}


