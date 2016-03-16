package com.ford.emergencyconnect;

import java.util.Date;

/**
 * Created by OCARPEN4 on 3/16/2016.
 */
public class DistressMessage {

    public double lat;
    public double lng;

    public String name;
    public int age;
    public String preConditions;
    public String phoneNumber;

    public int numPassengers;
    public Date timestamp;

    DistressMessage(){}

    DistressMessage(double lat, double lng, String name, int age, String preConditions, String phoneNumber, int numPassengers){
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.age = age;
        this.preConditions = preConditions;
        this.phoneNumber = phoneNumber;
        this.numPassengers = numPassengers;
        this.timestamp = new Date();
    }
}
