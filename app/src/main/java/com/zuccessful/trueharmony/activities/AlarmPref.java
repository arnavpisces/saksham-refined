package com.zuccessful.trueharmony.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zuccessful.trueharmony.R;

import java.io.File;
import java.io.IOException;

public class AlarmPref extends AppCompatActivity {

    private Button songButton;
    private Button alarmButton;
    private Button pledgeButton;
    private Button msgButton;
    private Uri ringtone = null;

    public static final int RECORD_AUDIO = 0;


    private static final String LOG_TAG = "Audio";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {android.Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_pref);
        songButton = (Button) findViewById(R.id.songButton);
        alarmButton = (Button) findViewById(R.id.alarmButton);
        pledgeButton = (Button) findViewById(R.id.pledgeButton);
        msgButton = (Button) findViewById(R.id.msgButton);




        pledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), RecordPledge.class);
                startActivityForResult(myIntent, 0);
            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone);
                startActivityForResult(intent , 1);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    SharedPreferences pref = getSharedPreferences("alarmAudio",MODE_PRIVATE);
                    pref.edit().putString("URI", ringtone.toString()).commit();
                    break;

                default:
                    break;
            }
        }
    }

    public void recordAudio()
    {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(myDir.getPath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed e " +e);
        }

        mRecorder.start();
    }




}
