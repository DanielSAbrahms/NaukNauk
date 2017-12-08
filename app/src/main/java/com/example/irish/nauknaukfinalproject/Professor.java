package com.example.irish.nauknaukfinalproject;

import java.io.Serializable;

/**
 * Professor class. This class is a direct subclass of GuAffiliate, inheriting fields such as firstName,
 * lastName, email, and password, as well as associated getters, setters and constructors. This class
 * also implements professor-specific fields, such as department, officeLocation, officeHorus, phoneNumber,
 * and boolean field isAvailable, representing whether or not the professor is currently available.
 * Class implements various constructors, as well as getters and setters for all fields.
 *
 * Sources:
 *      None
 * Version: 1.6
 * Authors: Jason Conci
 */

public class Professor extends GUAffiliate implements Serializable{
    public String department;
    public String officeLocation;
    public String officeHours;
    public String phoneNumber;
    public boolean isAvailable;



    // Default value constructor, uses GUAffiliate DVC
    public Professor() {
        super();
        this.isAvailable = true;
        this.department = this.phoneNumber = this.officeLocation = this.officeHours = null;
    }

    // BLOCK OF VARIOUS EVC's //

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
        this.officeHours = null;
    }

    public Professor(String firstName, String lastName, String email, String password, String department, String officeLocation, String officeHours, String phoneNumber) {

        super(firstName, lastName, email, password);
        this.department = department;
        this.officeLocation = officeLocation;
        this.officeHours = officeHours;
        this.phoneNumber = phoneNumber;
    }

    public Professor(String firstName, String lastName, String email, String password, String department, String officeLocation, String officeHours, String phoneNumber, boolean isAvailable) {

        super(firstName, lastName, email, password);
        this.department = department;
        this.officeLocation = officeLocation;
        this.officeHours = officeHours;
        this.phoneNumber = phoneNumber;
        this.isAvailable = isAvailable;
    }

    public Professor(String firstName, String lastName, String email, String password, String department, String officeLocation, String phoneNumber) {
        super(firstName, lastName, email, password);
        this.department = department;
        this.officeLocation = officeLocation;
        this.officeHours = null;

        this.phoneNumber = phoneNumber;
    }

    // BLOCK OF GETTERS AND SETTERS FOR FIELDS //


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


    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }


    @Override
    public String toString() {
        return (this.isAvailable() ? "T" : "F") + " " + firstName + " " + lastName + " : " + department;
    }
}
