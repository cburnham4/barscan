package com.barscan.barscan;

import android.os.Parcel;
import android.os.Parcelable;

public class ScannedLicense implements Parcelable{
    private String firstName;
    private String lastName;
    private int age;
    private String dob;
    private String gender;
    private String address;
    private String scannedDateTime;

    public ScannedLicense(String firstName, String lastName, int age, String dob, String gender, String address, String scannedDateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.scannedDateTime = scannedDateTime;
    }

    protected ScannedLicense(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        age = in.readInt();
        dob = in.readString();
        gender = in.readString();
        address = in.readString();
        scannedDateTime = in.readString();
    }

    public static final Creator<ScannedLicense> CREATOR = new Creator<ScannedLicense>() {
        @Override
        public ScannedLicense createFromParcel(Parcel in) {
            return new ScannedLicense(in);
        }

        @Override
        public ScannedLicense[] newArray(int size) {
            return new ScannedLicense[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeInt(age);
        parcel.writeString(dob);
        parcel.writeString(gender);
        parcel.writeString(address);
        parcel.writeString(scannedDateTime);
    }
}
