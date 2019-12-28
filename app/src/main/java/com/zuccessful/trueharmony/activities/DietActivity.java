package com.zuccessful.trueharmony.activities;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.DietModuleFragment;
import com.zuccessful.trueharmony.fragments.DietStatsFrag;
import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class DietActivity extends AppCompatActivity {
    long startTime;
    long endTime;
    String TAG = "DietModule";
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
//                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
                    DietModuleFragment dietFrag = new DietModuleFragment();
                    fragmentTransaction.replace(R.id.fragment_content_diet, dietFrag).commit();
                    return true;
                case R.id.navigation_info:
                    //putting extra info to fragment
                    Bundle bundle = new Bundle();
                    ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_que)));
                    bundle.putStringArrayList("questions", questions);
                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_ans)));
                    bundle.putStringArrayList("answers", answers);
                    bundle.putString("Module","Diet");
                    //fragment
                    InformationFragment infoFrag = new InformationFragment();
                    infoFrag.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content_diet, infoFrag).commit();
                    return true;
                case R.id.navigation_progress:
//                    fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
                    DietStatsFrag dietStatsFrag = new DietStatsFrag();
                    fragmentTransaction.replace(R.id.fragment_content_diet,dietStatsFrag).commit();
                    return true;
            }
            return false;

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.activity_diet);
        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeActionBarFont();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content_diet, new DietModuleFragment()).commit();
    }
    public void changeActionBarFont(){ //This function is used to customize the action bar
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

        // Apply the layout parameters to TextView widget
        tv.setLayoutParams(lp);

        // Set text to display in TextView
        tv.setText(ab.getTitle()); // ActionBar title text

        // Set the text color of TextView to black
        tv.setTextColor(Color.WHITE);

        // Set the monospace font for TextView text
        // This will change ActionBar title text font
        Typeface customTypeface = ResourcesCompat.getFont(getApplicationContext(),R.font.semibold);

        tv.setTypeface(customTypeface);
        tv.setTextSize(20);

        // Set the ActionBar display option
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Finally, set the newly created TextView as ActionBar custom view
        ab.setCustomView(tv);
//        customToolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(customToolbar);
        //Changing the appbar title font here
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }
}
