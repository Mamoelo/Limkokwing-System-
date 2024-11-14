package com.example.demo1;

public class LecturerReport {
    private String week;
    private String lecturer;
    private String report;

    public LecturerReport(String week, String lecturer, String report) {
        this.week = week;
        this.lecturer = lecturer;
        this.report = report;
    }

    public String getWeek() {
        return week;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getReport() {
        return report;
    }
}
