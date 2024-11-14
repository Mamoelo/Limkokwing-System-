package com.example.demo1;

class User {
    private String username;
    private String email;
    private String status;

    public User(String username, String email, String status) {
        this.username = username;
        this.email = email;
        this.status = status;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
