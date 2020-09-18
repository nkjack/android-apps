package com.studentadvisor.noam.studentadvisor.extras;

import android.view.View;

/**
 * Created by Noam on 12/6/2015.
 */
public interface ClickListenerPosts {
    public void onClickPlusButton(View view, int position);
    public void onClickMinusButton(View view, int position);
    public void onClickCommentButton(View view, int position);
    public void onClickFetchMoreButton(View view, int position);
    public void onClickDegreeTitleButton(View view, int position);
    public void onClickAddToFavoritesButton(View view, int position);
}
