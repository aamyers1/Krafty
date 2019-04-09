package com.team6.krafty;

import java.util.ArrayList;

public class Schedule {
    static ArrayList<Integer> ids;
    static ArrayList<Schedulable> schedule;
    static Schedule instance;

    private Schedule() {
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

    public ArrayList<Schedulable> getSchedule(){
        return schedule;
    }

    public void addItem(Schedulable s, int i){
        ids.add(i);
        schedule.add(s);
    }

    public int getSchedIDByEventID(int eventID){
        for(int i = 0; i < schedule.size(); i++){
            if(schedule.get(i).getType() == 1){
                if(schedule.get(i).getID() == eventID){
                    return ids.get((i));
                }
            }
        }
        return 0;
    }

    public void clearSchedule(){
        ids = new ArrayList<>();
        schedule = new ArrayList<>();
    }



}
