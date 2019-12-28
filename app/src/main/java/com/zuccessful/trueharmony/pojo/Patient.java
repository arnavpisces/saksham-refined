package com.zuccessful.trueharmony.pojo;

public class Patient {
    private String id;
    private String name;
    private String uid;
    private String caregiverId;


    public Patient() {
    }

    public Patient(String id, String name, String uid, String caregiverId) {
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.caregiverId = caregiverId;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(String caregiverId) {
        this.caregiverId = caregiverId;
    }
}
