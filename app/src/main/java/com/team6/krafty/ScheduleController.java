package com.team6.krafty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class ScheduleController {

    private DBManager dbManager = DBManager.getInstance();
    private boolean isScheduled;
    private boolean isUnscheduled;
    private String message;
    private static ScheduleController instance;


    private ScheduleController() {

    }

    public static ScheduleController getInstance() {
        if (instance == null){
            synchronized (DBAccessImpl.class){
                if (instance == null) instance = new ScheduleController();
            }
        }
        return instance;
    }

    public boolean checkScheduled(int eventID){
        return Schedule.getInstance().isScheduled(eventID);
    }

    public static void getSchedule(Context context){
        Schedule.getInstance().clearSchedule();
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
            Schedule.getInstance().sortSched();
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

    public boolean scheduleForEvent(final Integer eventId, Context context){
        final String token = SessionManager.getToken(context);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isScheduled = true;
                try {
                    DBManager.getInstance().scheduleForEvent(eventId, token);
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
                Log.d("SCHEDULE EVENT ERROR", "event error");
                Toast.makeText(context, "Schedule Failure!", Toast.LENGTH_SHORT).show();
            }
            return isScheduled;
        }
        catch(Exception e){ ;
            Log.d("SCHEDULEEVENTM", e.getMessage());
        }
        return false;
    }
    public boolean unscheduleForEvent(final Integer eventId, Context context, final String type){
        final String token = SessionManager.getToken(context);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isUnscheduled = true;
                try {
                    DBManager.getInstance().unscheduleForEvent(eventId, token, type);
                }
                catch(KraftyRuntimeException e){
                    Log.d("UNSCHEDULE EVENT ERROR", e.getMessage());
                    isUnscheduled = false;
                }
            }
        });
        t.start();
        try{
            t.join();
            if (isUnscheduled){
                Toast.makeText(context, "Unschedule Success!", Toast.LENGTH_SHORT).show();
            }else {
                Log.d("UNSCHEDULE EVENT ERROR", "event error");
                Toast.makeText(context, "Unschedule Failure!", Toast.LENGTH_SHORT).show();
            }
            return isScheduled;
        }
        catch(Exception e){ ;
            Log.d("UNSCHEDULEEVENTM", e.getMessage());
        }
        return false;
    }
}
