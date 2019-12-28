package com.zuccessful.trueharmony.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Admin on 06-08-2018.
 */

public class AddDailyRoutActivity extends AppCompatActivity {
    private LinearLayout dailyRoutTimeLayout;
    private TextView dailyRoutTaskName, dailyRoutTaskId;
    private ArrayList<EditText> timesEditText;
    private AlarmManager alarmManager;
    private SakshamApp app;
    private FirebaseFirestore db;
    private PendingIntent pendingIntent;
    private String[] timesDef = {"08:00", "14:00", "20:00"};
    String id;

    // todo set daily rout task name, get from intent

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);

        setContentView(R.layout.activity_add_daily_rout);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        dailyRoutTimeLayout = findViewById(R.id.daily_rout_time_pref);
        dailyRoutTaskName = findViewById(R.id.daily_rout_task_name);
        dailyRoutTaskId = findViewById(R.id.daily_rout_task_id);
        attachTimeListeners(Constants.DEFAULT_ACTIVITY_REPETITIONS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String taskName = getIntent().getStringExtra("dailyRoutTaskName");
        String taskId = getIntent().getStringExtra("dailyRoutTaskid");

        Log.d("AddDailyRoutActivity", "onCreate: task ID" + taskId);

//        String taskPos = getIntent().getStringExtra("dailyRoutTaskPos");
//        id = getIdFromName(taskPos);
        dailyRoutTaskName.setText(taskName);
        dailyRoutTaskId.setText(taskId);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null) {
                handleEditAlarm();

            }
        }
    }

//    private String getIdFromName(String taskName){
//        ArrayList<String> names; String id;
//        names = new ArrayList<>();
////        names.add(Utilities.getDataFromSharedpref(this, Constants.KEY_LEISURE_ACT_LIST));
////        if (names.contains(taskName)){
////            id = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST)
////        }
////        names.append(Utilities.getDataFromSharedpref(this, Constants.KEY_PHY_ACT_LIST));
////        names.append(Utilities.getDataFromSharedpref(this, Constants.KEY_MEALS));
////
////        String id = "";
//        return "";
//    }


//    private String getIdFromPos(String taskPos) {
//        if(taskPos.equalsIgnoreCase("0")){
//            return String.valueOf(DailyRoutine.DailyRoutineConstants.brush);
//        }else if(taskPos.equalsIgnoreCase("1")){
//            return String.valueOf(DailyRoutine.DailyRoutineConstants.walk);
//        } else if(taskPos.equalsIgnoreCase("2")){
//            return String.valueOf(DailyRoutine.DailyRoutineConstants.wake);
//        }else if(taskPos.equalsIgnoreCase("3")){
//            return String.valueOf(DailyRoutine.DailyRoutineConstants.bath);
//        }else if(taskPos.equalsIgnoreCase("4")){
//            return String.valueOf(DailyRoutine.DailyRoutineConstants.sleep);
//        }else if(taskPos.equalsIgnoreCase("5")){
//            return String.valueOf(DailyRoutine.DailyRoutineConstants.meal);
//        }
//        return "9";
//    }

    private void handleEditAlarm() {
        DailyRoutine dailyRoutine = (DailyRoutine) getIntent().getSerializableExtra(Constants.DAILY_ROUT_OBJ);

        Toast.makeText(getApplicationContext(), "dailyroutine task received : " + dailyRoutine.getName()
                , Toast.LENGTH_SHORT).show();

        dailyRoutTaskName.setText(dailyRoutine.getName());
        dailyRoutTaskId.setText(dailyRoutine.getId());
        switch (dailyRoutine.getReminders().size()){
            case 1:{
                ((RadioButton)findViewById(R.id.daily_rout_reminder_1)).setChecked(true);
                break;
            }
            case 2:{
                ((RadioButton)findViewById(R.id.daily_rout_reminder_2)).setChecked(true);
                break;
            }
            case 3:{
                ((RadioButton)findViewById(R.id.daily_rout_reminder_3)).setChecked(true);
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                editText.setText(String.format(Locale.ENGLISH, "%02d:%02d", i, i1));
            }
        }, mHour, mMin, false);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), timePickerDialog);

        timePickerDialog.show();
    }

    public void submitDailyRout(View view) {
        final ArrayList<String> reminders = new ArrayList<>();
        final String name, id;
        name = dailyRoutTaskName.getText().toString();
        id = dailyRoutTaskId.getText().toString();
        setDailyRoutAlarm(reminders, name, id);
        finish();
        Log.d("name",name);
//        if(name.equals("Meals")){
//            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
//            sharedPreferences.edit().putInt("num_meals",reminders.size()).commit();
//            Log.d("AddDailyRoutActivity", "submitDailyRout: Meals clicked");
//        }
    }



    private void setDailyRoutAlarm(final ArrayList<String> reminders, final String name, final String id) {
        final ArrayList<Integer> alarm_ids = new ArrayList<>();
        for (EditText editText : timesEditText) {
            String time = editText.getText().toString();
            if (!time.equals("")) {
                reminders.add(time);
            }
        }
        for (int i = 0; i < reminders.size(); i++) {
            alarm_ids.add(Utilities.getNextAlarmId(this));
        }

        final DailyRoutine dailyRoutineObj = new DailyRoutine(name, reminders,id);

        if(!Utilities.isInternetOn(getApplicationContext())){
            if (dailyRoutineObj.getReminders().size() == alarm_ids.size()) {      // Check
                for (int slot = 0; slot < dailyRoutineObj.getReminders().size(); slot++) {
//                        for (int i = 0; i < weekdays.size(); i++) {
                    Intent mIntent = new Intent(AddDailyRoutActivity.this, AlarmReceiver.class);
                    Utilities.setExtraForIntent(mIntent, "dailyRoutRaw", dailyRoutineObj);
                    mIntent.putExtra("alarm_id", alarm_ids.get(slot));
                    mIntent.putExtra("dailyRoutSlot", slot);
                    int h = Integer.parseInt(reminders.get(slot).split(":")[0]);
                    int m = Integer.parseInt(reminders.get(slot).split(":")[1]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);
                    calendar.set(Calendar.SECOND, 0);
                    scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot));
                    Log.d("AlarmService", "Setting alarm, Alarm Id: " + alarm_ids.get(slot) + " Slot: " + slot);
                }
                Toast.makeText(AddDailyRoutActivity.this, "Daily Routine Task : " + name + " added successfully.", Toast.LENGTH_SHORT).show();
            }
        }

        DocumentReference documentReference = db.collection("alarms/" +
                app.getAppUser(null).getId() + "/daily_routine").document(name);
        dailyRoutineObj.setAlarmIds(alarm_ids);
        dailyRoutineObj.setId(id);
        dailyRoutineObj.setAlarmStatus(true);
        SharedPreferences sharedPreferences = getSharedPreferences("DailyRoutineMeals", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numberMeals",dailyRoutineObj.getAlarmIds().size());
        editor.commit();


        documentReference.set(dailyRoutineObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (dailyRoutineObj.getReminders().size() == alarm_ids.size()) {      // Check
                    for (int slot = 0; slot < dailyRoutineObj.getReminders().size(); slot++) {
//                        for (int i = 0; i < weekdays.size(); i++) {
                            Intent mIntent = new Intent(AddDailyRoutActivity.this, AlarmReceiver.class);
                            Utilities.setExtraForIntent(mIntent, "dailyRoutRaw", dailyRoutineObj);
                            mIntent.putExtra("alarm_id", alarm_ids.get(slot));
                            mIntent.putExtra("dailyRoutSlot", slot);
                            int h = Integer.parseInt(reminders.get(slot).split(":")[0]);
                            int m = Integer.parseInt(reminders.get(slot).split(":")[1]);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, h);
                            calendar.set(Calendar.MINUTE, m);
                            calendar.set(Calendar.SECOND, 0);
                            scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot));
                            Log.d("AlarmService", "Setting alarm, Alarm Id: " + alarm_ids.get(slot) + " Slot: " + slot);
//                        }
                    }
                    Toast.makeText(AddDailyRoutActivity.this, "Daily Routine Task : " + name + " added successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, Intent intent, int request_code) {
//        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Set this to whatever you were planning to do at the given time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddDailyRoutActivity.this, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public void attachTimeListeners(int count) {
        timesEditText = new ArrayList<>();
        dailyRoutTimeLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            final EditText et = new EditText(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            et.setLayoutParams(layoutParams);
            et.setText(timesDef[i]);
            et.setFocusable(false);
            final int h = Integer.parseInt(timesDef[i].split(":")[0]);
            final int m = Integer.parseInt(timesDef[i].split(":")[1]);
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    selectTime(AddDailyRoutActivity.this, et, h, m);
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

            dailyRoutTimeLayout.addView(et);
            timesEditText.add(et);
        }
    }

    public void updateTimes(View view) {
        int times = 1;
        switch (view.getId()) {
            case R.id.daily_rout_reminder_1:
                times = 1;
                break;
            case R.id.daily_rout_reminder_2:
                times = 2;
                break;
            case R.id.daily_rout_reminder_3:
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
