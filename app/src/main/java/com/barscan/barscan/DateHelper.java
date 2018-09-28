package com.barscan.barscan;

import android.icu.util.GregorianCalendar;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateHelper {

    public static String getRandomTime() {
        SimpleDateFormat dfDateTime  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

        int hour = randBetween(9, 22); //Hours will be displayed in between 9 to 22
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);


        GregorianCalendar gc = new GregorianCalendar(2018, 12, 1);
        gc.set(2018, 12, 20, hour, min,sec);

        return dfDateTime.format(gc.getTime());
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

}
