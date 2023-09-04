package com.rmc33.polybook.polyback.models;

public class UserDataRequest {
	
    private String sessionNum;
    private String userData;
    public String getSessionNum() {
        return sessionNum;
    }
    public void setSessionNum(String sessionNum) {
        this.sessionNum = sessionNum;
    }
    public String getUserData() {
        return userData;
    }
    public void setUserData(String userData) {
        this.userData = userData;
    }

    public UserDataRequest() {};
}