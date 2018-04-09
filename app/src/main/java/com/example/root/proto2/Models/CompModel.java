package com.example.root.proto2.Models;

import java.io.Serializable;

/**
 * Created by root on 10/15/17.
 */

public class CompModel implements Serializable {

    private String place="lonawala";
    private String description="ndwirnviernvenmweuivnwmuivnwrivbninvwirnvwi";
    private String date="28 nov 2017";
    private String time="10:00 pm";
    private String state="Maharashtra";
    private String city="pune";
    private String landmark="dhdyjhfh";
    private String category="maintenance";


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
