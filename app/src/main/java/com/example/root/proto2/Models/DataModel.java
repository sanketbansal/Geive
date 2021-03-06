package com.example.root.proto2.Models;

import android.location.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 10/15/17.
 */

public class DataModel implements Serializable {
    public String username="Sanket bansal";
    public String userid="sankybansal59@yahoo.in";
    public int age=22;
    public String phone="Phone no";
    public String phoneVerification="Not Verified";
    public String addresshome="cmeifvnievneivmeivumomeocmeocmefvevoueve";
    public String addresswork="mcejcwjxmwomweociw,ocipwmcowimcowmcwoicw";
    public List<CompModel> complaint= new ArrayList<CompModel>();
    public List<TimeModel> timeline=new ArrayList<TimeModel>();

    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }

    public String getPhone() {
        return phone;
    }

    public List<CompModel> getComplaint() {
        return complaint;
    }

    public List<TimeModel> getTimeline() {
        return timeline;
    }

    public String getaddresshome() {
        return addresshome;
    }

    public String getaddresswork() {
        return addresswork;
    }

    public void setaddresshome(String address_home) {
        addresshome = address_home;
    }

    public void setaddresswork(String address_work) {
        addresswork = address_work;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setComplaint(List<CompModel> complaint) {
        this.complaint = complaint;
    }

    public void setTimeline(List<TimeModel> timeline) {
        this.timeline = timeline;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneVerification() {
        return phoneVerification;
    }

    public void setPhoneVerification(String phoneVerification) {
        this.phoneVerification = phoneVerification;
    }

    public int getAge() {
        return age;
    }

}
