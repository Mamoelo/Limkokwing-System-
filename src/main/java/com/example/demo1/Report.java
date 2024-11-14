package com.example.demo1;

public class Report {
    private String submissionDate;
    private String lecturer;
    private String report;

    public Report(String submissionDate, String lecturer, String report) {
        this.submissionDate = submissionDate;
        this.lecturer = lecturer;
        this.report = report;
    }

    // Getters and Setters
    public String getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(String submissionDate) { this.submissionDate = submissionDate; }

    public String getLecturer() { return lecturer; }
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }

    public String getReport() { return report; }
    public void setReport(String report) { this.report = report; }
}
