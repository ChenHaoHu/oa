package com.example.activiti.entity;

import java.io.Serializable;

public class LeaveBillBean implements Serializable {
    private String person;
    private String time;
    private String reason;

    public LeaveBillBean(String person, String time, String reason) {
        this.person = person;
        this.time = time;
        this.reason = reason;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
