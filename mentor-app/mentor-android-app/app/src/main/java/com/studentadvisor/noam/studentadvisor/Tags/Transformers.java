package com.studentadvisor.noam.studentadvisor.Tags;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.greenfrvr.hashtagview.HashtagView;

/**
 * Created by Noam on 11/11/2015.
 */
public class Transformers {

    public static final HashtagView.DataTransform<String> HASH = new HashtagView.DataTransform<String>() {
        @Override
        public CharSequence prepare(String item) {
            SpannableString spannableString = new SpannableString("" + item);//("#" + item);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#85F5F5F5")), 0, 0/*1*/, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    };



}
