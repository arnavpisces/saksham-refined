package com.zuccessful.trueharmony.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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

public class ReminderActionReceiver extends BroadcastReceiver {
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if(AppConstant.NO_ACTION.equals(action)) {
            //Actually pressed YES, Do the YES stuff here
            //No idea what is happening
            onYes(context,intent);
        }

    }
    private void onYes(Context context, Intent intent)
    {
        Bundle args = intent.getBundleExtra("DATA");
        Injection injection = (Injection) args.getSerializable("INJECTION");
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(injection.getReqCode()));
        injection.setStatus("Taken");

        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
        Map<String, Object> today_date = new HashMap<>();
        today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
        db.collection("patient_injr_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());

        // create  injection record
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        InjectionRecord injectionRecord = new InjectionRecord(injection.getName(),timeStamp,"Taken",injection.getRepeated());

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
        Toast.makeText(context, "Well done", Toast.LENGTH_SHORT).show();
    }
}
