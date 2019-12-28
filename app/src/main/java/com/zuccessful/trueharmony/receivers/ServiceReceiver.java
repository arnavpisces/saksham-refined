package com.zuccessful.trueharmony.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.zuccessful.trueharmony.services.AccelerometerSensorService;
import com.zuccessful.trueharmony.services.CallService;
import com.zuccessful.trueharmony.services.GyroscopeService;
import com.zuccessful.trueharmony.services.SMSService;

public class ServiceReceiver extends BroadcastReceiver {

    Intent acc_data;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ServiceReceiver","intent received");
        if (intent.getAction().equalsIgnoreCase("STOP_TEST_SERVICE")) {
            if (isMyServiceRunning(context, AccelerometerSensorService.class)) {
                Log.d("AccelerometerService","AccelerometerSensorService is running!! Stopping...");
                context.stopService(new Intent(context, AccelerometerSensorService.class));
            } else {
                //Toast.makeText(context, "Service not running", Toast.LENGTH_LONG).show();
            }
            if (isMyServiceRunning(context, GyroscopeService.class)) {
                Log.d("GyroscopeService","GyroscopeService is running!! Stopping...");
                context.stopService(new Intent(context, GyroscopeService.class));
            } else {
                //Toast.makeText(context, "Service not running", Toast.LENGTH_LONG).show();
            }
            if (isMyServiceRunning(context, CallService.class)) {
                Log.d("CallService","CallService is running!! Stopping...");
                context.stopService(new Intent(context, CallService.class));
            } else {
                //Toast.makeText(context, "Service not running", Toast.LENGTH_LONG).show();
            }
            if (isMyServiceRunning(context, SMSService.class)) {
                Log.d("SMSService","SMS Service is running!! Stopping...");
                context.stopService(new Intent(context, SMSService.class));
            } else {
                //Toast.makeText(context, "Service not running", Toast.LENGTH_LONG).show();
            }
        }
        if(intent.getAction().equalsIgnoreCase("START_TEST_SERVICE")){

            Intent intent1 = new Intent(context, AccelerometerSensorService.class);
            context.startService(intent1);
            Log.d("AccelerometerService","AccelerometerSensorService is started");
            Intent intent2 = new Intent(context, GyroscopeService.class);
            context.startService(intent2);
            Log.d("GyroscopeService","GyroscopeService is started");
            Intent intent3 = new Intent(context, CallService.class);
            context.startService(intent3);
            Log.d("CallService","CallService is started");
            Intent intent4 = new Intent(context, SMSService.class);
            context.startService(intent4);
            Log.d("SMSService","SMSService is started");
        }


    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
