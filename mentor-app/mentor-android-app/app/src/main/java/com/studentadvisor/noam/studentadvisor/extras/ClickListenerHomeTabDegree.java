package com.studentadvisor.noam.studentadvisor.extras;

import android.view.View;

/**
 * Created by Noam on 11/28/2015.
 */
public interface ClickListenerHomeTabDegree {
    public void onClickLikeButton(View view, int position);
    public void onClickAskButton(View view, int position);
    public void onClickRateButton(View view, int position);
    public void onClickGenericAsk(View view, int position);
    public void onClickFollowButton(View view, int position);
}
