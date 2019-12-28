package com.zuccessful.trueharmony.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class CallService extends Service {


    int PERMISSION_READ_CALLLOG = 1;
    int PERMISSION_READ_SMS = 2;

    private FirebaseFirestore db;
    private SimpleDateFormat sdf;
    private SakshamApp app;
    Patient patient;
    final static String ACCTAG = "ACCDATA";
    File myFile;

    StringBuffer SMS_Messages = new StringBuffer();
    DateFormat outputformat = new SimpleDateFormat("HH:mm");
    String output = null;


    public CallService() {


    }

    public int onStartCommand(Intent intent, int flags, int startId) {


        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        myFile = new File(path+"/Calllog.csv");
        try{
            myFile.createNewFile();
        }catch(Exception e){
            Log.e("FileTag","File creation:"+e);
        }

        getCallDetails();
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }
    private void getCallDetails() {


//        Toast.makeText(getApplicationContext(),"Call Details Recorded",Toast.LENGTH_SHORT).show();
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            Toast.makeText(getApplicationContext(),"no permission",Toast.LENGTH_SHORT).show();
//            //requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSION_READ_CALLLOG);
//        }


//        Toast.makeText(this,"Call Service Started",Toast.LENGTH_SHORT).show();


        checkPermission(this);


        String curr_date = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        //DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);

        // output = outputformat.format(curr_date);
        output = curr_date;
        int h = Integer.parseInt(output.split(":")[0]);
        int m = Integer.parseInt(output.split(":")[1]);

        System.out.println("Time" + h+ "," +m);
        // if ((h==12 || h==24) && (m==00)) {


        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");

        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);

            Date callDayTime = new Date(Long.valueOf(callDate));
            System.out.println("callDate: "+ callDayTime);
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
            //Toast.makeText(getApplicationContext(),"Call Details:"+sb,Toast.LENGTH_SHORT).show();
        }

        System.out.println("Log" + sb);

        try {

            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append(sb);
            myOutWriter.close();
            fOut.close();

        } catch (FileNotFoundException e) {
            System.out.println("exception");
            e.printStackTrace();
        }
        catch (IOException e) {e.printStackTrace();}


        managedCursor.close();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

