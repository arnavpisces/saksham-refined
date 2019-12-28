package com.zuccessful.trueharmony.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.receivers.AlarmActionReceiver;
import com.zuccessful.trueharmony.utilities.CustomDialoge;
import com.zuccessful.trueharmony.utilities.FancyGifDialogListener;

public class DialogeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialoge);


        String message = "";
        Intent intent = getIntent();
        if(intent!=null && intent.getStringExtra(AlarmActionReceiver.DIALOGE_MESSAGE)!=null){
            message = (intent.getStringExtra(AlarmActionReceiver.DIALOGE_MESSAGE));
        }

        new CustomDialoge.Builder(this)
                .setTitle(message)
                .setMessage(message)
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText(getResources().getString(R.string.ok))
                .setNegativeBtnBackground("#FFA9A7A8")
                .setGifResource(R.drawable.well_done) //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                      finish();
                    }
                })
                .build();

    }



}
