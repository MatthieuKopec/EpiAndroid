package com.epiandroid.epitech.epiandroid;

public class SusiesBean {

    private String  mTime;
    private String  mTitle;
    private String  mType;
    private String  mName;
    private String  mRegister;
    private String  mId;
    private String  mCalendarId;

    public String   getTime() {
        return mTime;
    }

    public String   getTitle() {
        return mTitle;
    }

    public String   getType() {
        return mType;
    }

    public String   getName() { return mName; }

    public String   getRegister() { return mRegister; }

    public String   getId() { return mId; }

    public String   getCalendarId() { return mCalendarId; }

    public void     setTime(String start, String end) {
        String result_start = start.substring(start.indexOf(" ") + 1, start.lastIndexOf(":"));
        String result_end = end.substring(start.indexOf(" ") + 1, start.lastIndexOf(":"));
        this.mTime = result_start + " - " + result_end;
    }

    public void     setTitle(String title) {this.mTitle = title;}

    public void     setType(String type) {this.mType = type;}

    public void     setName(String name) {this.mName = name;}

    public void     setRegister(String register) {this.mRegister = register;}

    public void     setId(String id) {this.mId = id;}

    public void     setCalendarId(String calendarId) {this.mCalendarId = calendarId;}
}
