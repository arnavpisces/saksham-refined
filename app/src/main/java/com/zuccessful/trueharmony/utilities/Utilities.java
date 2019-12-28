package com.zuccessful.trueharmony.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class Utilities {


    private static final String MY_PREFS_NAME = "Saksham_Pref";
//    public static final String KEY_HEIGHT = "height";
//    public static final String KEY_WEIGHT = "weight";
//    public static final String KEY_NAME = "name";
//    public static final String KEY_ALARM_PREF = "alarmPref";
//    public static final String KEY_LANGUAGE_PREF = "langPref";
//    public static final String KEY_PHY_ACT_LIST = "physicalActList";
//    public static final String KEY_LEISURE_ACT_LIST = "leisureActList";
//    public static final String KEY_MIDICINES_LIST = "medicinesList";
//    public static final String KEY_BREAKFAST = "breakfast";
//    public static final String KEY_LUNCH = "lunch";
//    public static final String KEY_DINNER = "dinner";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public static void changeLanguage(Context context){
        String langPrefType = Utilities.getDataFromSharedpref(context,Constants.KEY_LANGUAGE_PREF);
        String languageToLoad;
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            if(lang==1) {
                languageToLoad = "hi"; // your language
            }else{
                languageToLoad = "en";
            }

        }else{
            languageToLoad = "hi"; // default language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        updateResources(context,languageToLoad);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

    }



    public static void saveListToSharedPref(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getListFromSharedPref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if(gson.fromJson(json, type)!=null) {
            return gson.fromJson(json, type);
        }else{
            return new ArrayList<>();
        }
    }

    public static void saveMedicineToList(Medication medication){
        ArrayList<Medication> medicationArrayList;
        medicationArrayList = getListOfMedication();
        if(medicationArrayList == null) medicationArrayList = new ArrayList<>();
        medicationArrayList.add(medication);
        saveListOfMedicine(medicationArrayList);
    }

    public static void saveListOfMedicine(ArrayList<Medication> medicationArrayList){
        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());

        Gson gson = new Gson();
        String json = gson.toJson(medicationArrayList);

        editor = shref.edit();
        editor.remove(Constants.KEY_MIDICINES_LIST).commit();
        editor.putString(Constants.KEY_MIDICINES_LIST, json);
        editor.commit();
    }

    public static ArrayList<Medication> getListOfMedication(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MIDICINES_LIST , "");
        ArrayList<Medication> medicationArrayList = gson.fromJson(response,
                new TypeToken<List<Medication>>(){}.getType());
        return medicationArrayList;
    }

    public static void removeListFromSharedPref(String key,String value){
        ArrayList<String> actList = getListFromSharedPref(key);
        if(actList.contains(value)){
            actList.remove(value);
            saveListToSharedPref(actList,key);
        }
    }



    public static void saveDataInSharedpref(Context context,String key,String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDataFromSharedpref(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public static void saveBooleanDataInSharedpref(Context context, String key, boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanDataFromSharedpref(Context context, String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    return prefs.getBoolean(key, true);
    }

    public static void saveArrayListTimers(ArrayList<String> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("timerlist", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getArrayListTimers(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = prefs.getString("timerlist", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void addToTimersList(String notID){
        ArrayList<String> timerList = getArrayListTimers();
        if(timerList==null) timerList = new ArrayList<>();
        timerList.add(notID);
        saveArrayListTimers(timerList);
    }

    public static void cancelAlarms(Context context, ArrayList<Integer> alarmIDs){

    }
    public static void removeTimerFromIDList(String id){
        ArrayList<String> timerList = getArrayListTimers();
        if (timerList == null) return;
        timerList.remove(id);
        saveArrayListTimers(timerList);
    }

    public static void removeFromTimersList(int notID){
        ArrayList<String> timerList = getArrayListTimers();
        if(timerList==null) return;
        timerList.remove(String.valueOf(notID));
        saveArrayListTimers(timerList);
    }

    public static void incrementTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int count = preferences.getInt("timerCounter", 0);
        preferences.edit().putInt("timerCounter", count+1).commit();
    }

    public static void resetTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        preferences.edit().putInt("timerCounter", 0).commit();
    }

    public static int getTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        return preferences.getInt("timerCounter", 0);
    }

    public static int getNextAlarmId(Context context) {

        // TODO: fix if the app is reinstalled, sync with servers max value
        SharedPreferences preferences = context.getSharedPreferences("com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int id = preferences.getInt("new_alarm_index", 0);
        setNextAlarmId(context, id + 1);
        return id;
    }

    private static void setNextAlarmId(Context context, int i) {
        SharedPreferences preferences = context.getSharedPreferences("com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        preferences.edit().putInt("new_alarm_index", i).apply();
    }

    public static Intent setExtraForIntent(Intent intent, String key, Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] data = bos.toByteArray();
            intent.putExtra(key, data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return intent;
    }

    public static Object getExtraFromIntent(Intent intent, String key) {
        Object object = null;
        byte[] rawObj = intent.getByteArrayExtra(key);
        if (rawObj != null && rawObj.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(rawObj);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                object = in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return object;
    }

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("MM-dd-yy", Locale.US);
    }

    public static void clearPatientId(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_PID, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static Context onAttach(Context newBase) {
        String langPrefType = Utilities.getDataFromSharedpref(newBase,Constants.KEY_LANGUAGE_PREF);
        String languageToLoad;
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            if(lang==1) {
                languageToLoad = "hi"; // your language
            }else{
                languageToLoad = "en";
            }

        }else{
            languageToLoad = "hi"; // default language
        }

        return updateResource(newBase, languageToLoad);
    }
    public static Context updateResource(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        return updateResourcesLegacy(context, language);
    }

    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static final boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet
//            Toast.makeText(context, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(context, context.getResources().getString(R.string.internet_connectivity), Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }



    /*
        ## Script to lookup alarms in the device
        echo "Please set a search filter"
        read search

        adb shell dumpsys alarm | grep $search | (while read i; do echo $i; _DT=$(echo $i | grep -Eo 'when\s+([0-9]{10})' | tr -d '[[:alpha:][:space:]]'); if [ $_DT ]; then echo -e "\e[31m$(date -d @$_DT)\e[0m"; fi; done;)

     */
}
