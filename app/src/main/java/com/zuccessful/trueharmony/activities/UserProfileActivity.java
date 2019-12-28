package com.zuccessful.trueharmony.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.CustomSpinner;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

import static com.zuccessful.trueharmony.utilities.Utilities.removeListFromSharedPref;


public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView titleUserName;
    private EditText aboutMe;
    private TextView titleHeight;
    private EditText userHeight;
    private TextView titleUserWeight;
    private EditText userWeight;
    private TextView titleAlarmPreference;
    private CustomSpinner alrmPrefSpinner;
    private CustomSpinner languagePrefSpinner;
    private TextView titlePhysicalActivity;
    private Spinner phyActPrefSpinner;
    private TextView titleLeisureActivity;
    private Spinner leisureActivityPrefSpinner;
    private TextView titleMeals;
//    private TextView breakfastTv;
//    private Button breakfastButton;
//    private TextView lunchTv;
//    private Button lunchButton;
//    private TextView dinnerTv;
//    private Button dinnerButton;
    private Button addMoreButton;
    private Button addPhysicalActivityButton;
    private Button addLeisureActivityButton;

    private CardView logout;


    private LinearLayout phyActLinearLayout;
    private LinearLayout mealLinearLayout;

    private FirebaseFirestore db;
    private SimpleDateFormat sdf;
    private SakshamApp app;
    private Patient patient;
    private LinearLayout leisureActivityLinearLayout;
    private Uri ringtone;

    private void init() {
        titleUserName = (TextView)findViewById( R.id.title_user_name );
        aboutMe = (EditText)findViewById( R.id.about_me );
        titleHeight = (TextView)findViewById( R.id.title_height );
        userHeight = (EditText)findViewById( R.id.user_height );
        titleUserWeight = (TextView)findViewById( R.id.title_user_weight );
        userWeight = (EditText)findViewById( R.id.user_weight );
        titleAlarmPreference = (TextView)findViewById( R.id.title_alarm_preference );
        alrmPrefSpinner = (CustomSpinner)findViewById( R.id.alrm_pref_spinner );
        languagePrefSpinner = (CustomSpinner)findViewById( R.id.language_spinner );
        titlePhysicalActivity = (TextView)findViewById( R.id.title_physical_activity );
        phyActPrefSpinner = (Spinner)findViewById( R.id.phy_act_pref_spinner );
        titleLeisureActivity = (TextView)findViewById( R.id.title_leisure_activity );
        leisureActivityPrefSpinner = (Spinner)findViewById( R.id.leisure_activity_pref_spinner );
        titleMeals = (TextView)findViewById( R.id.title_meals );
//        breakfastTv = (TextView)findViewById( R.id.breakfast_tv );
//        breakfastButton = (Button)findViewById( R.id.breakfast_button );
//        lunchTv = (TextView)findViewById( R.id.lunch_tv );
//        lunchButton = (Button)findViewById( R.id.lunch_button );
//        dinnerTv = (TextView)findViewById( R.id.dinner_tv );
//        dinnerButton = (Button)findViewById( R.id.dinner_button );
        addMoreButton = (Button)findViewById( R.id.add_more_button );
        addPhysicalActivityButton = findViewById(R.id.add_physical_activity_button);
        phyActLinearLayout = findViewById(R.id.phy_act_linear_layout);
        mealLinearLayout = findViewById(R.id.meal_linear_layout);
        leisureActivityLinearLayout= findViewById(R.id.leisure_act_linear_layout);
        addLeisureActivityButton = findViewById(R.id.add_leisure_activity_button);
        logout = findViewById(R.id.cv_logout);

//        breakfastButton.setOnClickListener( this );
//        lunchButton.setOnClickListener( this );
//        dinnerButton.setOnClickListener( this );
        addMoreButton.setOnClickListener( this );
        addPhysicalActivityButton.setOnClickListener(this);
        addLeisureActivityButton.setOnClickListener(this);
        logout.setOnClickListener(this);

        getSupportActionBar().setTitle(getString(R.string.app_name));

        alrmPrefSpinner.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id, boolean userSelected) {
                if(userSelected) {
                    String itemSelected = String.valueOf(alrmPrefSpinner.getItemAtPosition(position));
                    Utilities.saveDataInSharedpref(getApplicationContext(), Constants.KEY_ALARM_PREF,
                            String.valueOf(position));
//                    Toast.makeText(getApplicationContext(), "Selected : " + itemSelected,
//                            Toast.LENGTH_SHORT).show();
                    if (itemSelected.equalsIgnoreCase(getString(R.string.ringtone))) {
                        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone);
                        startActivityForResult(intent, 1);
                    } else if (itemSelected.equalsIgnoreCase(getString(R.string.pledge))) {
                        Intent myIntent = new Intent(UserProfileActivity.this, RecordPledge.class);
                        startActivityForResult(myIntent, 0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        languagePrefSpinner.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id, boolean userSelected) {
                if(userSelected) {
                    String itemSelected = String.valueOf(languagePrefSpinner.getItemAtPosition(position));
                    Utilities.saveDataInSharedpref(getApplicationContext(), Constants.KEY_LANGUAGE_PREF,
                            String.valueOf(position));
//                    Toast.makeText(getApplicationContext(), "Selected : " + itemSelected,
//                            Toast.LENGTH_SHORT).show();

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        app = SakshamApp.getInstance();
        patient = app.getAppUser(null);
        db = app.getFirebaseDatabaseInstance();
        sdf = Utilities.getSimpleDateFormat();

        setProfileData();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    SharedPreferences pref = getSharedPreferences("alarmAudio",MODE_PRIVATE);
                    pref.edit().putString("URI", ringtone.toString()).commit();
                    break;

                default:
                    break;
            }
        }
    }

    ArrayList<String> phyActList,leisureActList, mealsList;
    private void setProfileData() {
        String height = Utilities.getDataFromSharedpref(getApplicationContext(), Constants.KEY_HEIGHT);
        String weight = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_WEIGHT);
        String name = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_NAME);
        String alarmPrefType = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_ALARM_PREF);
        String langPrefType = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_LANGUAGE_PREF);
//        String breakfastTime = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_BREAKFAST);
//        String lunchTime = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_LUNCH);
//        String dinnerTime = Utilities.getDataFromSharedpref(getApplicationContext(),Constants.KEY_DINNER);

        phyActList = Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST);
        leisureActList = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST);
        mealsList = Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST);

        if(phyActList!=null && !phyActList.isEmpty()){
            for(String act : phyActList) {
                addtoPhysicalActivityLayout(act,PHYSICAL_ACTIVITY);
            }
        }else{
            phyActList = new ArrayList<>();
        }

        if(leisureActList!=null && !leisureActList.isEmpty()){
            for(String act : leisureActList) {
                addtoPhysicalActivityLayout(act,LEISURE_ACTIVITY);
            }
        }else{
            leisureActList = new ArrayList<>();
        }

        if(mealsList!=null && !mealsList.isEmpty()){
            for(String act : mealsList) {
                addtoPhysicalActivityLayout(act,ADD_MEAL_ACTIVITY);
            }
        }else{
            mealsList = new ArrayList<>();
        }

        if(height!=null) userHeight.setText(height);
        if(weight!=null) userWeight.setText(weight);
        if(name!=null) aboutMe.setText(name);
        if(alarmPrefType!=null) alrmPrefSpinner.programmaticallySetPosition(Integer.parseInt(alarmPrefType),false);
        if(langPrefType!=null) languagePrefSpinner.programmaticallySetPosition(Integer.parseInt(langPrefType),false);
//        if(breakfastTime!=null) breakfastButton.setText(breakfastTime);
//        if(lunchTime!=null) lunchButton.setText(breakfastTime);
//        if(dinnerTime!=null) dinnerButton.setText(breakfastTime);

    }

    private void openTimePicker(final Button button){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        DateFormat df = new SimpleDateFormat("HH:mm");
                        //Date/time pattern of desired output date
                        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
                        Date date = null;
                        String output = null;
                        try{
                            //Conversion of input String to date
                            date= df.parse(hourOfDay+":"+minute);
                            //old date format to new date format
                            output = outputformat.format(date);
//                            if(button.getId() == R.id.add_more_button){
//                                addNewMealLayout(output);
//                                return;
//                            }
                            button.setText(output);
//                            if(button==breakfastButton){
//                                Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_BREAKFAST,output);
//                            }else if(button == lunchButton){
//                                Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_LUNCH,output);
//                            }else if(button == dinnerButton){
//                                Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_DINNER,output);
//                            }

                        }catch(ParseException pe){
                            pe.printStackTrace();
                        }


                    }
                }, mHour, mMinute, false);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), timePickerDialog);

        timePickerDialog.show();
    }

    private void addNewMealLayout(final String timeOfMeal) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.activity_user_profile, null,false);

        final View inflatedLayout= inflater.inflate(R.layout.textview_with_button, (ViewGroup) view, false);
        ((TextView)inflatedLayout.findViewById(R.id.meal_tv)).setText(newMeal);
        ((Button)inflatedLayout.findViewById(R.id.new_meal_button)).setText(timeOfMeal);
        (inflatedLayout.findViewById(R.id.new_meal_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Meal : " +newMeal
//                        +" , Time : " +timeOfMeal,Toast.LENGTH_SHORT).show();
                openTimePicker((Button) v);
            }
        });

        mealLinearLayout.addView(inflatedLayout);

    }

    @Override
    public void onClick(View v) {
//        if ( v == breakfastButton ) {
//            openTimePicker(breakfastButton);
//        } else if ( v == lunchButton ) {
//            openTimePicker(lunchButton);
//        } else if ( v == dinnerButton ) {
//            openTimePicker(dinnerButton);
//        } else
            if ( v == addMoreButton ) {
                createAddMealDialoge(getResources().getStringArray(R.array.meals_pref_arrays), ADD_MEAL_ACTIVITY);
//            addNewAddPhysicalActDialoge(ADD_MEAL_ACTIVITY);
        }else if( v == addPhysicalActivityButton) {
            createAddPhysicalActDialoge(getResources().getStringArray(R.array.phy_act_pref_arrays),PHYSICAL_ACTIVITY);
        }else if (v == addLeisureActivityButton){
            createAddPhysicalActDialoge(getResources().getStringArray(R.array.leisure_activity_pref_arrays),LEISURE_ACTIVITY);
        }else if (v == logout){
            app.clearPatientId();
            Utilities.clearPatientId(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }
    }

    private int PHYSICAL_ACTIVITY = 1;
    private int LEISURE_ACTIVITY = 2;
    private int ADD_MEAL_ACTIVITY = 3;

    private void createAddMealDialoge(final String[] mealChoices, final int typeOfActivity){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.meals_pref_prompt);
        builder.setItems(mealChoices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(),"Choice  : " + mealChoices[which],Toast.LENGTH_SHORT).show();
                if(mealChoices[which].equalsIgnoreCase(getString(R.string.other))){
                    addNewAddPhysicalActDialoge(typeOfActivity);
                }else if(typeOfActivity==ADD_MEAL_ACTIVITY && Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST).contains(mealChoices[which])) {
                    Toast.makeText(getApplicationContext(), R.string.activity_already_added, Toast.LENGTH_SHORT).show();
                }else{
                    addtoPhysicalActivityLayout(mealChoices[which],typeOfActivity);
                    savePhysicalActData(mealChoices[which],typeOfActivity);
                }
            }
        });
        builder.show();
    }

    private void createAddPhysicalActDialoge(final String[] phyActChoices,final int typeOfActivity){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_activity);
        builder.setItems(phyActChoices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(),"Choice  : " + phyActChoices[which],Toast.LENGTH_SHORT).show();
                if(phyActChoices[which].equalsIgnoreCase(getString(R.string.other))){
                    addNewAddPhysicalActDialoge(typeOfActivity);
                }else if(typeOfActivity==PHYSICAL_ACTIVITY && Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST).contains(phyActChoices[which])){
                    Toast.makeText(getApplicationContext(),R.string.activity_already_added,Toast.LENGTH_SHORT).show();
                }else if(typeOfActivity==LEISURE_ACTIVITY && Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST).contains(phyActChoices[which])){
                    Toast.makeText(getApplicationContext(),R.string.activity_already_added,Toast.LENGTH_SHORT).show();
                }else if(typeOfActivity==ADD_MEAL_ACTIVITY && Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST).contains(phyActChoices[which])){
                    Toast.makeText(getApplicationContext(),R.string.activity_already_added,Toast.LENGTH_SHORT).show();
                }
                else{
                        addtoPhysicalActivityLayout(phyActChoices[which],typeOfActivity);
                        savePhysicalActData(phyActChoices[which],typeOfActivity);
                }
            }
        });
        builder.show();

    }

    String m_Text;
    String newMeal;
    private void addNewAddPhysicalActDialoge(final int typeOfActivity){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(typeOfActivity == ADD_MEAL_ACTIVITY){
            builder.setTitle(R.string.enter_meal_name);
        }else {
            builder.setTitle(R.string.enter_activity_name);
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if(typeOfActivity == ADD_MEAL_ACTIVITY){
//                    newMeal = input.getText().toString();
//                    openTimePicker(addMoreButton);
//                    return;
//                }
                m_Text = input.getText().toString();
                addtoPhysicalActivityLayout(m_Text,typeOfActivity);
                savePhysicalActData(m_Text,typeOfActivity);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void savePhysicalActData(String activity,int typeOfActivity) {
        ArrayList<String> actList = new ArrayList<>();
        if(typeOfActivity == PHYSICAL_ACTIVITY) {
            actList = Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST);
        }else if(typeOfActivity == LEISURE_ACTIVITY){
            actList = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST);
        }else if(typeOfActivity == ADD_MEAL_ACTIVITY){
            actList = Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST);
        }
        if(actList!=null){
            actList.add(activity);
        }else{
            actList = new ArrayList<>();
            actList.add(activity);
        }
        if(typeOfActivity == PHYSICAL_ACTIVITY) {
            //only saved to sharedpred...need to update this list on firebase for updating on dailyroutine activity
            Utilities.saveListToSharedPref(actList,Constants.KEY_PHY_ACT_LIST);

            Map<String, Object> data = new HashMap<>();
            data.put("name", activity);
            data.put("alarmIds", new ArrayList<>(Arrays.asList(Utilities.getNextAlarmId(this))));
            data.put("reminders", new ArrayList<>(Arrays.asList("08:00")));
            data.put("id", String.valueOf(Utilities.getNextAlarmId(this)));
            data.put("alarmStatus", true);

            db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(activity).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void documentReference) {
                            Log.d("UserProfileActivity", "DocumentSnapshot written with ID: ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("UserProfileActivity", "Error adding document", e);
                        }
                    });

        }else if(typeOfActivity == LEISURE_ACTIVITY){
            Utilities.saveListToSharedPref(actList,Constants.KEY_LEISURE_ACT_LIST);

            Map<String, Object> data = new HashMap<>();
            data.put("name", activity);
            data.put("alarmIds", new ArrayList<>(Arrays.asList(Utilities.getNextAlarmId(this))));
            data.put("reminders", new ArrayList<>(Arrays.asList("08:00")));
            data.put("id", String.valueOf(Utilities.getNextAlarmId(this)));
            data.put("alarmStatus", true);

            db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(activity).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void documentReference) {
                            Log.d("UserProfileActivity", "DocumentSnapshot written with ID: ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("UserProfileActivity", "Error adding document", e);
                        }
                    });
        } else if(typeOfActivity == ADD_MEAL_ACTIVITY){
            Utilities.saveListToSharedPref(actList,Constants.KEY_MEALS_LIST);
            Map<String, Object> data = new HashMap<>();
            data.put("name", activity);
            data.put("alarmIds", new ArrayList<>(Arrays.asList(Utilities.getNextAlarmId(this))));
            data.put("reminders", new ArrayList<>(Arrays.asList("08:00")));
            data.put("id", String.valueOf(Utilities.getNextAlarmId(this)));
            data.put("alarmStatus", true);
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
            sharedPreferences.edit().putInt("num_meals",Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST).size() + Constants.FIXED_MEALS).commit();
            db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(activity).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void documentReference) {
                            Log.d("UserProfileActivity", "DocumentSnapshot written with ID: ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("UserProfileActivity", "Error adding document", e);
                        }
                    });
        }

        Utilities.saveBooleanDataInSharedpref(this, Constants.KEY_LIST_CHANGED, true);
        db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("UserProfileActivity", document.getId() + " => " + document.getData());
                                Utilities.saveDataInSharedpref(getApplicationContext(), Constants.KEY_ACTIVITY_COUNT, String.valueOf(task.getResult().size()));
                            }
                        } else {
                            Log.d("UserProfileActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void addtoPhysicalActivityLayout(final String activity,final int typeOfAct) {

        LayoutInflater inflater = LayoutInflater.from(this);
        Log.d("UserProfileActivity", "addtoPhysicalActivityLayout: ");
        View view = inflater.inflate(R.layout.activity_user_profile, null,false);

//        final View inflatedLayout= inflater.inflate(R.layout.textview_with_delete, (ViewGroup) view, false);
        final View inflatedLayout= inflater.inflate(R.layout.textview_with_edit, (ViewGroup) view, false);
        ((TextView)inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).setText(activity);

        (inflatedLayout.findViewById(R.id.delete_physical_activity_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListFromSharedPref(Constants.KEY_PHY_ACT_LIST,activity);
                removeListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST,activity);
                removeListFromSharedPref(Constants.KEY_MEALS_LIST, activity);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(activity);
                Log.d("UserProfileActivity", "onClick: removing "+activity);
                editor.apply();
                editor.commit();

                //gb: remove this activity from the firestore database also
                db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(activity).delete() ;
                Utilities.saveDataInSharedpref(v.getContext(), Constants.KEY_ACTIVITY_COUNT, String.valueOf(Integer.parseInt(Utilities.getDataFromSharedpref(v.getContext(),Constants.KEY_ACTIVITY_COUNT))-1));

//                Toast.makeText(getApplicationContext(),"Deleted " +activity,Toast.LENGTH_SHORT).show();
                Utilities.saveBooleanDataInSharedpref(v.getContext(), Constants.KEY_LIST_CHANGED, true);
                if(typeOfAct==PHYSICAL_ACTIVITY) {
                    phyActLinearLayout.removeView(inflatedLayout);
                }else if(typeOfAct ==LEISURE_ACTIVITY){
                    leisureActivityLinearLayout.removeView(inflatedLayout);
                }else if(typeOfAct == ADD_MEAL_ACTIVITY){
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
                    sharedPreferences.edit().putInt("num_meals",Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST).size() + Constants.FIXED_MEALS).commit();
                    mealLinearLayout.removeView(inflatedLayout);
                }
            }
        });


        if(typeOfAct==PHYSICAL_ACTIVITY) {
            phyActLinearLayout.addView(inflatedLayout);
        }else if(typeOfAct ==LEISURE_ACTIVITY){
            leisureActivityLinearLayout.addView(inflatedLayout);
        }else if(typeOfAct == ADD_MEAL_ACTIVITY){
            mealLinearLayout.addView(inflatedLayout);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.activity_user_profile);
        Log.d("UserProfileActivity", "onCreate: started");
        init();

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
        if (id == R.id.action_save) {
//            Toast.makeText(this,"Saving...",Toast.LENGTH_SHORT).show();
            final String heightStr = userHeight.getText().toString();
            String weightStr = userWeight.getText().toString();
            final Context mContext = this;
            try {
                final double height = Double.parseDouble(heightStr);
                final double weight = Double.parseDouble(weightStr);

                if (height > 0.0 && weight > 0.0) {
                    Map<String, Double> values = new HashMap<>();
                    values.put("height", height);
                    values.put("weight", weight);

                    db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                            set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();
                            Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_HEIGHT,String.valueOf(height));
                            Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_WEIGHT,String.valueOf(weight));

                        }
                    });

                    Map<String,String> today_date = new HashMap<>();
                    today_date.put(sdf.format(new Date()), sdf.format(new Date()));
                    db.collection("records_dates/" + patient.getId() + "/dates").document("dates").
                            set(today_date);
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.valid_value), Toast.LENGTH_SHORT).show();
                }

                Map<String, String> values = new HashMap<>();
                values.put("height", String.valueOf(height));
                values.put("weight", String.valueOf(weight));
                values.put("name", String.valueOf(aboutMe.getText()));
                values.put("alarm_pref", alrmPrefSpinner.getSelectedItem().toString());

                db.collection("user_profile/" + patient.getId() + "/patient").document("info").
                        set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(mContext, "Successfully Saved!", Toast.LENGTH_SHORT).show();
                        Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_NAME,
                                String.valueOf(aboutMe.getText()));
                        Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_ALARM_PREF,
                                String.valueOf(alrmPrefSpinner.getSelectedItemPosition()));

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        else if(id ==android.R.id.home){
            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Utilities.saveDataInSharedpref(getApplicationContext(), Constants.KEY_NAME,
                    String.valueOf(aboutMe.getText()));
            Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_HEIGHT, String.valueOf(userHeight.getText()));
            Utilities.saveDataInSharedpref(getApplicationContext(),Constants.KEY_WEIGHT,String.valueOf(userWeight.getText()));
        }catch (Exception e){

        }
    }


}
