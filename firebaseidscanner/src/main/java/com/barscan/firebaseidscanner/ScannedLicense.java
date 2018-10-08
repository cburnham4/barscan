package com.barscan.firebaseidscanner;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public ScannedLicense() {

    }
    public String getScannedDateTime() {
        return scannedDateTime;
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

    public ScannedLicense(FirebaseVisionBarcode.DriverLicense driverLicense) {
        this.uuid = UUID.randomUUID().toString();
        this.firstName = driverLicense.getFirstName();
        this.lastName = driverLicense.getLastName();
        this.dob = driverLicense.getBirthDate();
        this.age = DateHelper.getAge(dob);
        this.gender = driverLicense.getGender().equals("1") ? "male" : "female";
        this.address = driverLicense.getAddressZip();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.scannedDateTime = dtf.format(now);
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getId() {
        return uuid.toString();
    }

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
        parcel.writeString(uuid.toString());
    }
    public String getUserInfo() {
        String info = "Name: " + firstName + " " +lastName + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender +  "";
        return info;
    }
}
