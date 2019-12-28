package com.zuccessful.trueharmony.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.zuccessful.trueharmony.services.AlarmService;
import com.zuccessful.trueharmony.services.PlayRingtone;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //check the alarm notification preference for the user
        String alarmPref = Utilities.getDataFromSharedpref(context.getApplicationContext(), Constants.KEY_ALARM_PREF);
        if(alarmPref.equals("0")) {
            Intent startIntent = new Intent(context.getApplicationContext(), PlayRingtone.class);
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            startIntent.putExtra("ringtone-uri", alarmUri.toString());
            context.getApplicationContext().startService(startIntent);
        }

        int alarm_id = intent.getIntExtra("alarm_id", 0);
        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        AlarmService.enqueueWork(context, alarm_id, intent.setComponent(comp));

        setResultCode(Activity.RESULT_OK);
    }
}
