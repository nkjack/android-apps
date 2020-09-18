package com.noam.ewallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noam.ewallet.R;
import com.noam.ewallet.logging.L;
import com.noam.ewallet.pojo.Category;
import com.noam.ewallet.pojo.SubCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Noam on 3/31/2017.
 */
public class SubGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<SubCategory> myArray = new ArrayList<SubCategory>();


    // Constructor
    public SubGridAdapter(Context c, List<SubCategory> myArray) {
        mContext = c;
        this.myArray = myArray;
    }


    @Override
    public int getCount() {
        return myArray == null ? 1 : myArray.size() + 1;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
        LinearLayout mLinearLayout;
        ImageView imageView;


        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

        }
        else
        {
            imageView = (ImageView) convertView;
        }

        if (position == 0){
            imageView.setImageResource(R.mipmap.ic_action_add);
        }
        else {
            imageView.setImageResource(R.mipmap.ic_action_folder_tabs);
        }
        return imageView;

*/

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(mContext);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.grid_item_category, null);
            // set image based on selected text

        } else {
            gridView = (View) convertView;
        }

        LinearLayout gridItemLayout = (LinearLayout) gridView.findViewById(R.id.layoutGridItem);
        int[] androidColors = mContext.getResources().getIntArray(R.array.grid_colors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        gridItemLayout.setBackgroundColor(randomAndroidColor);

        TextView textView = (TextView) gridView.findViewById(R.id.categoryName);
        if (position == myArray.size()){
            textView.setText("+");
        }
        else{
            textView.setText(myArray.get(position).getSub_category_name());
        }

        return gridView;

    }

    public void setList(List<SubCategory> listSubCategory) {
        myArray.addAll(listSubCategory);
    }

}

