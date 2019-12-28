package com.zuccessful.trueharmony.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.pojo.AppConstant;
import com.zuccessful.trueharmony.receivers.ReminderActionReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler {
    public static final String TAG="NotificationScheduler";

    public static void setReminder(Context context,
                                   Class<?> cls,
                                   int hour,
                                   int min,
                                   int day,
                                   int month,
                                   int year,
                                   String repeated,
                                   int reqcode)
    {
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.DAY_OF_MONTH, day);
        setcalendar.set(Calendar.MONTH, month);
        setcalendar.set(Calendar.YEAR, year);

        // cancel already scheduled reminders
        //cancelReminder(context,cls);

        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Log.d("PendingIntent",Integer.toString(reqcode));
        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqcode, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        

        String TAG = "AlarmFrequency";
        if(repeated.equals("Weekly")){
            Log.d(TAG,"Weekly");
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent);
        }

        if(repeated.equals("Fortnight")){
            Log.d(TAG,"Fortnight");
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*14, pendingIntent);
        }

        if(repeated.equals("Once in 3 weeks")){
            Log.d(TAG,"Once in 3 weeks");
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*21, pendingIntent);
        }

        if(repeated.equals("Monthly")){
            Log.d(TAG,"Monthly");
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*30, pendingIntent);
        }

        Log.d(TAG,Integer.toString(reqcode));

    }
    public static void cancelReminder(Context context,Class<?> cls,int reqcode)
    {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqcode, intent1, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context,Class<?> cls,String title,String content,int day,int month,int year ,int reqcode,String type)
    {
        Log.d("Notification","Show");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(reqcode,0);

        Notification.Builder builder = new Notification.Builder(context);


        if(type.equals("one"))
        {
            Notification notification = builder.setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.ic_schedule)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(reqcode, notification);
        }
        else
        {
            Intent yesReceive = new Intent(context,ReminderActionReceiver.class);
            yesReceive.putExtra("reqcode",reqcode+"");
            yesReceive.putExtra("day",day+"");
            yesReceive.putExtra("month",month+"");
            yesReceive.putExtra("year",year+"");
            yesReceive.setAction(AppConstant.YES_ACTION);
            PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, reqcode, yesReceive,0);
            Intent noReceive = new Intent(context,ReminderActionReceiver.class);
            noReceive.putExtra("reqcode",reqcode+"");
            noReceive.putExtra("day",day+"");
            noReceive.putExtra("month",month+"");
            noReceive.putExtra("year",year+"");
            noReceive.setAction(AppConstant.NO_ACTION);
            PendingIntent pendingIntentNo = PendingIntent.getBroadcast(context, reqcode, noReceive,0);


            Notification notification = builder.setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.ic_schedule)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_check, "Yes", pendingIntentYes)
                    .addAction(R.drawable.ic_close, "No", pendingIntentNo)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(reqcode, notification);
        }


    }
    public static void clearNotification(int code,Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(code);
        Log.d("NotificationClear:"," Done");
    }
}
