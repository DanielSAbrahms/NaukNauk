package com.example.irish.nauknaukfinalproject;

import java.io.Serializable;

/**
 * Created by Jason on 11/30/2017.
 */

public class Professor extends GUAffiliate implements Serializable{
    public String department;
    public String officeLocation;
    public String phoneNumber;
    public boolean isAvailable;



    public Professor() {
        super();
        this.isAvailable = true;
        this.department = this.phoneNumber = this.officeLocation = null;
    }
    public Professor(String firstName, String lastName, String email, String password, String department, boolean isAvailable) {
        super(firstName, lastName, email, password);
        this.department = department;
        this.isAvailable = isAvailable;
    }

    public Professor(String firstName, String lastName, String email, String password, String department) {
        super(firstName, lastName, email, password);
        this.department = department;
        this.officeLocation = null;
        this.phoneNumber = null;
        this.isAvailable = false;
    }

    public Professor(String firstName, String lastName, String email, String password, String department, String officeLocation, String phoneNumber) {
        super(firstName, lastName, email, password);
        this.department = department;
        this.officeLocation = officeLocation;
        this.phoneNumber = phoneNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getDepartment() {
        return department;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return (this.isAvailable() ? "T" : "F") + " " + firstName + " " + lastName + " : " + department;
    }
}
