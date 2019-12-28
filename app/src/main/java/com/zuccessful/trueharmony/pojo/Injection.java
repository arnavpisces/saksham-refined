package com.zuccessful.trueharmony.pojo;

import android.content.Context;

import java.io.Serializable;

public class Injection implements Serializable {
    private  String id;
    private  String name;
    //private  String reminderStatus;
    private  String hour;
    private  String min;
    private  String day;
    private  String month;
    private  String year;
    private  String repeated;
    private  String title;
    private  String content;
    private  String status;
    private  String ReqCode;
    private String type;

    //private  final String APP_SHARED_PREFS = "RemindMePref";
   // private SharedPreferences appSharedPrefs;
   // private SharedPreferences.Editor prefsEditor;

    public Injection(Context context) {
        //this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        //this.prefsEditor = appSharedPrefs.edit();
    }

    public Injection() {
    }

   /* public boolean getReminderStatus()
    {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }*/

    // Settings Page Reminder Time (Hour)


    public String getReqCode() {
        return ReqCode;
    }

    public void setReqCode(String reqCode) {
        ReqCode = reqCode;
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

   /* public String getReminderStatus() {
        return reminderStatus;
    }

    public void setReminderStatus(String reminderStatus) {
        this.reminderStatus = reminderStatus;
    }*/

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Injection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", hour='" + hour + '\'' +
                ", min='" + min + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", repeated='" + repeated + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
