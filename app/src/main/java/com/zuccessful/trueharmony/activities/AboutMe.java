package com.zuccessful.trueharmony.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;

public class AboutMe extends AppCompatActivity {

    private SakshamApp app;
    private Patient patient;
    private TextView insertName;
    private TextView insertID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.title_activity_about_me); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("About App", "onCreate: Started");
        changeLanguage(getApplicationContext());
        setContentView(R.layout.activity_about_me);
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
}
