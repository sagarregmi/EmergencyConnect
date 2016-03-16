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

    public int ETA;
    public Date timestamp;

    ResponseMessage(){}

    ResponseMessage(String name, int age, String skills, String phoneNumber, int ETA){
        this.name = name;
        this.age = age;
        this.skills = skills;
        this.phoneNumber = phoneNumber;
        this.ETA = ETA;
        this.timestamp = new Date();
    }
}
