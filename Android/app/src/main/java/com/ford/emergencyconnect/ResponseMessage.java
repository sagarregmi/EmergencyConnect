package com.ford.emergencyconnect;

import java.util.Date;

/**
 * Created by OCARPEN4 on 3/16/2016.
 */
public class ResponseMessage {

    public String name;
    public int age;
    public String skills;
    public String phoneNumber;
    public String distressKey;

    public int ETA;
    public Date timestamp;

    ResponseMessage(){}

    ResponseMessage(String name, int age, String skills, String phoneNumber, int ETA, String distressKey){
        this.name = name;
        this.age = age;
        this.skills = skills;
        this.phoneNumber = phoneNumber;
        this.ETA = ETA;
        this.distressKey = distressKey;
        this.timestamp = new Date();
    }

    public String getSkills() {
        return skills;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDistressKey() {
        return distressKey;
    }

    public int getETA() {
        return ETA;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
