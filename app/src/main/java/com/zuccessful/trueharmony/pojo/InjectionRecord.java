package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;

public class InjectionRecord implements Serializable {
    private String name;
    private String timeStamp;
    private String status;
    private String repeated;

    public InjectionRecord() {

    }

    public InjectionRecord(String name, String timeStamp, String status, String repeated) {
        this.name = name;
        this.timeStamp = timeStamp;
        this.status = status;
        this.repeated = repeated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepeated() {
        return repeated;
    }

    public void setRepeated(String repeated) {
        this.repeated = repeated;
    }
}
