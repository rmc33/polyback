package com.rmc33.polybook.polyback.models;

public class SessionRequest {

    private String userId;
    private String provider;
    private String idToken;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getIdToken() {
        return idToken;
    }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public SessionRequest() {}
}
