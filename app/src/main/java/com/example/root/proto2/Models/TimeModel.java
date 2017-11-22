package com.example.root.proto2.Models;

import java.io.Serializable;

/**
 * Created by root on 10/15/17.
 */

public class TimeModel implements Serializable {

    private String place="lonawala";
    private String description="ndwirnviernvenmweuivnwmuivnwrivbninvwirnvwi";
    private Double lat=8.9;
    private  Double lng=-7.6;
    private String date="28 nov 2017";
    private String time="10:00 pm";
    private String state="Maharashtra";
    private String city="pune";
    private String landmark="dhdyjhfh";
    private String rating =null;
    private String category="maintenance";

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
