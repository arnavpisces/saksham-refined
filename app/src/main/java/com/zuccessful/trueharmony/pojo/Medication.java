package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Medication implements Serializable {
    private String id;
    private ArrayList<Integer> alarmIds;
    private String name;
    private String description;
    private Map<String, Boolean> days;
    private ArrayList<String> reminders;

    private String day;
    private String month;
    private String year;
    private String repeated;
    private Boolean taken;

    private long timeStamp;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    public Medication() {
    }

    public Medication(String name, String description, Map<String, Boolean> days, ArrayList<String> reminders) {
        this.alarmIds = new ArrayList<>();
        this.name = name;
        this.description = description;
        this.days = days;
        this.reminders = reminders;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Boolean> getDays() {
        return days;
    }

    public void setDays(Map<String, Boolean> days) {
        this.days = days;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRepeated() {
        return repeated;
    }

    public void setRepeated(String repeated) {
        this.repeated = repeated;
    }
}
