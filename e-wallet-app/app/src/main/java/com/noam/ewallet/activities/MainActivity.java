package com.noam.ewallet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noam.ewallet.R;
import com.noam.ewallet.adapters.MainGridAdapter;
import com.noam.ewallet.ewallet.MyApplication;
import com.noam.ewallet.extras.Util;
import com.noam.ewallet.logging.L;
import com.noam.ewallet.pojo.Category;

import static com.noam.ewallet.extras.Keys.EndpointsBundles.*;
import static com.noam.ewallet.db.DBWallet.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {
    private List<Category> mCategoryArray = new ArrayList<Category>();
    private String categoryTextInput;
    private int categoryBudgetInput = -1;
    private Context mContext;
    private TextView mBudgetTitle;
    private MainGridAdapter mAdapter;
    private GridView gridview;
    private Button mBudgetButton;
    private int mBudget ,mSaves;

    private final String PREF_BUDGET ="MY_PREF_BUDGET";
    private final String PREF_SAVES ="MY_PREF_SAVES";

    private TextView mCategoriesBudgetTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        MyApplication.getWritableDatabase().readFilterPurchase("",false);

        mBudgetTitle = (TextView) findViewById(R.id.budgetTitle);
        mCategoriesBudgetTV = (TextView)findViewById(R.id.categoryBudgetsTV);
        //mBudgetTitle.setText("1732 ILS/ 2000");

        mCategoryArray = MyApplication.getWritableDatabase().readFilterCategory("", false);
        L.m(mCategoryArray.size() + "");

        gridview = (GridView) findViewById(R.id.mainGridView);
        mAdapter = new MainGridAdapter(this, mCategoryArray);
        gridview.setAdapter(mAdapter);

        mBudget = MyApplication.readFromPreferences(mContext,PREF_BUDGET,-1);
        mSaves = MyApplication.readFromPreferences(mContext,PREF_SAVES,-1);


        if (mBudget != -1 && mSaves != -1){
            Calendar c = Calendar.getInstance();   // this takes current date
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
//            c.set(Calendar.MINUTE, 0);

            long temp = Util.persistDate(c.getTime());
            int howMuchLeft = MyApplication.getWritableDatabase().sumMonthlyExpenses(temp, -1);
            L.m("Expensed this month = " + howMuchLeft);
            mBudgetTitle.setText(" "+mBudget +" / " + (mBudget - howMuchLeft) + "");

            int categoriesBudget = MyApplication.getWritableDatabase().getAllCategoryBudget();
            mCategoriesBudgetTV.setText("תקציב הקטגוריות "+"/"+" תקציב כולל"+"\n" + categoriesBudget +" / " + mBudget);

        }

        mBudgetButton = (Button)findViewById(R.id.buttonForOption);
        mBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("הגדר תקציב חודשי");


                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setDividerPadding(8);
                final EditText inputBudget = new EditText(mContext);
                inputBudget.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputBudget.setHint("מה תקציבך?");
                layout.addView(inputBudget);

                final EditText inputSaves = new EditText(mContext);
                inputSaves.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputSaves.setHint("כמה לשים בחיסכון?");
                layout.addView(inputSaves);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input_budget = inputBudget.getText().toString();
                        String input_saves = inputSaves.getText().toString();

                        if (!input_budget.contentEquals("") && !input_saves.contentEquals("")) {
                            MyApplication.saveToPreferences(mContext, PREF_BUDGET, Integer.parseInt(input_budget));
                            MyApplication.saveToPreferences(mContext, PREF_SAVES, Integer.parseInt(input_saves));
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Send intent to SingleViewActivity
                if (position == mCategoryArray.size()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("אוסף קטגוריה");

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setDividerPadding(8);

                    final EditText inputCategory = new EditText(mContext);
                    inputCategory.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputCategory.setHint("שם הקטגוריה");
                    layout.addView(inputCategory);

                    final EditText inputBudget = new EditText(mContext);
                    inputBudget.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputBudget.setHint("תקציב");
                    layout.addView(inputBudget);

                    builder.setView(layout);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            categoryTextInput = inputCategory.getText().toString();
                            if (!categoryTextInput.contentEquals("") && !inputBudget.getText().toString().contentEquals("")) {
                                categoryBudgetInput = Integer.parseInt(inputBudget.getText().toString());
                                Category temp = new Category(-1, categoryTextInput, categoryBudgetInput);
                                ArrayList<Category> tempArr = new ArrayList<Category>();
                                tempArr.add(temp);
                                MyApplication.getWritableDatabase().insertCategory(ALL_CATEGORIES, tempArr, false);

                                mCategoryArray = MyApplication.getWritableDatabase().readFilterCategory("", false);
                                mAdapter = new MainGridAdapter(mContext, mCategoryArray);
                                gridview.setAdapter(mAdapter);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {
                    Category category = mCategoryArray.get(position);

                    Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                    i.putExtra(SELECTED_CATEGORY, category);
                    startActivity(i);
                }
                //position
            }
        });

        gridview.setLongClickable(true);
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                if (pos != mCategoryArray.size()) {
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                    alert.setTitle("Delete Confirmation");
                    alert.setMessage("Are you sure to delete category");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do your work here
                            Category cat = mCategoryArray.get(pos);
                            MyApplication.getWritableDatabase().deleteCategory(cat.getDbid_category());
                            MyApplication.getWritableDatabase().deleteSubCategory(-1, cat.getDbid_category());
                            MyApplication.getWritableDatabase().deletePurchase(-1, cat.getDbid_category(), -1);

                            mCategoryArray = MyApplication.getWritableDatabase().readFilterCategory("", false);
                            mAdapter = new MainGridAdapter(mContext, mCategoryArray);
                            gridview.setAdapter(mAdapter);

                            mBudget = MyApplication.readFromPreferences(mContext, PREF_BUDGET, -1);
                            mSaves = MyApplication.readFromPreferences(mContext, PREF_SAVES, -1);


                            if (mBudget != -1 && mSaves != -1) {
                                Calendar c = Calendar.getInstance();   // this takes current date
                                c.set(Calendar.DAY_OF_MONTH, 1);
                                c.set(Calendar.HOUR_OF_DAY, 0);
//                                c.set(Calendar.MINUTE, 0);
                                long temp = Util.persistDate(c.getTime());
                                int howMuchLeft = MyApplication.getWritableDatabase().sumMonthlyExpenses(temp,-1);

                                mBudgetTitle.setText(" " + mBudget + " / " + (mBudget - howMuchLeft) + "");

                            }
                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
