package com.example.qrattendance;

// Student.java

public class Student {
    private String name;
    private String rollNo;
    private String parentMobile;

    public Student() {
        // Default constructor required for Firebase
    }

    public Student(String name, String rollNo, String parentMobile) {
        this.name = name;
        this.rollNo = rollNo;
        this.parentMobile = parentMobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getParentMobile() {
        return parentMobile;
    }

    public void setParentMobile(String parentMobile) {
        this.parentMobile = parentMobile;
    }
}
