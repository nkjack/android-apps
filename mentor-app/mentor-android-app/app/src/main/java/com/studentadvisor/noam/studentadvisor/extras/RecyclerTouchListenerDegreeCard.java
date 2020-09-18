package com.studentadvisor.noam.studentadvisor.extras;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.studentadvisor.noam.studentadvisor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noam on 11/14/2015.
 */
public class RecyclerTouchListenerDegreeCard implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListenerDegreeCard clickListener;

        public RecyclerTouchListenerDegreeCard(Context context, final RecyclerView recyclerView, final ClickListenerDegreeCard clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

//                    final View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null) {
//                        ImageButton temp = (ImageButton) child.findViewById(R.id.hurtImageCard);
//                        temp.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (clickListener != null) {
//                                    clickListener.onClickLike(child, recyclerView.getChildPosition(child));
//                                }
//                            }
//                        });
//
//                    }
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
//                clickListener.onClickLike(child,rv.getChildPosition(child));
            }
            return false;

        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

