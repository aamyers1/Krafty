package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class ScheduleController {
    private DBManager dbManager = DBManager.getInstance();
    private boolean isScheduled;

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

    public boolean createTask(final Task task, Context context){
        final String token = SessionManager.getToken(context);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.createTask(task, token);
                    isScheduled = true;
                }
                catch(KraftyRuntimeException e){
                    Log.d("SCHEDULE EVENT ERROR", e.getMessage());
                    isScheduled = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            if (isScheduled){
                Toast.makeText(context, "Schedule Success!", Toast.LENGTH_SHORT).show();
            }else {
                Log.d("CREATE TASK ERROR", "task error");
                Toast.makeText(context, "Schedule Failure!", Toast.LENGTH_SHORT).show();
            }
            return isScheduled;
        }
        catch(Exception e){ ;
            Log.d("CREATETASK", e.getMessage());
        }
        return false;
    }
}
