package com.henryhilton.internationalstudentapp;

/**
 * Created by henryhilton on 12/10/17.
 */

public class BuddyData {

    String NameYear;
    String Activities;
    String Major;
    Boolean Chat;

    public BuddyData(){
    }

    public String getActivities() {
        return Activities;
    }

    public String getMajor() {
        return Major;
    }

    public String getNameYear() {
        return NameYear;
    }

    public void setNameYear(String nameYear) {
        NameYear = nameYear;
    }

    public void setActivities(String activities) {
        Activities = activities;
    }

    public void setMajor(String major){
        Major = major;
    }

    public Boolean getChat() {
        return Chat;
    }

    public void setChat(Boolean chat) {
        Chat = chat;
    }
}
