package com.studentadvisor.noam.studentadvisor.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.studentadvisor.noam.studentadvisor.R;
import com.studentadvisor.noam.studentadvisor.Tags.Transformers;
import com.studentadvisor.noam.studentadvisor.adapters.SearchAdapter;
import com.studentadvisor.noam.studentadvisor.callbacks.DegreesLoadedListener;
import com.studentadvisor.noam.studentadvisor.callbacks.WelcomeScreenDialogCommunicator;
import com.studentadvisor.noam.studentadvisor.database.DBDegrees;
import com.studentadvisor.noam.studentadvisor.database.SQLiteHandler;
import com.studentadvisor.noam.studentadvisor.extras.SharedPreference;
import com.studentadvisor.noam.studentadvisor.extras.Util;
import com.studentadvisor.noam.studentadvisor.fragments.ConfirmWelcomeDialogFragment;
import com.studentadvisor.noam.studentadvisor.fragments.NewsFeedFragment;
import com.studentadvisor.noam.studentadvisor.fragments.WelcomeFragment1;
import com.studentadvisor.noam.studentadvisor.fragments.WelcomeFragment3Picture;
import com.studentadvisor.noam.studentadvisor.fragments.WelcomeFragmentSt2;
import com.studentadvisor.noam.studentadvisor.helper.SessionManager;
import com.studentadvisor.noam.studentadvisor.logging.L;
import com.studentadvisor.noam.studentadvisor.network.RequestHandler;
import com.studentadvisor.noam.studentadvisor.pojo.Degree;
import com.studentadvisor.noam.studentadvisor.pojo.School;
import com.studentadvisor.noam.studentadvisor.studentadvisor.MyApplication;
import com.studentadvisor.noam.studentadvisor.tasks.TaskInsertUsersExtraInfo;
import com.studentadvisor.noam.studentadvisor.tasks.TaskLoadDegrees;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_DEGREE_NAME;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.DBDegrees.COLUMN_SCHOOL_NAME_HE;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.LIST_SELECTED_SUBJECTS;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.SELECTED_DEGREE;

/**
 * Created by Noam on 12/26/2015.
 */
public class WelcomeActivity extends AppCompatActivity implements WelcomeScreenDialogCommunicator,
        DegreesLoadedListener {
    public static final String UPLOAD_URL = "http://bullnshit.com/student_advisor/db_scripts/upload_image.php";
    public static final String UPLOAD_KEY = "image";
    public static final String UPLOAD_KEY_UNIQUE_ID = "user_unique_id";
    public static final String TAG = "MY MESSAGE";

    private String unique_id;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    private static final String NAME_DIALOG_PARAM = "nameDialogParam";
    private static final String PROFILE_IMAGE_DIALOG_PARAM = "profileImageDialogParam";
    private static final String TYPE_USER_IMAGE_DIALOG_PARAM = "typeUserImageDialogParam";
    private static final String IF_BABY_BOOLEAN_DIALOG_PARAM = "ifBabyBooleanDialogParam";
    private static final String YEARS_STUDY_DIALOG_PARAM = "yearsStudy";
    private static final String DEGREE_1_DIALOG_PARAM = "degree1DialogParam";
    private static final String DEGREE_2_DIALOG_PARAM = "degree2DialogParam";
    private static final String DEGREE_3_DIALOG_PARAM = "degree3DialogParam";
    private static final String USER_TYPE_STR_DIALOG_PARAM ="userTypeStringDialogParam";
    static final int TOTAL_PAGES = 3;

    ViewPager pager;
    WelcomePagerAdapter pagerAdapter;

    boolean ifStudent = false;
    boolean ifBaby = false;
    boolean uploadedImage = false;
    int yearsStudying = 1;
    String comment = "";
    String userName = "";

    private SQLiteHandler db;
    private SessionManager session;
    private ArrayList<String> mDegrees;
    private ArrayList<Degree> mListDegrees = new ArrayList<>();
    private List<School> mSchoolList = new ArrayList<School>();
    private static final String STATE_DEGREE_WELCOME = "state_degree_welcome";
    private Degree degree1, degree2, degree3;

    int previous_pager = 0;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Window window = getWindow();
       // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.fragment_1_txt_color));
        */
        init_typeface();

        setContentView(R.layout.welcome_layout);
        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.fragment_1_txt_color));

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        userName = name;
        unique_id = user.get("uid");

        checkSaveInstanceDegrees(savedInstanceState);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
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

    private void checkSaveInstanceDegrees(Bundle savedInstanceState) {
        if ((savedInstanceState != null) && savedInstanceState.getParcelableArray(STATE_DEGREE_WELCOME) != null) {
            mListDegrees = savedInstanceState.getParcelableArrayList(STATE_DEGREE_WELCOME);

        } else {
            mListDegrees = MyApplication.getWritableDatabase().readDegrees(DBDegrees.ALL_DEGREES);
            mSchoolList = MyApplication.getWritableDatabase().readSchools();

            //DEGREES
            if (mListDegrees.isEmpty()) {
                L.m("ExploreActivity: executing TaskLoadDegrees");
                new TaskLoadDegrees(this).execute();
            } else {

            }
        }

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmap = cropToSquare(bitmap);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                WelcomeFragment3Picture fragment = (WelcomeFragment3Picture) pagerAdapter.getFragment(2);
                if (fragment != null) {
                    fragment.setProfileImage(bitmap);
                }
//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    // ------- WelcomeScreenDialogCommunicator -----
    @Override
    public void continueToNext(int fragmentNum) {
        if (pager != null) {
            pager.setCurrentItem(fragmentNum + 1);
        }
    }

    @Override
    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(WelcomeActivity.this, "Uploading Image", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                if (unique_id != null) {
                    data.put(UPLOAD_KEY_UNIQUE_ID, unique_id);
                    L.m("YES - unique id - " + unique_id);
                } else {
                    L.m("NO - unique id - " + unique_id);
                }

                String result = rh.sendPostRequest(UPLOAD_URL, data);

                return result;
            }
        }

        if (CheckEnabledInternet()) {
            UploadImage ui = new UploadImage();
            if (unique_id != null) {
                ui.execute(bitmap);
            } else {
                Toast.makeText(WelcomeActivity.this, "problem with log in credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // -------- LOAD TOOLBAR SEARCH -----------//
    @Override
    public void loadToolBarSearch() {
        if (mListDegrees != null && !mListDegrees.isEmpty()) {
            if (!ifBaby) {

                ArrayList<String> degreeStored = SharedPreference.loadList(WelcomeActivity.this, Util.PREFS_NAME, Util.KEY_COUNTRIES);

                View view = WelcomeActivity.this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
                LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
                ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
                final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
                ImageView searchImg = (ImageView) view.findViewById(R.id.img_search);
                final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
                final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

                Util.setListViewHeightBasedOnChildren(listSearch);

                edtToolSearch.setHint("חפש תואר");

                final Dialog toolbarSearchDialog = new Dialog(WelcomeActivity.this, R.style.MaterialSearch);
                toolbarSearchDialog.setContentView(view);
                toolbarSearchDialog.setCancelable(true);
                toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
                toolbarSearchDialog.show();

//        toolbarSearchDialog.setCancelable(true);
                toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                degreeStored = (degreeStored != null && degreeStored.size() > 0) ? degreeStored : new ArrayList<String>();
                final SearchAdapter searchAdapter = new SearchAdapter(WelcomeActivity.this, degreeStored, false);

                listSearch.setVisibility(View.VISIBLE);
                listSearch.setAdapter(searchAdapter);

                parentToolbarSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbarSearchDialog.cancel();

                    }

                });
                listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                        String country = String.valueOf(adapterView.getItemAtPosition(position));
                        SharedPreference.addList(WelcomeActivity.this, Util.PREFS_NAME, Util.KEY_COUNTRIES, country);
                        edtToolSearch.setText(country);
                        listSearch.setVisibility(View.GONE);

                    }
                });

                edtToolSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (!edtToolSearch.getText().toString().contentEquals("")) {
                            List<String> tempDegreeSearch = new ArrayList<String>();
                            tempDegreeSearch.add(edtToolSearch.getText().toString());

                            String searchString = edtToolSearch.getText().toString();
                            String degree = "";
                            String school = "";
                            int lastPsik = searchString.lastIndexOf(",");
                            if (searchString.contains(",")) {
                                degree = searchString.substring(0, lastPsik);
                                school = searchString.substring(lastPsik + 2);
                            }


                            String searchQuery = " " + COLUMN_DEGREE_NAME + " = '" + degree +
                                    "' AND " + COLUMN_SCHOOL_NAME_HE + " = '" + school + "' ";


                            List<Degree> tempListDegree = new ArrayList<Degree>();
                            tempListDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, searchQuery, true);

                            if (tempListDegree.size() == 1) {
//                             tempListDegree.get(0)
                                WelcomeFragmentSt2 fragment = (WelcomeFragmentSt2) pagerAdapter.getFragment(1);
                                if (fragment != null) {
                                    fragment.setTextView(tempListDegree.get(0));
                                    toolbarSearchDialog.dismiss();
                                } else {
                                    toolbarSearchDialog.dismiss();
                                    Toast.makeText(WelcomeActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            } else if (tempListDegree.size() > 1) {
                                boolean if_ok = true;
                                String tempString = tempListDegree.get(0).getDegree_name() + ", " + tempListDegree.get(0).getSchool_name_he();
                                for (Degree tempDegree : tempListDegree) {
                                    String checkStr = tempDegree.getDegree_name() + ", " + tempDegree.getSchool_name_he();
                                    if (!checkStr.contentEquals(tempString)) {
                                        if_ok = false;
                                    }
                                }

                                if (if_ok) {
                                    WelcomeFragmentSt2 fragment = (WelcomeFragmentSt2) pagerAdapter.getFragment(1);
                                    if (fragment != null) {
                                        fragment.setTextView(tempListDegree.get(0));
                                        toolbarSearchDialog.dismiss();
                                    } else {
                                        toolbarSearchDialog.dismiss();
                                        Toast.makeText(WelcomeActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Too Many Results, be more specific", Toast.LENGTH_LONG).show();
                                    edtToolSearch.setText("");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                                edtToolSearch.setText("");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Search your Degree..", Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });


                edtToolSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                String[] country = WelcomeActivity.this.getResources().getStringArray(R.array.countries_array);
                        mDegrees = new ArrayList<String>();
                        for (Degree degree : mListDegrees) {
                            mDegrees.add(degree.getDegree_name() + ", " + degree.getSchool_name_he());
                        }

                        listSearch.setVisibility(View.VISIBLE);
                        searchAdapter.updateList(mDegrees, true);


                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ArrayList<String> filterList = new ArrayList<String>();
                        boolean isNodata = false;
                        if (s.length() > 0) {
                            for (int i = 0; i < mDegrees.size(); i++) {


                                if (mDegrees.get(i).toLowerCase().contains(s.toString().trim().toLowerCase())) {

                                    filterList.add(mDegrees.get(i));

                                    listSearch.setVisibility(View.VISIBLE);
                                    searchAdapter.updateList(filterList, true);
                                    isNodata = true;
                                }
                            }
                            if (!isNodata) {
                                listSearch.setVisibility(View.GONE);
                                txtEmpty.setVisibility(View.VISIBLE);
                                txtEmpty.setText("No data found");
                            }
                        } else {
                            listSearch.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                imgToolBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toolbarSearchDialog.dismiss();
                    }
                });

                searchImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!edtToolSearch.getText().toString().contentEquals("")) {
                            List<String> tempDegreeSearch = new ArrayList<String>();
                            tempDegreeSearch.add(edtToolSearch.getText().toString());

                            String searchString = edtToolSearch.getText().toString();
                            String degree = "";
                            String school = "";
                            int lastPsik = searchString.lastIndexOf(",");
                            if (searchString.contains(",")) {
                                degree = searchString.substring(0, lastPsik);
                                school = searchString.substring(lastPsik + 2);
                            }


                            String searchQuery = " " + COLUMN_DEGREE_NAME + " = '" + degree +
                                    "' AND " + COLUMN_SCHOOL_NAME_HE + " = '" + school + "' ";


                            List<Degree> tempListDegree = new ArrayList<Degree>();
                            tempListDegree = MyApplication.getWritableDatabase().readDegreesFilter(DBDegrees.ALL_DEGREES, searchQuery, true);

                            if (tempListDegree.size() == 1) {
//                             tempListDegree.get(0)
                                WelcomeFragmentSt2 fragment = (WelcomeFragmentSt2) pagerAdapter.getFragment(1);
                                if (fragment != null) {
                                    fragment.setTextView(tempListDegree.get(0));
                                    toolbarSearchDialog.dismiss();
                                } else {
                                    toolbarSearchDialog.dismiss();
                                    Toast.makeText(WelcomeActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            } else if (tempListDegree.size() > 1) {
                                boolean if_ok = true;
                                String tempString = tempListDegree.get(0).getDegree_name() + ", " + tempListDegree.get(0).getSchool_name_he();
                                for (Degree tempDegree : tempListDegree) {
                                    String checkStr = tempDegree.getDegree_name() + ", " + tempDegree.getSchool_name_he();
                                    if (!checkStr.contentEquals(tempString)) {
                                        if_ok = false;
                                    }
                                }

                                if (if_ok) {
                                    WelcomeFragmentSt2 fragment = (WelcomeFragmentSt2) pagerAdapter.getFragment(1);
                                    if (fragment != null) {
                                        fragment.setTextView(tempListDegree.get(0));
                                        toolbarSearchDialog.dismiss();
                                    } else {
                                        toolbarSearchDialog.dismiss();
                                        Toast.makeText(WelcomeActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Too Many Results, be more specific", Toast.LENGTH_LONG).show();
                                    edtToolSearch.setText("");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                                edtToolSearch.setText("");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Search your Degree..", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            } else {
                Toast toast = Toast.makeText(WelcomeActivity.this, "Sorry, U Told Us U Are Not A Student", Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        }


    }

    @Override
    public void setIfBaby(boolean bool) {
        ifBaby = bool;
        ifStudent = !bool;

//        pagerAdapter.setIf_baby_frag_manager(ifBaby);
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setYearsStudying(int years) {
        yearsStudying = years;
    }

    @Override
    public void setDegreeStudying(int num, Degree degree) {
        switch (num) {
            case 1:
                degree1 = degree;
                break;
            case 2:
                degree2 = degree;
                break;
            case 3:
                degree3 = degree;
                break;
        }
    }

    @Override
    public void deleteDegreeStudying(int num) {
        switch (num) {
            case 1:
                degree1 = null;
                break;
            case 2:
                degree2 = null;
                break;
            case 3:
                degree3 = null;
                break;
        }
    }

    @Override
    public void finishButton() {
        String userType = "baby";
        if (!ifBaby) {
            userType = "student";
        } else {
//            yearsStudying = 1;
        }


        if (CheckEnabledInternet()) {
            createFinishDialog(userName
                    , bitmap
                    , ifBaby
                    , String.valueOf(yearsStudying)
                    , degree1
                    , degree2
                    , degree3
                    , userType);
        }

        /*
        if (unique_id != null && CheckEnabledInternet()) {
            new TaskInsertUsersExtraInfo(this,
                    unique_id,
                    userType,
                    yearsStudying,
                    degree1 != null ? degree1.getDbid_degree() : 0,
                    degree2 != null ? degree2.getDbid_degree() : 0,
                    degree3 != null ? degree3.getDbid_degree() : 0).execute();
        }
        */
    }


    public void createFinishDialog(String name, Bitmap profilaImage, boolean ifBaby, String yearsStudy, Degree degree1,
                                   Degree degree2, Degree degree3, String userType) {
        android.app.FragmentManager manager = getFragmentManager();
        ConfirmWelcomeDialogFragment d = new ConfirmWelcomeDialogFragment();
        Bundle args = new Bundle();
        args.putString(NAME_DIALOG_PARAM, name);
        args.putParcelable(PROFILE_IMAGE_DIALOG_PARAM, profilaImage);
        args.putBoolean(IF_BABY_BOOLEAN_DIALOG_PARAM, ifBaby);
        args.putString(YEARS_STUDY_DIALOG_PARAM, yearsStudy != null ? yearsStudy : "");
        args.putString(DEGREE_1_DIALOG_PARAM, degree1 != null ? degree1.getDegree_name() + ", " + degree1.getSchool_name_he() : "");
        args.putString(DEGREE_2_DIALOG_PARAM, degree2 != null ? degree2.getDegree_name() + ", " + degree2.getSchool_name_he() : "");
        args.putString(DEGREE_3_DIALOG_PARAM, degree3 != null ? degree3.getDegree_name() + ", " + degree3.getSchool_name_he() : "");
        args.putString(USER_TYPE_STR_DIALOG_PARAM, userType);
        d.setArguments(args);
        d.show(manager, "MyConfirmationDialog");
    }

    @Override
    public void onPostLoadedUsersExtraInfo(int success, String unique_id) {
        hideDialog();
        if (success == 1) {
            SQLiteHandler db = new SQLiteHandler(MyApplication.getAppContext());
            db.updateSeenInto(unique_id);
            Intent intent = new Intent(WelcomeActivity.this,
                    ExploreActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(WelcomeActivity.this, "Problem With Sending Info", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void backToLoginActivity() {
        if (Util.logOut()) {
            Intent logOutIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(logOutIntent);
            finish();
        }
    }

    @Override
    public void finishConfirmDialog(String userType) {
        if (unique_id != null && CheckEnabledInternet()) {
            pDialog.setMessage("Finishing ...");
            showDialog();
            new TaskInsertUsersExtraInfo(this,
                    unique_id,
                    userType,
                    yearsStudying,
                    degree1 != null ? degree1.getDbid_degree() : 0,
                    degree2 != null ? degree2.getDbid_degree() : 0,
                    degree3 != null ? degree3.getDbid_degree() : 0).execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_DEGREE_WELCOME, mListDegrees);
    }

    @Override
    public void onAllDegreesLoaded(ArrayList<Degree> listDegrees) {
        mListDegrees = listDegrees;
    }

    private boolean CheckEnabledInternet() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }
        if (!isConnected) {
            AlertDialog.Builder networkDialog = new AlertDialog.Builder(this);
            networkDialog.setTitle("Not Connected");
            networkDialog.setMessage("Please connect to internet to proceed");
            networkDialog.setCancelable(false);
            networkDialog.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    CheckEnabledInternet();
                }
            });
            networkDialog.create().show();
        }

        return isConnected;
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private class WelcomePagerAdapter extends FragmentStatePagerAdapter {

        FragmentManager fragmentManager;
        private Map<Integer, Fragment> mPageReferenceMap;
//        boolean if_baby_frag_manager = true;

        public WelcomePagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
            mPageReferenceMap = new HashMap<Integer, Fragment>();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment welcomeScreenFragment = null;
            switch (position) {
                case 0:
                    welcomeScreenFragment = WelcomeFragment1.newInstance(0, userName, ifBaby);
                    mPageReferenceMap.put(position, welcomeScreenFragment);
                    break;
                case 1:
                    welcomeScreenFragment = WelcomeFragmentSt2.newInstance(1);
                    mPageReferenceMap.put(position, welcomeScreenFragment);

                    break;
                case 2:
                    welcomeScreenFragment = WelcomeFragment3Picture.newInstance(2);
                    mPageReferenceMap.put(position, welcomeScreenFragment);
                    break;
            }
            /*
            if (position == 0){
                welcomeScreenFragment = WelcomeFragment1.newInstance(0, userName);
                mPageReferenceMap.put(position, welcomeScreenFragment);
            }
            else if (position == 1){
                if (!if_baby_frag_manager) {
                    welcomeScreenFragment = WelcomeFragmentSt2.newInstance(1);
                    mPageReferenceMap.put(position, welcomeScreenFragment);
                }
                else{
                    welcomeScreenFragment = WelcomeFragment3Picture.newInstance(2);
                    mPageReferenceMap.put(position, welcomeScreenFragment);
                }
            }
            else if (!if_baby_frag_manager){
                if (position == 2){
                    welcomeScreenFragment = WelcomeFragment3Picture.newInstance(2);
                    mPageReferenceMap.put(position, welcomeScreenFragment);
                }
            }
            */

            return welcomeScreenFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }


    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View button_continue = page.findViewById(R.id.btnContinue);
            View header_view = page.findViewById(R.id.headerView);

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

                if (button_continue != null) {
                    ViewHelper.setTranslationX(button_continue, pageWidth * position);
                    ViewHelper.setAlpha(button_continue, 1.0f - Math.abs(position));
                }
//
                if (header_view != null) {
                    ViewHelper.setTranslationX(header_view, pageWidth * position);
                    ViewHelper.setAlpha(header_view, 1.0f - Math.abs(position));
                }
            }
        }
    }


}
