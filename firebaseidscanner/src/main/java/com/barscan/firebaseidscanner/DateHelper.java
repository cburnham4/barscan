package com.barscan.firebaseidscanner;

import java.util.Calendar;

public class DateHelper {

    public static int getAge(String dob) {
        int year = Integer.valueOf(dob.substring(4));
        int month = Integer.valueOf(dob.substring(0, 2));
        int day = Integer.valueOf(dob.substring(2, 4));

        return getAge(year, month, day);
    }

    public static int getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

}
