package com.ford.emergencyconnect;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeInt(age);
        dest.writeString(skills);
        dest.writeString(preConditions);
        dest.writeString(mode);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    //De-Parcel
    public User(Parcel in){
        email = in.readString();
        name = in.readString();
        phone = in.readString();
        password = in.readString();
        age = in.readInt();
        skills = in.readString();
        preConditions = in.readString();
        mode = in.readString();
    }
}
