package com.rmc33.polybook.polyback.models;

public class SessionResponse {
	
    private String sessionNum;
    private String email;

    public String getSessionNum() { return sessionNum; }
    public void setSessionNum(String s) { sessionNum = s; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}