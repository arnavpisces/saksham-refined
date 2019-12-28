package com.zuccessful.trueharmony.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.receivers.ReminderReceiver;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.Calendar;
import java.util.Locale;


public class AddInjActivity extends AppCompatActivity {


    private Spinner injectionSpinner,repeatSpinner;
    private String  injectionSelected,repeatSelected;
    private LinearLayout injTimeLayout;
    private SakshamApp app;
    private FirebaseFirestore db;
    private final String TAG = "ADDINJ_TAG";


    private static String year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inj_sch);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();

        injTimeLayout = findViewById(R.id.inj_time_pref);

        injectionSpinner = findViewById(R.id.inj_name_et);
        repeatSpinner = findViewById(R.id.spinner_repeat);


        final Calendar c = Calendar.getInstance();
        year = Integer.toString(c.get(Calendar.YEAR));
        month = Integer.toString(c.get(Calendar.MONTH));
        day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.injectionList,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        injectionSpinner.setAdapter(adapter);
        Intent intent =  getIntent();
        String name = intent.getStringExtra("editName");
        if (name!=null)
        {
            int spinnerPosition = adapter.getPosition(name);
            injectionSpinner.setSelection(spinnerPosition);
        }

        injectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                injectionSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                repeatSelected = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void setDate(View view)
    {
        DialogFragment newFragment = new DateFrag();
        newFragment.show(getFragmentManager(),"date picker");
    }




    public void submitInj(View view) {
        String name = injectionSelected;
        String repeated = repeatSelected;
        Toast.makeText(AddInjActivity.this,name+" Injection Submitted", Toast.LENGTH_SHORT).show();
        setInjAlarm(name,repeated);
        finish();
    }


    private void setInjAlarm(String name, String repeated) {

        Injection injectionObj = new Injection(AddInjActivity.this);
        injectionObj.setName(name);
        injectionObj.setId(name);
        injectionObj.setDay(day);
        injectionObj.setMonth(month);
        injectionObj.setYear(year);
        injectionObj.setRepeated(repeated);
        injectionObj.setStatus("Not yet");
       //injectionObj.setReminderStatus("No");
        //reqcode set
        int reqcode = (int) System.currentTimeMillis();
        injectionObj.setReqCode(String.valueOf(reqcode));
        injectionObj.setHour(String.valueOf(8+12));
        injectionObj.setMin(String.valueOf(0));
        injectionObj.setTitle(name + " Injection Day!");
        injectionObj.setContent("Have you taken it?");
        injectionObj.setType("two");

        /*Intent mIntent = new Intent(AddInjActivity.this, ReminderReceiver.class);
        Utilities.setExtraForIntent(mIntent, "injRaw", injectionObj);*/
        DocumentReference documentReference = db.collection("alarms/" +
                app.getAppUser(null).getId() + "/injection").document(name);
        documentReference.set(injectionObj);
        scheduleAlarm(injectionObj);

    }

    private void scheduleAlarm(Injection injectionObj) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(injectionObj.getYear()),Integer.parseInt(injectionObj.getMonth()),Integer.parseInt(injectionObj.getDay()),4,0,0);

        Intent intent = new Intent(getApplicationContext(), ReminderReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("INJECTION",injectionObj);
        intent.putExtra("DATA",args);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),Integer.parseInt(injectionObj.getReqCode()),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //repeating alarm code
        if (injectionObj.getRepeated().equals("Weekly"))
        {
            calendar.add(Calendar.DATE,7);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7,pendingIntent);
        }
        if (injectionObj.getRepeated().equals("Monthly"))
        {
            calendar.add(Calendar.DATE,30);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*30,pendingIntent);
        }
        if (injectionObj.getRepeated().equals("Fortnight"))
        {
            calendar.add(Calendar.DATE,14);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*14,pendingIntent);
        }
        if (injectionObj.getRepeated().equals("Once in 3 weeks"))
        {
            calendar.add(Calendar.DATE,21);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*21,pendingIntent);
        }

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

    public static class DateFrag extends DialogFragment
    {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSetListener, y,m,d);

            dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), dialog);
            dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), (DialogInterface.OnClickListener)null);
//            return new DatePickerDialog(getActivity(), dateSetListener, y, m, d);
        return dialog;
        }
        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view,int y,int m,int d) {

                        year = Integer.toString(y);
                        month = Integer.toString(m+1);
                        day = Integer.toString(d);
//                        Toast.makeText(getActivity(), "selected date is " + day +
//                                " / " + month +
//                                " / " + year, Toast.LENGTH_SHORT).show();
                    }
                };
    }
}