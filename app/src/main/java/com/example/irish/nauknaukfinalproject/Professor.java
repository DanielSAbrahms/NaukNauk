package com.example.irish.nauknaukfinalproject;

/**
 * Created by Jason on 11/30/2017.
 */

public class Professor extends GUAffiliate {
    public String department;
    public boolean isAvailable;



    public Professor() {
        super();
    }
    public Professor(String firstName, String lastName, String email, String password, String department, boolean isAvailable) {
        super(firstName, lastName, email, password);
        this.department = department;
        this.isAvailable = isAvailable;
    }

    public Professor(String firstName, String lastName, String email, String password, String department) {
        super(firstName, lastName, email, password);
        this.department = department;
        this.isAvailable = false;
    }

    public String getDepartment() {
        return department;
    }
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return (this.isAvailable() ? "T" : "F") + " " + firstName + " " + lastName + " : " + department;
    }
}
