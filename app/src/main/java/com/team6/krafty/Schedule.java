package com.team6.krafty;

import java.util.ArrayList;

public class Schedule {
    static ArrayList<Integer> ids;
    static ArrayList<Schedulable> schedule;
    static Schedule instance;

    private Schedule() {
        ids = new ArrayList<>();
        schedule = new ArrayList<>();
       // Task task = new Task(2, 3, 3,"March 31, 2019", "7:00 PM");
      //  Task task3 = new Task(5, 3, 9,"April 1, 2019", "7:00 PM");
       // Task task2 = new Task(3, 3, 4,"March 31, 2019", "7:00 PM");
       // Event event = new Event( "", "Test Event", "March 31, 2019", "March 31, 2019", "6:00 PM", "7:00 PM", 49, "aksdjfksafjd", "askdjf", "ak", "48573", 94.55, 95.00, "alsdkjflkdsjf", true, true, true, true, true);
       // event.setID(34);
       // Event event2 = new Event( "", "Test Event2", "April 5, 2019", "April 5, 2019", "6:00 PM", "7:00 PM", 49, "aksdjfksafjd", "askdjf", "ak", "48573", 94.55, 95.00, "alsdkjflkdsjf", true, true, true, true, true);
       // event2.setID(232324);
       // schedule.add(task);
       // schedule.add(task3);
        //schedule.add(task2);
        //schedule.add(event);
       // schedule.add(event2);
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

}
