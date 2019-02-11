package com.henryhilton.internationalstudentapp;

/**
 * Created by henryhilton on 12/11/17.
 */

public class ProfileData {

    String email;
    String fullName;
    String country;

    public ProfileData(){

    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
