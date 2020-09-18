package com.studentadvisor.noam.studentadvisor.extras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.logging.L;

/**
 * Created by Noam on 1/2/2016.
 */
public class RecycleTouchListenerNewsFeed implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private ClickListenerNewsFeed clickListener;

    public RecycleTouchListenerNewsFeed(Context context, final RecyclerView recyclerView, final int size,final boolean news_feed_bool, final ClickListenerNewsFeed clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                int itemCount = recyclerView.getAdapter().getItemCount()-1 ;
                L.m("itemCount = " + itemCount);


                final View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                L.m("position = " +recyclerView.getChildPosition(child));
                if (recyclerView.getChildPosition(child) == 0){
                    if (child != null){
                        Button button = (Button)child.findViewById(R.id.exploreHeaderButton);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null){
                                    clickListener.onClickExploreHeaderButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });
                    }
                }
                else if (recyclerView.getChildPosition(child) != itemCount) {
                    if (child != null) {
                        ImageButton temp = (ImageButton) child.findViewById(R.id.plusButton);
                        temp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickPlusButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        ImageButton askButton = (ImageButton) child.findViewById(R.id.minusButton);
                        askButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickMinusButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        ImageButton commentButton = (ImageButton) child.findViewById(R.id.commentsImageButton);
                        commentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickCommentButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        ImageButton addContentButton = (ImageButton) child.findViewById(R.id.addToFavoritesButton);
                        addContentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickAddToFavoritesButton(child, recyclerView.getChildPosition(child));
                                }
                            }
                        });

                        if (news_feed_bool){
                            TextView degreeText = (TextView)child.findViewById(R.id.postedAtTV);
                            degreeText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (clickListener != null){
                                        clickListener.onClickDegreeTitleButton(child, recyclerView.getChildPosition(child));
                                    }
                                }
                            });
                        }
                    }
                }
                else{
                    if (child != null) {
                        ImageButton fetchDownButton = (ImageButton) child.findViewById(R.id.fetchButton);
                        fetchDownButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clickListener != null) {
                                    clickListener.onClickFetchMoreButton(child, recyclerView.getChildPosition(child));
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

