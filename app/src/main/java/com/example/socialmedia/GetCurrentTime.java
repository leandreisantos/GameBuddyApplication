package com.example.socialmedia;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetCurrentTime {

    public String ctime(){
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());


        String time = savedate + ":" + savetime;

        return time;
    }
}
