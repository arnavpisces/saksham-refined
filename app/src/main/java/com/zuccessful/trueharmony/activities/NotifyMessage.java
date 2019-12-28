package com.zuccessful.trueharmony.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotifyMessage extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView txt=new TextView(this);

        txt.setText("This is the message!");
        setContentView(txt);
    }
}