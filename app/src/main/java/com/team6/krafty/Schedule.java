package com.team6.krafty;

import java.util.ArrayList;

public class Schedule {


    static ArrayList<Task> tasks;
    static ArrayList<Integer> ids;
    static ArrayList<ScheduleItem> schedule;
    static Schedule instance;

    private Schedule() {
        tasks = new ArrayList<>();
        ids = new ArrayList<>();
        schedule = new ArrayList<>();
    }

    public static Schedule getInstance() {
        if (instance == null){
            synchronized (DBAccessImpl.class){
                if (instance == null) instance = new Schedule();
            }
        }
        return instance;
    }

}
