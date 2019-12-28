package com.zuccessful.trueharmony.pojo;

import java.util.HashMap;

public class QuestionUsage {
    private String name;
    private HashMap<String,Long> hm;


    public QuestionUsage() {

    }

    public QuestionUsage(String name, HashMap<String,Long>hm){
        this.hm = hm;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Long> getHm() {
        return hm;
    }

    public void setHm(HashMap<String, Long> hm) {
        this.hm = hm;
    }
}
