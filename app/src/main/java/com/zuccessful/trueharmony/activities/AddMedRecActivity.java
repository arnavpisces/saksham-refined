package com.zuccessful.trueharmony.activities;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class AddMedRecActivity extends AppCompatActivity {
    private TextInputLayout nameLayout, descLayout;
    private TextInputEditText descEditText;
    private Spinner medicineSpinner;
    private String  medicineSlected;
    private LinearLayout medTimeLayout;
    private ArrayList<EditText> timesEditText;
    private MaterialDayPicker dayPicker;
    private AlarmManager alarmManager;
    private SakshamApp app;
    private FirebaseFirestore db;
    private PendingIntent pendingIntent;
    private ProgressBar progressBar;

    private String[] timesDef = {"08:00 AM", "02:00 PM", "08:00 PM"};


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.activity_add_med_rec);
        progressBar = findViewById(R.id.progressbar);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        medTimeLayout = findViewById(R.id.med_time_pref);
//        nameLayout = findViewById(R.id.med_name_lay);
        descLayout = findViewById(R.id.med_desc_lay);
        medicineSpinner = findViewById(R.id.med_name_et);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        ArrayAdapter medAdapter = ArrayAdapter.createFromResource(this,
                R.array.medicineList, R.layout.spinner_item);

        medAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        medicineSpinner.setAdapter(medAdapter);

        descEditText = findViewById(R.id.med_desc_et);
        dayPicker = findViewById(R.id.repeat_days);
        attachTimeListeners(1);

        medicineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                medicineSlected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeActionBarFont();
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null) {

                handleEditAlarm();

            }
        }
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
        tv.setText(R.string.add_a_medicine); // ActionBar title text

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

    private void handleEditAlarm() {
        Medication medication = (Medication) getIntent().getSerializableExtra(Constants.MED_OBJ);

        Toast.makeText(getApplicationContext(), "medication received : " + medication.getName()
                , Toast.LENGTH_SHORT).show();

//        medicineSpinner.setText(medication.getName());
        descEditText.setText(medication.getDescription());
        Map<String,Boolean> daysMap = medication.getDays();
        List<MaterialDayPicker.Weekday> weekdayList = new ArrayList<>();

//                for (String name : daysMap.keySet())
        {
            // search  for value
//                    Boolean isPresent = daysMap.get(name);
            weekdayList.add(MaterialDayPicker.Weekday.FRIDAY);
            weekdayList.add(MaterialDayPicker.Weekday.TUESDAY);
//                    System.out.println("Key = " + name + ", Value = " + url);
        }
        dayPicker.setSelectedDays(weekdayList);

        switch (medication.getReminders().size()){
            case 1:{
                ((RadioButton)findViewById(R.id.med_reminder_1)).setChecked(true);
                break;
            }
            case 2:{
                ((RadioButton)findViewById(R.id.med_reminder_2)).setChecked(true);
                break;
            }
            case 3:{
                ((RadioButton)findViewById(R.id.med_reminder_3)).setChecked(true);
                break;
            }
        }
    }

    public static void selectTime(Context context, final EditText editText, int h, int m) {
        // TODO: Fix this method
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);

        if (h >= 0 && h < 24 && m >= 0 && m < 60) {
            mHour = h;
            mMin = m;
        }
        Locale.setDefault(Locale.ENGLISH);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay,
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
                    editText.setText(output);
                }catch(ParseException pe){
                    pe.printStackTrace();
                }
//                editText.setText(String.format(Locale.ENGLISH, "%02d:%02d", i, i1));


            }
        }, mHour, mMin, false);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), timePickerDialog);

        timePickerDialog.show();
    }

    public void submitMed(View view) {
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<String> reminders = new ArrayList<>();
        final String name, desc;
        Map<String, Boolean> days = new HashMap<>();
        name = medicineSlected;
        if (name.equals("")) {
            nameLayout.setError(getResources().getString(R.string.med_field_error));
            return;
        }
        desc = descEditText.getText().toString();
        List<MaterialDayPicker.Weekday> daysSelected = dayPicker.getSelectedDays();
        if (daysSelected.size() < 1) {
            Toast.makeText(AddMedRecActivity.this, getResources().getString(R.string.select_days), Toast.LENGTH_SHORT).show();
            return;
        }

        setMedAlarm(reminders, name, desc, days, daysSelected);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null) {
                Medication mItem = (Medication) getIntent().getSerializableExtra(Constants.MED_OBJ);

                SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" +
                        SakshamApp.getInstance().getAppUser(null).getId() +
                        "/medication").document(mItem.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }

                });

            }
        }else{
            progressBar.setVisibility(View.GONE);
            Log.d("AlarmService", "finishing");

            finish();
        }


    }

    private void setMedAlarm(final ArrayList<String> reminders, final String name,
                             String desc, Map<String, Boolean> days,
                             List<MaterialDayPicker.Weekday> daysSelected) {

        final ArrayList<Integer> alarm_ids = new ArrayList<>();
        final ArrayList<Integer> weekdays = new ArrayList<>();

        for (EditText editText : timesEditText) {
            String time = editText.getText().toString();
            if (!time.equals("")) {
                reminders.add(time);
            }
        }


        for (MaterialDayPicker.Weekday day : daysSelected) {

            for (int i = 0; i < reminders.size(); i++) {
                alarm_ids.add(Utilities.getNextAlarmId(this));
            }

            switch (day) {
                case MONDAY:
                    weekdays.add(Calendar.MONDAY);
                    days.put("mon", true);
                    break;
                case TUESDAY:
                    weekdays.add(Calendar.TUESDAY);
                    days.put("tue", true);
                    break;
                case WEDNESDAY:
                    weekdays.add(Calendar.WEDNESDAY);
                    days.put("wed", true);
                    break;
                case THURSDAY:
                    weekdays.add(Calendar.THURSDAY);
                    days.put("thu", true);
                    break;
                case FRIDAY:
                    weekdays.add(Calendar.FRIDAY);
                    days.put("fri", true);
                    break;
                case SATURDAY:
                    weekdays.add(Calendar.SATURDAY);
                    days.put("sat", true);
                    break;
                case SUNDAY:
                    weekdays.add(Calendar.SUNDAY);
                    days.put("sun", true);
                    break;
            }
        }
        final Medication medicationObj = new Medication(name, desc, days, reminders);
        Utilities.saveMedicineToList(medicationObj);

        if(!Utilities.isInternetOn(getApplicationContext())) {
            Log.d("AlarmService", "no internet");
            if (weekdays.size() * medicationObj.getReminders().size() == alarm_ids.size()) {      // Check
                for (int slot = 0; slot < medicationObj.getReminders().size(); slot++) {
                    for (int i = 0; i < weekdays.size(); i++) {
                        Intent mIntent = new Intent(AddMedRecActivity.this, AlarmReceiver.class);
                        Utilities.setExtraForIntent(mIntent, "medRaw", medicationObj);
                        mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                        mIntent.putExtra("medSlot", slot);
                        DateFormat df = new SimpleDateFormat("hh:mm aa");
                        //Date/time pattern of desired output date
                        DateFormat outputformat = new SimpleDateFormat("HH:mm");
                        Date date = null;
                        String output = null;
                        try {
                            //Conversion of input String to date
                            date = df.parse(reminders.get(slot));
                            //old date format to new date format
                            output = outputformat.format(date);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                        }
                        int h = Integer.parseInt(output.split(":")[0]);
                        int m = Integer.parseInt(output.split(":")[1]);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, h);
                        calendar.set(Calendar.MINUTE, m);
                        calendar.set(Calendar.SECOND, 0);
                        scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i));
                        Log.d("AlarmService", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                    }
                }
                Toast.makeText(AddMedRecActivity.this, "Medicine: " + name + " added successfully.", Toast.LENGTH_SHORT).show();


            }
        }

            Log.d("AlarmService", "above document");
            DocumentReference documentReference = db.collection("alarms/" +
                    app.getAppUser(null).getId() + "/medication").document(name);
            medicationObj.setId(documentReference.getId());
            medicationObj.setAlarmIds(alarm_ids);
//        final Intent mIntent = new Intent(AddMedRecActivity.this, AlarmService.class);
            documentReference.set(medicationObj).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    if (weekdays.size() * medicationObj.getReminders().size() == alarm_ids.size()) {      // Check
                        for (int slot = 0; slot < medicationObj.getReminders().size(); slot++) {
                            for (int i = 0; i < weekdays.size(); i++) {
                                Intent mIntent = new Intent(AddMedRecActivity.this, AlarmReceiver.class);
                                Utilities.setExtraForIntent(mIntent, "medRaw", medicationObj);
                                mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                                mIntent.putExtra("medSlot", slot);
                                DateFormat df = new SimpleDateFormat("hh:mm aa");
                                //Date/time pattern of desired output date
                                DateFormat outputformat = new SimpleDateFormat("HH:mm");
                                Date date = null;
                                String output = null;
                                try {
                                    //Conversion of input String to date
                                    date = df.parse(reminders.get(slot));
                                    //old date format to new date format
                                    output = outputformat.format(date);
                                } catch (ParseException pe) {
                                    pe.printStackTrace();
                                }
                                int h = Integer.parseInt(output.split(":")[0]);
                                int m = Integer.parseInt(output.split(":")[1]);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, h);
                                calendar.set(Calendar.MINUTE, m);
                                calendar.set(Calendar.SECOND, 0);
                                scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i));
                                Log.d("AlarmService", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                            }
                        }
                        Toast.makeText(AddMedRecActivity.this, "Medicine: " + name + " added successfully.", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (weekdays.size() * medicationObj.getReminders().size() == alarm_ids.size()) {      // Check
                        for (int slot = 0; slot < medicationObj.getReminders().size(); slot++) {
                            for (int i = 0; i < weekdays.size(); i++) {
                                Intent mIntent = new Intent(AddMedRecActivity.this, AlarmReceiver.class);
                                Utilities.setExtraForIntent(mIntent, "medRaw", medicationObj);
                                mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                                mIntent.putExtra("medSlot", slot);
                                DateFormat df = new SimpleDateFormat("hh:mm aa");
                                //Date/time pattern of desired output date
                                DateFormat outputformat = new SimpleDateFormat("HH:mm");
                                Date date = null;
                                String output = null;
                                try {
                                    //Conversion of input String to date
                                    date = df.parse(reminders.get(slot));
                                    //old date format to new date format
                                    output = outputformat.format(date);
                                } catch (ParseException pe) {
                                    pe.printStackTrace();
                                }
                                int h = Integer.parseInt(output.split(":")[0]);
                                int m = Integer.parseInt(output.split(":")[1]);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, h);
                                calendar.set(Calendar.MINUTE, m);
                                calendar.set(Calendar.SECOND, 0);
                                scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i));
                                Log.d("AlarmService", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                            }
                        }
                        Toast.makeText(AddMedRecActivity.this, "Medicine: " + name + " added successfully.", Toast.LENGTH_SHORT).show();
                    }

//                pendingIntent = PendingIntent.getService(AddMedRecActivity.this, alarm_id, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    pendingIntent = PendingIntent.getBroadcast(AddMedRecActivity.this, alarm_id, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

                }
            });

    }

    private void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, Intent intent, int request_code, int dayOfWeek) {
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Set this to whatever you were planning to do at the given time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddMedRecActivity.this, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        finish();
    }


    public void attachTimeListeners(int count) {
        timesEditText = new ArrayList<>();
        medTimeLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            final EditText et = new EditText(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            et.setLayoutParams(layoutParams);
            et.setText(timesDef[i]);
            et.setFocusable(false);
            final int h = Integer.parseInt(timesDef[i].split(" ")[0].split(":")[0]);
            final int m = Integer.parseInt(timesDef[i].split(" ")[0].split(":")[1]);
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    selectTime(AddMedRecActivity.this, et, h, m);
                }
            });

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (timesEditText.contains(et)) {
                        timesDef[timesEditText.indexOf(et)] = et.getText().toString();
                    }
                }
            });

            medTimeLayout.addView(et);
            timesEditText.add(et);
        }
    }

    public void updateTimes(View view) {
        int times = 1;
        switch (view.getId()) {
            case R.id.med_reminder_1:
                times = 1;
                break;
            case R.id.med_reminder_2:
                times = 2;
                break;
            case R.id.med_reminder_3:
                times = 3;
                break;
        }
        attachTimeListeners(times);
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
