package com.ford.emergencyconnect;

public class User {

    public String email;
    public String password;
    public String name;
    public String phone;
    public int age;
    public String skills;
    public String preConditions;
    public String mode;

    User() {}

    User(String email, String password, String name, String phone, int age, String skills, String preConditions, String mode){
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.skills = skills;
        this.preConditions = preConditions;
        this.mode = mode;
    }
}
