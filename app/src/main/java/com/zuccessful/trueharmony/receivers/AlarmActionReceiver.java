package com.zuccessful.trueharmony.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.DialogeActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.services.AlarmService;
import com.zuccessful.trueharmony.services.PlayRingtone;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class AlarmActionReceiver extends BroadcastReceiver {

    public final static String ACTION_TAKEN = "com.zuccessful.trueharmony.ACTION_TAKEN";
    public final static String ACTION_DAILY_DONE = "com.zuccessful.trueharmony.ACTION_DAILY_DONE";
    public final static String ACTION_DAILY_MISSED = "com.zuccessful.trueharmony.ACTION_DAILY_MISSED";
    public final static String ACTION_MISSED = "com.zuccessful.trueharmony.ACTION_MISSED";
    public static final String DIALOGE_MESSAGE = "dialogeMessage";
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarm_id", 0);
        Log.d("AlarmService", "Something is clicked in the notification : " + alarmId);

        //turn off the alarm here
        Intent stopIntent = new Intent(context.getApplicationContext(), PlayRingtone.class);
        context.getApplicationContext().stopService(stopIntent);

        if (alarmId >= 0) {
            String action = intent.getAction();
            Log.d("AlarmService", action);
            if (action != null) {
                SakshamApp app = SakshamApp.getInstance();
                FirebaseFirestore db = app.getFirebaseDatabaseInstance();
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    switch (action) {
                        case ACTION_TAKEN: {
                            MedicineRecord medicineRecord = null;
                            String medId = intent.getStringExtra("medId");
                            String medName = intent.getStringExtra("medName");
                            int medSlot = intent.getIntExtra("medSlot", 0);
                            int alarmID = intent.getIntExtra("alarm_id",0);
                            Utilities.removeFromTimersList(alarmID);
                            Log.d("AlarmService", "Action Taken for slot: " + medSlot);
//                        Intent i = new Intent(context, AlarmReceiver.class);
//                        context.stopService(i);
                            medicineRecord = new MedicineRecord(medId, medName, Calendar.getInstance().getTimeInMillis(), medSlot, true);
                            db.collection("patient_med_logs/" + context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error")
                                    + "/" + sdf.format(new Date())).document(medName).set(medicineRecord, SetOptions.merge());
                            Map<String, Object> data = new HashMap<>();
                            data.put(sdf.format(new Date()), sdf.format(new Date()));
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");
                            db.collection("patient_med_dates/"+patient_id+"/dates").document("dates").set(data, SetOptions.merge());


                            //                            Intent i2 = (Intent) intent.clone();
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, i2, PendingIntent.FLAG_CANCEL_CURRENT);
//                            alarmManager.cancel(pendingIntent);
//                            pendingIntent.cancel();
                            Log.d("AlarmService", "Medication Taken");

                            Intent dialogeActivityIntent = new Intent(context,DialogeActivity.class);
                            dialogeActivityIntent.putExtra(DIALOGE_MESSAGE,context.getString(R.string.medicine_taken_message));
                            context.startActivity(dialogeActivityIntent);
                            try{
                                closeNotificationBar(context);
                            }catch (Exception e){
                                Log.d("AlarmService","Closing not bar exception : " +e);
                            }
                            break;
                        }
                        case ACTION_MISSED: {
                            MedicineRecord medicineRecord = null;
                            String medId = intent.getStringExtra("medId");
                            String medName = intent.getStringExtra("medName");
                            int alarmID = intent.getIntExtra("alarm_id",0);
                            Utilities.removeFromTimersList(alarmID);
                            int medSlot = intent.getIntExtra("medSlot", 0);
                            Log.d("AlarmService", "Action Missed for slot: " + medSlot);
                            medicineRecord = new MedicineRecord(medId, medName, Calendar.getInstance().getTimeInMillis(), medSlot, false);
                            db.collection("patient_med_logs/" +
                                    context.getSharedPreferences(PREF_PID, MODE_PRIVATE)
                                            .getString(PREF_PID, "error")
                                    + "/" + sdf.format(new Date())).document(medName).set(medicineRecord, SetOptions.merge());
                            Map<String, Object> data = new HashMap<>();
                            data.put(sdf.format(new Date()), sdf.format(new Date()));
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");
                            db.collection("patient_med_dates/"+patient_id+"/dates").document("dates").set(data, SetOptions.merge());

                            Log.d("AlarmService", "Medication Missed");
                            Intent dialogeActivityIntent = new Intent(context,DialogeActivity.class);
                            dialogeActivityIntent.putExtra(DIALOGE_MESSAGE,context.getString(R.string.medicine_missed_message));
                            context.startActivity(dialogeActivityIntent);
                            try{
                                closeNotificationBar(context);
                            }catch (Exception e){
                                Log.d("AlarmService","Closing not bar exception : " +e);
                            }
                            break;
                        }
                        case ACTION_DAILY_DONE: {

                            DailyRoutineRecord dailyRoutRecord = null;
                            String dailyRoutId = intent.getStringExtra("dailyRoutId");
                            String dailyRoutName = intent.getStringExtra("dailyRoutName");
                            int dailyRoutSlot = intent.getIntExtra("dailyRoutSlot", 0);
                            Log.d("AlarmService", "Action Taken for slot: " + dailyRoutSlot);
//                        Intent i = new Intent(context, AlarmReceiver.class);
//                        context.stopService(i);
                            dailyRoutRecord = new DailyRoutineRecord(dailyRoutId, dailyRoutName,
                                    Calendar.getInstance().getTimeInMillis(), dailyRoutSlot, 1);
                            db.collection("patient_daily_rout_logs/" +
                                    context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                    "/" + sdf.format(new Date())+"/"+dailyRoutName+"/details").document(String.valueOf(dailyRoutSlot))
                                    .set(dailyRoutRecord, SetOptions.merge());
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");
                            Map<String, Object> today_date = new HashMap<>();
                            today_date.put(sdf.format(new Date()), sdf.format(new Date()));
                            db.collection("patient_dr_dates/"+patient_id+"/dates").document("dates").set(today_date, SetOptions.merge());
                            Map<String, Object> task_name = new HashMap<>();
                            task_name.put(dailyRoutName, dailyRoutName);
                            db.collection("patient_dr_dates/"+patient_id+"/dates").document("tasks").set(task_name,SetOptions.merge());
                            Log.d("Kya",context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error"));
//                            db.document("patient_dr_dates/"+
//                                    context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error").set(sdf.format(new Date()),SetOptions.merge());
                            //db.collection("patient_daily_rout_logs").document(context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error")).set(sdf.format(new Date()),SetOptions.merge());
//                            Intent i2 = (Intent) intent.clone();
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, i2, PendingIntent.FLAG_CANCEL_CURRENT);
//                            alarmManager.cancel(pendingIntent);
//                            pendingIntent.cancel();
                            Log.d("AlarmService", "Daily Routine Task Done");
                            cancelTimerInService(context);
                            break;
                        }
                        case ACTION_DAILY_MISSED: {
                            DailyRoutineRecord dailyRoutRecord = null;
                            String dailyRoutId = intent.getStringExtra("dailyRoutId");
                            String dailyRoutName = intent.getStringExtra("dailyRoutName");
                            int dailyRoutSlot = intent.getIntExtra("dailyRoutSlot", 0);

                            Log.d("AlarmService", "Action Missed for slot: " + dailyRoutSlot);
                            dailyRoutRecord = new DailyRoutineRecord(dailyRoutId, dailyRoutName,
                                    Calendar.getInstance().getTimeInMillis(), dailyRoutSlot, 0);
                            db.collection("patient_daily_rout_logs/" +
                                    context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                    "/" + sdf.format(new Date())+"/"+dailyRoutName+"/details").document(String.valueOf(dailyRoutSlot))
                                    .set(dailyRoutRecord, SetOptions.merge());
                            Log.d("AlarmService", "Daily Routine Task Missed");
                            Map<String, Object> data = new HashMap<>();
                            data.put(sdf.format(new Date()), sdf.format(new Date()));
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");
                            db.collection("patient_dr_dates/"+patient_id+"/dates").document("dates").set(data, SetOptions.merge());
                            Map<String, Object> task_name = new HashMap<>();
                            task_name.put(dailyRoutName, dailyRoutName);
                            db.collection("patient_dr_dates/"+patient_id+"/dates").document("tasks").set(task_name, SetOptions.merge());
                            break;
                        }
                    }
                } else {
                    Log.e("AlarmService", "Alarm Manager Null");
                }
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(alarmId);
        } else {
            Log.e("AlarmService", "Object Issue");
        }


    }

    private void cancelTimerInService(Context context){
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.addCategory("cancel_timer");
        context.startService(serviceIntent);
    }

    private void closeNotificationBar(Context context) {
            Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(closeIntent);
    }
}
