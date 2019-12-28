package com.zuccessful.trueharmony.pojo;

import android.util.Log;

import java.util.ArrayList;

//model for firebase collection 'alarms/userID/daily_routine'

public class RoutineActivity {

    private String id;
    private ArrayList<Integer> alarmIds;
    private String name;
    private ArrayList<String> reminders;
    private boolean alarmStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Integer> getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(ArrayList<Integer> alarmIds) {
        this.alarmIds = alarmIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getReminders() {
        return reminders;
    }

    public void setReminders(ArrayList<String> reminders) {
        this.reminders = reminders;
    }

    public boolean getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(boolean alarmOn) {
        alarmStatus = alarmOn;
    }

    public RoutineActivity(String id, ArrayList<Integer> alarmIds, String name, ArrayList<String> reminders, boolean alarmStatus) {
        this.id = id;
        this.alarmIds = alarmIds;
        this.name = name;
        this.reminders = reminders;
        this.alarmStatus = alarmStatus;
    }

    public RoutineActivity(String name){
        this.name = name;
    }

    public RoutineActivity(){

    }

}