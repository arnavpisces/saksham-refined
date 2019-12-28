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
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
//import com.zuccessful.trueharmony.fragments.DailyRoutineManageFrag;
import com.zuccessful.trueharmony.fragments.ActivityStatsFrag;
import com.zuccessful.trueharmony.fragments.DailyRoutine_gb;
import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.fragments.MeasurementFragment;
import com.zuccessful.trueharmony.fragments.MedManageFragment;
import com.zuccessful.trueharmony.fragments.StatsFragment;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class DailyRoutineActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.fragment_content, new DailyRoutine_gb()).commit();
                    return true;
                case R.id.navigation_info:
                    //putting extra info to fragment
                    Bundle bundle = new Bundle();
                    ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.daily_routine_que)));
                    bundle.putStringArrayList("questions", questions);
                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.daily_routine_ans)));
                    bundle.putStringArrayList("answers", answers);
                    String module = "Daily Routine";
                    bundle.putString("Module", module);
                    Log.d("DailyRoutineActivity", "onNavigationItemSelected: navigation_dashboard");
                    //fragment
                    InformationFragment infoFrag = new InformationFragment();
                    infoFrag.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();
                    return true;
                case R.id.navigation_progress:
                    fragmentTransaction.replace(R.id.fragment_content, new ActivityStatsFrag()).commit();
//                    log
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.activity_iadl);
        Log.d("DailyRoutineActivity", "onCreate: started");

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeActionBarFont();
        getSupportActionBar().setTitle(getString(R.string.daily_routine));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d("DailyRoutineActivity", "onCreate: Initialized the fragment transaction");
        fragmentTransaction.replace(R.id.fragment_content, new DailyRoutine_gb()).commit();
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
        }
    }
}