package com.example.irish.nauknaukfinalproject;

import java.io.Serializable;

/**
 * GUAffiliate class. This class is the superclass for Student and Professor, being that both have in common
 * a firstName, lastName, email, and password. All fields have appropriate getters and setters. Other
 * implemented methods include DVC, EVC, and overridden toString() method.
 *
 * Sources:
 *      None
 *
 * Version: 1.1
 * Author: Jason Conci
 */

public class GUAffiliate implements Serializable{
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    // EVC, takes all fields as String parameters
    public GUAffiliate(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // DVC, sets all fields equal to String "NULL"
    public GUAffiliate() {
        this.firstName = this.lastName = this.email = this.password = "NULL";
    }

    // BLOCK OF GETTERS AND SETTERS

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
