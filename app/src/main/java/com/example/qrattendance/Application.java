package com.example.qrattendance;// Application.java

public class Application {
    private String rollNo;
    private String name;
    private String date;
    private String subject;
    private String reason;

    public Application() {
        // Default constructor required for Firebase
    }

    public Application(String rollNo, String name, String date, String subject, String reason) {
        this.rollNo = rollNo;
        this.name = name;
        this.date = date;
        this.subject = subject;
        this.reason = reason;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
