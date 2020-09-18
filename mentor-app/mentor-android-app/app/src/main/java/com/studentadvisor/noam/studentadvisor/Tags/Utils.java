package com.studentadvisor.noam.studentadvisor.Tags;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Noam on 11/13/2015.
 */
public class Utils
{
    public static int dipToPx(Context c,float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
