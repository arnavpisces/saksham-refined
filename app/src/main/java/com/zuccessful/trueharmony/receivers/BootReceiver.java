package com.zuccessful.trueharmony.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zuccessful.trueharmony.services.AccelerometerSensorService;

public class BootReceiver extends BroadcastReceiver {
    static final String ACC_TAG = "Accelerometer";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceLauncher = new Intent(context, AccelerometerSensorService.class);
        context.startService(serviceLauncher);
        Log.e(ACC_TAG, "BootReceiver started AccelerometerService");
    }
}