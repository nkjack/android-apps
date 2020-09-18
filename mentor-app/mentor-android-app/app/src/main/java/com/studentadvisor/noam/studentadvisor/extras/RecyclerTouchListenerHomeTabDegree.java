package com.studentadvisor.noam.studentadvisor.extras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.studentadvisor.noam.studentadvisor.R;

/**
 * Created by Noam on 11/28/2015.
 */
public class RecyclerTouchListenerHomeTabDegree implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private ClickListenerHomeTabDegree clickListener;

    public RecyclerTouchListenerHomeTabDegree(Context context, final RecyclerView recyclerView, final ClickListenerHomeTabDegree clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                final View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (recyclerView.getChildPosition(child) == 1) {
                    if (child != null) {
                        Button temp = (Button) child.findViewById(R.id.toggleLikeButton);
                        temp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickLikeButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        Button followBtn = (Button) child.findViewById(R.id.followButton);
                        followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickFollowButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                    }
                }
                else if (recyclerView.getChildPosition(child) == 0){
                    if(child != null){
                        Button askButton = (Button) child.findViewById(R.id.actionBtnAskHome);
                        askButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickAskButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        Button rateButton = (Button) child.findViewById(R.id.actionBtnRateHome);
                        rateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickRateButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        RelativeLayout relativeLayout = (RelativeLayout) child.findViewById(R.id.newPostLayout);
                        relativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickGenericAsk(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });
                    }

                }
                return true;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            gestureDetector.onTouchEvent(e);
            return false;
        }
        return false;

//        gestureDetector.onTouchEvent(e);
//        return false;

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

