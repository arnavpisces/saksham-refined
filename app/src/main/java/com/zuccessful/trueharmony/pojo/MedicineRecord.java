package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;

public class MedicineRecord implements Serializable {
    private String medId;
    private String name;
    private long timestamp;
    private int slot;
    private boolean taken;

    public MedicineRecord() {
    }

    public MedicineRecord(String medId, String name, long timestamp, int slot, boolean taken) {
        this.medId = medId;
        this.name = name;
        this.timestamp = timestamp;
        this.slot = slot;
        this.taken = taken;
    }

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
}
