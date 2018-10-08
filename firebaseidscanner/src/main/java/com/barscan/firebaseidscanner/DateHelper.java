package com.barscan.firebaseidscanner;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {

    public static String getRandomTime() {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

        int hour = randBetween(9, 22); //Hours will be displayed in between 9 to 22
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);


        GregorianCalendar gc = new GregorianCalendar(2018, 12, 1);
        gc.set(2018, 12, 20, hour, min, sec);

        return dfDateTime.format(gc.getTime());
    }

    public static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }


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
