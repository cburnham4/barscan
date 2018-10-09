package com.barscan.firebaseidscanner;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ScannedLicense implements Parcelable{
    private String uuid;
    private String firstName;
    private String lastName;
    private int age;
    private String dob;
    private String gender;
    private String address;
    private String scannedDateTime;

    //Required for firebase UI
    public ScannedLicense() {
        // Used for firebase instantiation
    }

    public ScannedLicense(String firstName, String lastName, int age, String dob, String gender, String address, String scannedDateTime) {
        this.uuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.scannedDateTime = scannedDateTime;

    }

    ScannedLicense(FirebaseVisionBarcode.DriverLicense driverLicense) {
        this.uuid = UUID.randomUUID().toString();
        this.firstName = driverLicense.getFirstName();
        this.lastName = driverLicense.getLastName();
        this.dob = driverLicense.getBirthDate();
        this.age = DateHelper.getAge(dob);
        this.gender = driverLicense.getGender().equals("1") ? "male" : "female";
        this.address = driverLicense.getAddressZip();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        this.scannedDateTime = dateFormat.format(new Date());
    }

    ScannedLicense(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        age = in.readInt();
        dob = in.readString();
        gender = in.readString();
        address = in.readString();
        scannedDateTime = in.readString();
        uuid = in.readString();
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
        parcel.writeString(uuid);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getUserInfo() {
        String info = "Name: " + firstName + " " +lastName + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender +  "";
        return info;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScannedDateTime() {
        return scannedDateTime;
    }

    public void setScannedDateTime(String scannedDateTime) {
        this.scannedDateTime = scannedDateTime;
    }
}
