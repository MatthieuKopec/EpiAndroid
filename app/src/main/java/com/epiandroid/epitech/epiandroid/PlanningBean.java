package com.epiandroid.epitech.epiandroid;

public class PlanningBean {

    private String  mTime;
    private String  mTitle;
    private String  mModule;
    private String  mRoom;
    private String  mPast;
    private String  mAllowToken;
    private String  mEventRegistered;
    private String  mModuleRegistered;
    private String  mCalendarType;

    private String  mCodeActi;
    private String  mCodeModule;
    private String  mCodeInstance;
    private String  mCodeEvent;
    private String  mScolarYear;

    public String   getTime() {
        return mTime;
    }

    public String   getTitle() {
        return mTitle;
    }

    public String   getModule() {
        return mModule;
    }

    public String   getRoom() { return mRoom; }

    public String   getPast() { return mPast; }

    public String   getAllowToken() { return mAllowToken; }

    public String   getEventRegistered() { return mEventRegistered; }

    public String   getModuleRegistered() { return mModuleRegistered; }

    public String   getCalendarType() { return mCalendarType; }

    public String   getCodeActi() { return mCodeActi; }

    public String   getCodeModule() { return mCodeModule; }

    public String   getCodeInstance() { return mCodeInstance; }

    public String   getCodeEvent() { return mCodeEvent; }

    public String   getScolarYear() { return mScolarYear; }

    public void     setTime(String start, String end) {
        String result_start = start.substring(start.indexOf(" ") + 1, start.lastIndexOf(":"));
        String result_end = end.substring(start.indexOf(" ") + 1, start.lastIndexOf(":"));
        this.mTime = result_start + " - " + result_end;
    }

    public void     setTitle(String title) {this.mTitle = title;}

    public void     setModule(String module) {this.mModule = module;}

    public void     setRoom(String room) {this.mRoom = room.substring(room.lastIndexOf("/") + 1);}

    public void     setPast(String past) {this.mPast = past;}

    public void     setAllowToken(String allow) {this.mAllowToken = allow;}

    public void     setEventRegistered(String event) {this.mEventRegistered = event;}

    public void     setModuleRegistered(String event) {this.mModuleRegistered = event;}

    public void     setCalendarType(String type) { this.mCalendarType = type; }

    public void     setCodeActi(String acti) { this.mCodeActi = acti; }

    public void     setCodeModule(String module) { this.mCodeModule = module; }

    public void     setCodeInstance(String instance) { this.mCodeInstance = instance; }

    public void     setCodeEvent(String event) { this.mCodeEvent = event; }

    public void     setScolarYear(String year) { this.mScolarYear = year; }
}
