package com.zuccessful.trueharmony.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.AppConstant;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.pojo.InjectionRecord;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReminderReceiver extends BroadcastReceiver {
    private final String TAG = "ReminderReceiver";
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("ReminderReceiver", "onReceive: inside Reminder");
        Bundle args = intent.getBundleExtra("DATA");
        final Injection injection = (Injection) args.getSerializable("INJECTION");
        injection.setStatus("Have to take");
        try {db.collection("alarms")
                .document(app.getAppUser(null)
                        .getId())
                .collection("injection")
                .document(injection.getName())
                .set(injection);
        } catch (Exception e) { e.printStackTrace(); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Injection";
            String description = "Injection alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("injection", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
            Intent yesIntent = new Intent(context,ReminderActionReceiver.class);
            yesIntent.setAction(AppConstant.YES_ACTION);
            Bundle args2 = new Bundle();
            args2.putSerializable("INJECTION",injection);
            yesIntent.putExtra("DATA",args2);

            Intent noIntent = new Intent(context,ReminderActionReceiver.class);
            yesIntent.setAction(AppConstant.NO_ACTION);
            PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(injection.getReqCode()),yesIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent noPendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(injection.getReqCode()),noIntent,PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "injection")
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle(injection.getName())
                    .setContentText("Did you take the injection")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.drawable.common_google_signin_btn_icon_dark,"YES",yesPendingIntent)
                    //.addAction(R.drawable.common_google_signin_btn_icon_light,"NO",noPendingIntent)
                    .setOngoing(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(Integer.parseInt(injection.getReqCode()),builder.build());

            final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            //Start service which will wait for 3 days for you to take the injection and then dismiss
            // the notification if you don't take the injection
            Handler h = new Handler();
            long delay = 86400*3*1000;
            long delay2 = 5000;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //missed the notification
                    StatusBarNotification[] notifications = new StatusBarNotification[0];
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        notifications = notificationManager.getActiveNotifications();
                    }
                    for (StatusBarNotification notification : notifications) {
                        if (notification.getId() == Integer.parseInt(injection.getReqCode())) {
                            // Do something.
                            notificationManager.cancel(Integer.parseInt(injection.getReqCode()));
                            injection.setStatus("Missed");
                            Toast.makeText(context, injection.getName()+" Missed!", Toast.LENGTH_LONG).show();
                            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                            InjectionRecord injectionRecord = new InjectionRecord(injection.getName(),timeStamp,"Missed",injection.getRepeated());

                            //update date
                            SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                            Map<String, Object> today_date = new HashMap<>();
                            today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
                            db.collection("patient_injr_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());

                            //update record in firebase
                            SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                            try {db.collection("patient_inj_logs/")
                                    .document(app.getAppUser(null)
                                            .getId())
                                    .collection(sdf.format(new Date()))
                                    .document(injection.getName())
                                    .set(injectionRecord);
                            } catch (Exception e) { e.printStackTrace(); }


                            try {db.collection("alarms")
                                    .document(app.getAppUser(null)
                                            .getId())
                                    .collection("injection")
                                    .document(injection.getName())
                                    .set(injection);
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                    }
                }
            },delay);

        }

    }

