package com.zuccessful.trueharmony.pojo;

import java.util.Map;

public class LabInvestigation {
    private String id;
    private String name;
    private String unit;
    private Map<String, Double> range;

    public LabInvestigation() {
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Map<String, Double> getRange() {
        return range;
    }

    public void setRange(Map<String, Double> range) {
        this.range = range;
    }
}
