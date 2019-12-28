package com.zuccessful.trueharmony.activities;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.HomeFragment;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.receivers.ServiceReceiver;
import com.zuccessful.trueharmony.services.AccelerometerSensorService;
import com.zuccessful.trueharmony.services.CallService;
import com.zuccessful.trueharmony.services.GyroscopeService;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TITLE = "TITLE";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private String selectedFragmentId;
    private String selectedFragmentTitle;
    FirebaseUser user;
    private TextView userNameText, userEmailText;
    private ImageView userProfPic;
    private FirebaseAuth mAuth;
    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    int PERMISSION_READ_CALLLOG = 1;
    int READ_IN_SMS = 2;
    int READ_OUT_SMS = 3;
    StringBuffer sb = new StringBuffer();

    DateFormat outputformat = new SimpleDateFormat("HH:mm");
    String output = null;

    //    private DrawerLayout mDrawer;
//    GoogleSignInClient mGoogleSignInClient;
    private final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        changeLanguage(getApplicationContext());
        setContentView(R.layout.activity_main);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        patient = app.getAppUser(null);

       // askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_CODE);
        mAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_launcher_white_bg));
        toolbar.setPadding(10, 2, 2, 0);

        toolbar.setTitle(getResources().getString(R.string.app_name) + " - " + SakshamApp.getInstance().getPatientID());


//        Intent gintent = new Intent(MainActivity.this, GyroscopeService.class);
//        startService(gintent);






        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

        Calendar cal2 = new GregorianCalendar();
        cal2.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal2.set(Calendar.HOUR_OF_DAY,23 );
        cal2.set(Calendar.MINUTE,30);
        cal2.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
        cal2.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        cal2.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal2.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

        Intent intent = new Intent(this.getApplicationContext(), ServiceReceiver.class);
        intent.setAction("START_TEST_SERVICE");

        PendingIntent pintent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

        AlarmManager alarm1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm1.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pintent);

        Intent intent2 = new Intent(this.getApplicationContext(), ServiceReceiver.class);
        intent2.setAction("STOP_TEST_SERVICE");

        PendingIntent pintent2 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent2, 0);

        AlarmManager alarm2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm2.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pintent2);


        Fragment fragment = null;
        fragment = new HomeFragment();
//        if (fragment != null && !fragment.getClass().getSimpleName().equals(getSelectedFragmentId())) {
        replaceAndSetFragment(fragment);
//            item.setChecked(true);
        setTitle(getResources().getString(R.string.app_name));
        setSelectedFragmentTitle(getResources().getString(R.string.app_name));
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void askPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // we dont have permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            //we have permission

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                }
                break;


        }
    }




    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        } else {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }


//            super.onBackPressed();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(SakshamApp.getInstance().getPatientID());
//        menu.add("About me");
//        menu.add("Alarm preferences");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean res = false;
        switch (item.getItemId()) {
            case R.id.action_about_me:
                // about me
                Intent intent = new Intent(this, AboutMe.class);
                startActivity(intent);
                break;
            case R.id.action_alarm_pref:
                // alarm preferences;
                Intent intent2 = new Intent(this, AlarmPref.class);
                startActivity(intent2);
                break;

            case R.id.user_profile:
                // alarm preferences;
                Intent userProfileIntent = new Intent(this,
                        UserProfileActivity.class);
                startActivity(userProfileIntent);
                break;

            default:
                res = super.onOptionsItemSelected(item);
        }
        return res;

    }



    private void replaceAndSetFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .commit();
        setSelectedFragmentId(fragment.getClass().getSimpleName());
    }

    //
    public String getSelectedFragmentId() {
        return selectedFragmentId;
    }

    public void setSelectedFragmentId(String selectedFragmentId) {
        this.selectedFragmentId = selectedFragmentId;
    }

    //
//    public String getSelectedFragmentTitle() {
//        return selectedFragmentTitle;
//    }
//
    public void setSelectedFragmentTitle(String selectedFragmentTitle) {
        this.selectedFragmentTitle = selectedFragmentTitle;
    }


    }


