package com.zuccessful.trueharmony.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
//import com.zuccessful.trueharmony.activities.DailyRoutineActivity;
import com.zuccessful.trueharmony.activities.DialogeActivity;
import com.zuccessful.trueharmony.activities.MedicalAdherenceActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.*;
import com.zuccessful.trueharmony.receivers.AlarmActionReceiver;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;
import static com.zuccessful.trueharmony.receivers.AlarmActionReceiver.DIALOGE_MESSAGE;

public class  AlarmService extends JobIntentService {

    private Medication medication = null;
    private Injection injection = null;
    private DailyRoutine dailyRoutine = null;
    private int alarm_id, medSlot, dailyRoutSlot;
    private String name;


    private static int JOB_ID = 10101;

//    public AlarmService() {
//        super("AlarmService");
//    }


    public static void enqueueWork(Context context, int alarmID, Intent work) {
        enqueueWork(context, AlarmService.class, JOB_ID, work);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasCategory("cancel_timer")) {
                cancelTimer();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        Log.d("AlarmService", "Inside the Service");
        medication = (Medication) Utilities.getExtraFromIntent(intent, "medRaw");
        dailyRoutine = (DailyRoutine) Utilities.getExtraFromIntent(intent, "dailyRoutRaw");
        injection = (Injection) Utilities.getExtraFromIntent(intent, "med");
        name = intent.getStringExtra("name");
        alarm_id = intent.getIntExtra("alarm_id", 0);
        medSlot = intent.getIntExtra("medSlot", 0);
        dailyRoutSlot = intent.getIntExtra("dailyRoutSlot", 0);

        if (medication != null) {
            Log.d("AlarmService", "Medication object passed normally");
//            alarm_id = medication.getAlarmId();
            name = medication.getName();
            sendMedNotification("Consume \"" + medication.getName() + "\" at "
                    + medication.getReminders().get(medSlot), intent);
        } else if (dailyRoutine != null) {
            Log.d("AlarmService", "Daily Routine task object passed normally");
//            alarm_id = dailyRoutine.getAlarmId();
            name = dailyRoutine.getName();
            sendDailyRoutNotification(dailyRoutine.getName() + " at " + dailyRoutine.getReminders().get(dailyRoutSlot));
//        }
//        else if (name != null && alarm_id >= 0) {
//            Log.w("AlarmService", "Issue in passing medication object");
//            sendNotification("Consume \"" + name + "\"", intent);

            // add 2, 10 minutes interval timers if no action taken
//            setTwoTimers(dailyRoutine, String.valueOf(dailyRoutSlot),name + " at " + dailyRoutine.getReminders().get(dailyRoutSlot));
        } else
            Log.e("AlarmService", "Nothing Being Passed Properly");
    }

    Timer timer1 = new Timer();

    private void setTwoTimers(final DailyRoutine dailyRoutine, final String slotId, final String msg) {
        // todo also check if task action already taken, then cancel timer
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                sendDailyRoutNotification(msg);
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (Utilities.getTimerCount(getApplicationContext()) >=2){
                            cancelTimer();
                            updateDb(dailyRoutine,slotId);
                            return;
                        }
                        Utilities.incrementTimerCount(getApplicationContext());
                        sendDailyRoutNotification(msg);
                    }
                }, 10 * 60 * 1000);
            }
        }, 10 * 60 * 1000);
    }

    private void setTimerForDailyMedNotificaion(final Medication medication, final int notId) {
        // todo also check if task action already taken, then cancel timer
        final Timer medTimer = new Timer();
        medTimer.schedule(new ScheduleSomething(medication,notId), 1 * 60 * 1000);
    }

    private void updateDb(DailyRoutine dailyRoutine,String slotId) {

        DailyRoutineRecord dailyRoutRecord = null;
        String dailyRoutId = slotId;
        String dailyRoutName = dailyRoutine.getName();
        int dailyRoutSlot = Integer.parseInt(slotId);

        Log.d("AlarmService", "Action Missed for slot: " + dailyRoutSlot);
        dailyRoutRecord = new DailyRoutineRecord(dailyRoutId, dailyRoutName,
                Calendar.getInstance().getTimeInMillis(), dailyRoutSlot, -1);
        SakshamApp app = SakshamApp.getInstance();
        FirebaseFirestore db = app.getFirebaseDatabaseInstance();
        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
        db.collection("patient_daily_rout_logs/" +
                getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                "/" + sdf.format(new Date())+"/"+name+"/details").document(String.valueOf(dailyRoutSlot))
                .set(dailyRoutRecord, SetOptions.merge());
    }

    private void cancelTimer() {
        if (timer1 == null) return;
        timer1.cancel();
        Utilities.resetTimerCount(getApplicationContext());
    }

    private void sendDailyRoutNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);

        // TODO: Take to a fullscreen activity for actions
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, DailyRoutineActivity.class), 0);

//        if (dailyRoutine == null) {
//            dailyRoutine = new DailyRoutine(name, null);
//            dailyRoutine.addAlarmId(alarm_id);
//        }

        Intent intent = new Intent(this, AlarmActionReceiver.class);
//        ComponentName comp = new ComponentName(this.getPackageName(), AlarmActionReceiver.class.getName());
//        intent.setComponent(comp);
        intent.setAction(AlarmActionReceiver.ACTION_DAILY_DONE);
        intent.putExtra("alarm_id", alarm_id);
        intent.putExtra("dailyRoutId", dailyRoutine.getId());
        intent.putExtra("dailyRoutName", dailyRoutine.getName());
        // TODO: Update Slot
        intent.putExtra("dailyRoutSlot", dailyRoutSlot);
        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent, 0);


        Intent intent2 = new Intent(this, AlarmActionReceiver.class);
//        intent2.setComponent(comp);
        intent2.setAction(AlarmActionReceiver.ACTION_DAILY_MISSED);
        intent2.putExtra("alarm_id", alarm_id);
        intent2.putExtra("dailyRoutId", dailyRoutine.getId());
        intent2.putExtra("dailyRoutName", dailyRoutine.getName());
        // TODO: Update Slot
        intent2.putExtra("dailyRoutSlot", dailyRoutSlot);
        PendingIntent missedPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent2, 0);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Uri alarmSound = Settings.System.DEFAULT_RINGTONE_URI;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "alarm")
                .setSmallIcon(R.drawable.ic_med_notif)
                .setContentTitle("Daily Routine Task Reminder")
                .setContentText(msg)
                .addAction(R.drawable.ic_info, "DONE", takenPendingIntent)
                .addAction(R.drawable.ic_info, "MISSED", missedPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
               // .setSound(alarmSound, AudioManager.STREAM_ALARM)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(alarm_id, mBuilder.build());
        Log.d("AlarmService", "Notification sent: " + alarm_id);
    }

    private void sendMedNotification(String msg, Intent workIntent) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);

        // TODO: Take to a fullscreen activity for actions
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MedicalAdherenceActivity.class), 0);

//        if (medication == null) {
//            medication = new Medication(medName, null, null, null);
//            medication.addAlarmId(alarm_id);
//        }

        Intent intent = new Intent(this, AlarmActionReceiver.class);
//        ComponentName comp = new ComponentName(this.getPackageName(), AlarmActionReceiver.class.getName());
//        intent.setComponent(comp);
        intent.setAction(AlarmActionReceiver.ACTION_TAKEN);
        intent.putExtra("alarm_id", alarm_id);
        intent.putExtra("medId", medication.getId());
        intent.putExtra("medName", medication.getName());
        // TODO: Update Slot
        intent.putExtra("medSlot", medSlot);
        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent, 0);


        Intent intent2 = new Intent(this, AlarmActionReceiver.class);
//        intent2.setComponent(comp);
        intent2.setAction(AlarmActionReceiver.ACTION_MISSED);
        intent2.putExtra("alarm_id", alarm_id);
        intent2.putExtra("medId", medication.getId());
        intent2.putExtra("medName", medication.getName());
        // TODO: Update Slot
        intent2.putExtra("medSlot", medSlot);
        PendingIntent missedPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent2, 0);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Uri alarmSound = Settings.System.DEFAULT_RINGTONE_URI;
        SharedPreferences sp = getSharedPreferences("alarmAudio" ,Context.MODE_PRIVATE);
        //if condition - for type of alarm
        String uri = sp.getString("URI","0");
        alarmSound = Uri.parse(uri);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                "alarm")
                .setSmallIcon(R.drawable.ic_med_notif)
                .setContentTitle(getString(R.string.medicine_reminder))
                .setContentText(msg)
                .addAction(R.drawable.ic_info, "TAKEN", takenPendingIntent)
                .addAction(R.drawable.ic_info, "MISSED", missedPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //.setSound(alarmSound, AudioManager.STREAM_ALARM)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(alarm_id, mBuilder.build());
        setTimerForDailyMedNotificaion(medication,alarm_id);
        Utilities.addToTimersList(String.valueOf(alarm_id));

        Log.d("AlarmService", "Notification sent: " + alarm_id);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("alarm", name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    private class ScheduleSomething extends TimerTask {

        Medication medication;
        int notId;

        public ScheduleSomething(Medication medication,int notId) {
            this.medication = medication;
            this.notId = notId;
        }

        @Override
        public void run() {
            Log.d("testTimer","timer says med : " +medication.toString());

            if(Utilities.getArrayListTimers().contains(String.valueOf(notId))) {
                Context context = SakshamApp.getInstance();
                Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.medicine_missed_message));
                context.startActivity(dialogeActivityIntent);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notId);
                Utilities.removeFromTimersList(notId);
            }
        }
    }


}