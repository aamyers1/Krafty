package com.team6.krafty;

import android.content.Context;
import android.util.Log;

public class ScheduleController {

    public static void getSchedule(Context context){
        final String token = SessionManager.getToken(context);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance().getSchedule(token);
            }
        });
        t.start();
        try{
            t.join();
        }
        catch(Exception e){
            Log.d("ERR SCHED CONT", e.getMessage());
        }
    }
}
