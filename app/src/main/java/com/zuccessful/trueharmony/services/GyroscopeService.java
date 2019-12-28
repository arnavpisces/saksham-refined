package com.zuccessful.trueharmony.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class GyroscopeService extends Service  implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mGyro;

    private FirebaseFirestore db;
    private SimpleDateFormat sdf;
    private SakshamApp app;
    Patient patient;
    final static String GYROTAG = "GYRODATA";
    File myFile;

    public GyroscopeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE);
        app = SakshamApp.getInstance();
        patient = app.getAppUser(null);
        db = app.getFirebaseDatabaseInstance();


        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        myFile = new File(path+"/Gyroscope.csv");
        try{
            myFile.createNewFile();
        }catch(Exception e){
            Log.e("FileTag","File creation:"+e);
        }


//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        Log.d("Service Started","Service Started");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyro, 50000,50000);


        return START_STICKY;

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        Map<String, Object> GyroVal = new HashMap<>();
        GyroVal.put("GyroX", x);
        GyroVal.put("GyroY", y);
        GyroVal.put("GyroZ", z);

//        db.collection("PatientSensorData/"+patient.getId()+"/Gyroscope").document(date)
//                .set(GyroVal)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(GYROTAG, "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(GYROTAG, "Error writing document", e);
//                    }
//                });

        String GyroData = date+GyroVal.toString();


        try {

            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append(GyroData);
            myOutWriter.close();
            fOut.close();
//            System.out.println("Gyroscope sensor data:"+date+":" + x+ "," +y+","+ z);
        } catch (FileNotFoundException e) {
            System.out.println("exception");
            e.printStackTrace();
        }
        catch (IOException e) {e.printStackTrace();}


//        System.out.println("Gyroscope sensor data:"+date+":" + x+ "," +y+","+ z);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
