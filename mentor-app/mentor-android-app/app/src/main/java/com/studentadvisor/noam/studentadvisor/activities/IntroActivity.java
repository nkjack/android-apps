package com.studentadvisor.noam.studentadvisor.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.fragments.IntroFragment;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;

import java.io.InputStream;
import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Noam on 1/12/2016.
 */
public class IntroActivity  extends AppCompatActivity {

    static final int TOTAL_PAGES = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button btnSkip;
    Button btnDone;
    ImageButton btnNext;
    boolean isOpaque = true;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_intro);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.fragment_1_txt_color));
        init_typeface();

        btnSkip = Button.class.cast(findViewById(R.id.btn_skip));
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });

        btnNext = ImageButton.class.cast(findViewById(R.id.btn_next));
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });

        btnDone = Button.class.cast(findViewById(R.id.done));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (position == TOTAL_PAGES - 1 && positionOffset > 0) {
//                    if (isOpaque) {
//                        pager.setBackgroundColor(Color.TRANSPARENT);
//                        isOpaque = false;
//                    }
//                } else {
//                    if (!isOpaque) {
//                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
//                        isOpaque = true;
//                    }
//                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == 0){
                    btnSkip.setVisibility(View.GONE);
                }
                else if (position == TOTAL_PAGES - 2) {
                    btnSkip.setVisibility(View.GONE);
                    btnNext.setVisibility(View.GONE);
                    btnDone.setVisibility(View.VISIBLE);
                } else if (position < TOTAL_PAGES - 1) {
                    btnSkip.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnDone.setVisibility(View.GONE);
                } else if (position == TOTAL_PAGES - 1) {
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {

            HashMap<String, String> user = db.getUserDetails();

            String seen_intro = user.get("seen_intro");
            //User is already logged in. Take him to main activity
            if (seen_intro == null){

            }
            else if (seen_intro.contentEquals("no")) {
                Intent intent = new Intent(IntroActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
            else if (seen_intro.contentEquals("yes")){
                Intent intent = new Intent(IntroActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                finish();
            }
            else{
//                Toast.makeText(IntroActivity.this, "Problem Loging In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IntroActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < TOTAL_PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_checkbox_blank_circle_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < TOTAL_PAGES) {
            for (int i = 0; i < TOTAL_PAGES - 1 ; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    circle.setColorFilter(getResources().getColor(R.color.colorTextSecondary2));
                }
            }
        }
    }

    private void endTutorial() {
//        finish();
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    // ----- DESIGN

    private void init_typeface() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("Alef-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setStatusBarColor(View statusBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
//            int actionBarHeight = getActionBarHeight();
            int actionBarHeight = 0;
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



    private class ScreenSlideAdapter extends FragmentStatePagerAdapter {

        public ScreenSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IntroFragment introFragment = null;
            switch (position) {
                case 0:
                    introFragment = IntroFragment.newInstance(R.layout.intro_fragment_0,1);
                    break;
                case 1:
                    introFragment = IntroFragment.newInstance(R.layout.intro_fragment_1,2);
                    break;
                case 2:
                    introFragment = IntroFragment.newInstance(R.layout.intro_fragment_2,3);
                    break;
                case 3:
                    introFragment = IntroFragment.newInstance(R.layout.intro_fragment_3,4);
                    break;
                case 4:
                    introFragment = IntroFragment.newInstance(R.layout.intro_fragment_4,5);
                    break;
            }

            return introFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            // Get the page index from the tag. This makes
            // it possible to know which page index you're
            // currently transforming - and that can be used
            // to make some important performance improvements.
            int pagePosition = (int) page.getTag();

            // Here you can do all kinds of stuff, like get the
            // width of the page and perform calculations based
            // on how far the user has swiped the page.
            float pageWidthTimesPosition = pageWidth * position;
            float absPosition = Math.abs(position);

            View backgroundView = page.findViewById(R.id.intro_fragment);
            View text_head = page.findViewById(R.id.title);
            View text_content = page.findViewById(R.id.explanation);
            View image = page.findViewById(R.id.exploreImage);



            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {

            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }

                if (image != null) {
                    ViewHelper.setTranslationX(image, pageWidth * position);
//                    ViewHelper.setTranslationY(image, pageWidth * position);
                    ViewHelper.setTranslationY(image, -pageWidthTimesPosition / 2f);
//                    ViewHelper.setAlpha(image ,1.0f - absPosition);
//                    ViewHelper.setAlpha(image, 1.0f - Math.abs(position));
                }

//                if (pagePosition == 0 && image != null) {
//                    image.setAlpha(1.0f - absPosition);
//                    image.setTranslationX(-pageWidthTimesPosition * 1.5f);
//                }
            }
        }
    }
}