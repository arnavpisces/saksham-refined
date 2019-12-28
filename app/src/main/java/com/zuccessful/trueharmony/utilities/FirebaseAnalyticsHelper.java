package com.zuccessful.trueharmony.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.zuccessful.trueharmony.application.SakshamApp;

import java.util.Map;

public class FirebaseAnalyticsHelper {

    public interface FirebaseAnalyConst {
        String OPEN_ACTIVITY = "OPEN_ACTIVITY";
        String ACTIVITY_NAME = "ACTIVITY_NAME";
        String QUESTION_CLICKED = "QUESTION_CLICKED";
        String QUESTION_NUMBER = "QUESTION_NUMBER";


    }

    public static void sendMapAnalytics(String key, String value){
        Bundle params = new Bundle();
        params.putString(key, value);
        SakshamApp.getInstance().getFirebaseAnalyticsObj().logEvent(FirebaseAnalyConst.OPEN_ACTIVITY,
                params);

    }
    public static void sendMapAnalytics(Map<String,String> valueMap, String eventName){
        Bundle params = new Bundle();
        for (String key : valueMap.keySet())
        {
            String value = valueMap.get(key);
            params.putString(key,value);
        }
        SakshamApp.getInstance().getFirebaseAnalyticsObj().logEvent(eventName,
                params);

    }

    public static void trackScreen(Context context,String screenName){
        SakshamApp.getInstance().getFirebaseAnalyticsObj().setCurrentScreen((Activity) context, screenName, null);

    }
}
