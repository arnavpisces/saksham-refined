package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;

public class Video implements Serializable {
    private String vName;
    private int vID;
    private String vPathName;

    public String getvPathName() {
        return vPathName;
    }

    public void setvPathName(String vPathName) {
        this.vPathName = vPathName;
    }

    public Video(String vName, int vID, String vPathName){
        this.vName = vName;
        this.vID = vID;
        this.vPathName=vPathName;
    }

    public int getvID() {
        return vID;
    }

    public void setvID(int vID) {
        this.vID = vID;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }
}
