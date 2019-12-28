package com.zuccessful.trueharmony.pojo;

public class Activity_Usage {
    private String timeSpent;
    private long time;
    private String activity_name;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public Activity_Usage() {

    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public Activity_Usage(String timeSpent, long time, String activity_name){
        this.timeSpent=timeSpent;
        this.time = time;
        this.activity_name=activity_name;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }
}
