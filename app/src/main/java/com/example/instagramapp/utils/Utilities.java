package com.example.instagramapp.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utilities {
    public static String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
