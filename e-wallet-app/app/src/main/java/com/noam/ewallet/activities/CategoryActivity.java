package com.noam.ewallet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.noam.ewallet.db.DBWallet.ALL_CATEGORIES;
import static com.noam.ewallet.db.DBWallet.ALL_SUB_CATEGORIES;
import static com.noam.ewallet.db.DBWallet.ALL_WALLET;
import static com.noam.ewallet.extras.Keys.EndpointsBundles.*;

import com.noam.ewallet.R;
import com.noam.ewallet.adapters.MainGridAdapter;
import com.noam.ewallet.adapters.SubGridAdapter;
import com.noam.ewallet.ewallet.MyApplication;
import com.noam.ewallet.logging.L;
import com.noam.ewallet.pojo.Category;
import com.noam.ewallet.pojo.Purchase;
import com.noam.ewallet.pojo.SubCategory;
import com.noam.ewallet.extras.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategoryActivity extends AppCompatActivity {
    private List<SubCategory> mSubCategoryArray = new ArrayList<SubCategory>();
    private Category mCategory;
    private String subCategoryTextInput;
    private String subCategoryBudgetInput;
    private Context mContext;
    private SubCategory chosenSubCategory;

    private TextView mCategoryTitle, mSubCategoryTitle;
    private EditText mPriceET, mCommentET;
    private Button mConfirmButton;

    private SubGridAdapter mAdapter;
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCategoryTitle = (TextView) findViewById(R.id.categoryNameTitle);
        mSubCategoryTitle = (TextView) findViewById(R.id.subCategoryTV);
        mCommentET = (EditText) findViewById(R.id.commentEditText);
        mPriceET = (EditText) findViewById(R.id.inputPriceBox);
        mConfirmButton = (Button) findViewById(R.id.buttonEnterPrice);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = mPriceET.getText().toString();
                if (!price.contentEquals("")){
                    String comment = mCommentET.getText().toString();
                    ArrayList<Purchase> templist = new ArrayList<Purchase>();

                    Calendar c = Calendar.getInstance();

                    Purchase tempPurchase = new Purchase(-1,
                            mCategory.getCategory_name(),
                            mCategory.getDbid_category(),
                            chosenSubCategory!=null ?  chosenSubCategory.getSub_category_name() : "Other",
                            chosenSubCategory!=null ?  chosenSubCategory.getDbid_sub_category() : -1,
                            Integer.parseInt(price),
                            Util.persistDate(c.getTime()),
                            comment);
                    templist.add(tempPurchase);

                    MyApplication.getWritableDatabase().insert_purchase(ALL_WALLET, templist, false);
                    Toast.makeText(CategoryActivity.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                    mPriceET.setText("");
                    mCommentET.setText("");

                    Calendar cal = Calendar.getInstance();   // this takes current date
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
//                c.set(Calendar.MINUTE, 0);
                    long temp = Util.persistDate(cal.getTime());
                    L.m("First Day" + Util.persistDate(cal.getTime()));
                    int monthExpenses = MyApplication.getWritableDatabase().sumMonthlyExpenses(temp, mCategory.getDbid_category());
                    int categoryBudget = MyApplication.getWritableDatabase().getCategoryBudget(mCategory.getDbid_category());

                    mCategoryTitle.setText(mCategory.getCategory_name() + "   " + categoryBudget + " / " + (categoryBudget - monthExpenses) );
                }
                else{
                    Toast.makeText(CategoryActivity.this, "Please fill the data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCategory = extras.getParcelable(SELECTED_CATEGORY);
            if (mCategory != null) {
                //TODO do stuff

                Calendar c = Calendar.getInstance();   // this takes current date
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
//                c.set(Calendar.MINUTE, 0);
                long temp = Util.persistDate(c.getTime());
                L.m("First Day" + Util.persistDate(c.getTime()));
                int monthExpenses = MyApplication.getWritableDatabase().sumMonthlyExpenses(temp, mCategory.getDbid_category());
                int categoryBudget = MyApplication.getWritableDatabase().getCategoryBudget(mCategory.getDbid_category());

                mCategoryTitle.setText(mCategory.getCategory_name() + "   " + categoryBudget + " / " + (categoryBudget - monthExpenses) );
                setTitle(mCategory.getCategory_name());
            }
            else{
                finish();
            }
        }
        mSubCategoryArray = MyApplication.getWritableDatabase().readFilterSubCategory("" + mCategory.getDbid_category(), true);

        mAdapter = new SubGridAdapter(this, mSubCategoryArray);
        gridview = (GridView) findViewById(R.id.subCategoryGrid);
        gridview.setAdapter(mAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Send intent to SingleViewActivity

                if (position == mSubCategoryArray.size()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("אוסף קטגוריה משנית");

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText inputCategory = new EditText(mContext);
                    inputCategory.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputCategory.setHint("שם הקטגוריה המשנית");
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
                            subCategoryTextInput = inputCategory.getText().toString();
                            subCategoryBudgetInput = inputBudget.getText().toString();
                            if (!subCategoryTextInput.contentEquals("") /*&& !subCategoryBudgetInput.contentEquals("")*/) {
                                int subBudget = subCategoryBudgetInput.contentEquals("") ? 0 : Integer.parseInt(subCategoryBudgetInput);
                                SubCategory temp = new SubCategory(-1, subCategoryTextInput, mCategory.getDbid_category(), subBudget);
                                ArrayList<SubCategory> tempArr = new ArrayList<SubCategory>();
                                tempArr.add(temp);
                                MyApplication.getWritableDatabase().insertSubCategories(ALL_SUB_CATEGORIES, tempArr, false);

                                mSubCategoryArray = MyApplication.getWritableDatabase().readFilterSubCategory("" + mCategory.getDbid_category(), true);
                                mAdapter = new SubGridAdapter(mContext, mSubCategoryArray);
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
                    chosenSubCategory = mSubCategoryArray.get(position);
                    mSubCategoryTitle.setText(chosenSubCategory.getSub_category_name());
                }
            }


        });

        gridview.setLongClickable(true);
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                if (pos != mSubCategoryArray.size()) {
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(
                            CategoryActivity.this);
                    alert.setTitle("Delete Confirmation");
                    alert.setMessage("Are you sure to delete sub category");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do your work here
                            SubCategory cat = mSubCategoryArray.get(pos);
                            MyApplication.getWritableDatabase().deleteSubCategory(cat.getDbid_sub_category(), -1);
                            MyApplication.getWritableDatabase().deletePurchase(-1, -1, cat.getDbid_sub_category());

                            mSubCategoryArray = MyApplication.getWritableDatabase().readFilterSubCategory("" + mCategory.getDbid_category(), true);
                            mAdapter = new SubGridAdapter(mContext, mSubCategoryArray);
                            gridview.setAdapter(mAdapter);

//                        mBudget = MyApplication.readFromPreferences(mContext, PREF_BUDGET, -1);
//                        mSaves = MyApplication.readFromPreferences(mContext, PREF_SAVES, -1);
//
//
//                        if (mBudget != -1 && mSaves != -1) {
//                            Calendar c = Calendar.getInstance();   // this takes current date
//                            c.set(Calendar.DAY_OF_MONTH, 1);
//                            c.set(Calendar.HOUR_OF_DAY, 0);
//                            long temp = Util.persistDate(c.getTime());
//                            int howMuchLeft = MyApplication.getWritableDatabase().sumMonthlyExpenses(temp);
//
//                            mBudgetTitle.setText(" " + mBudget + " / " + (mBudget - howMuchLeft) + "");
//
//                        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_budget) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("שינוי תקציב הקטגוריה");

            final EditText inputBudget = new EditText(mContext);
            inputBudget.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputBudget.setHint("תקציב חדש");
            builder.setView(inputBudget);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tempBudget = inputBudget.getText().toString();
                    if (!tempBudget.contentEquals("") ) {
                        int changeBudget = Integer.parseInt(tempBudget);
                        MyApplication.getWritableDatabase().updateCategoryBudget(mCategory.getDbid_category(), changeBudget);

                        Toast.makeText(CategoryActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
