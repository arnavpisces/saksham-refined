package com.zuccessful.trueharmony;

import android.widget.Toast;

import com.zuccessful.trueharmony.application.SakshamApp;

public class Toaster {

    public static void showShortMessage(String message){
        Toast.makeText(SakshamApp.getInstance(), message, Toast.LENGTH_SHORT).show();
    }
}
