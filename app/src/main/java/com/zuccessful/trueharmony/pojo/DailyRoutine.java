package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Admin on 06-08-2018.
 */

public class DailyRoutine implements Serializable {
    private String id;
    private ArrayList<Integer> alarmIds;
    private String name;
    private ArrayList<String> reminders;

    public boolean getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(boolean alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    private boolean alarmStatus;

    public interface DailyRoutineConstants {
        int bath = 1;
        int walk = 2;
        int wake = 3;
        int sleep = 4;
        int meal = 5;
        int brush = 6;
    }

    public DailyRoutine() {
    }

    public DailyRoutine(String name, ArrayList<String> reminders, String id) {
        this.alarmIds = new ArrayList<>();
        this.name = name;
        this.reminders = reminders;
        this.id = String.valueOf(id);
    }

    public DailyRoutine(ArrayList<String> reminders, ArrayList<Integer> alarmIds) {
        this.reminders = reminders;
        this.alarmIds = alarmIds;
    }

    public DailyRoutine(String name, int id) {
        this.alarmIds = new ArrayList<>();
        this.name = name;
        this.id = String.valueOf(id);
        this.reminders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<Integer> getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(ArrayList<Integer> alarmIds) {
        this.alarmIds = alarmIds;
    }

    public void addAlarmId(int alarm_id) {
        if (!this.alarmIds.contains(alarm_id))
            this.alarmIds.add(alarm_id);
    }
}