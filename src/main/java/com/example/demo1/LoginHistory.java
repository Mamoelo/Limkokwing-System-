package com.example.demo1;

public class LoginHistory {
    private String username;
    private String loginDate;

    public LoginHistory(String username, String loginDate) {
        this.username = username;
        this.loginDate = loginDate;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginDate() {
        return loginDate;
    }
}
