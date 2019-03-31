package com.team6.krafty;

public class ScheduleItem {

    private int id;
    private int type;
    private String date;
    String time;
    String name;

    public ScheduleItem(int id, int type, String date, String time, String name){
        this.id = id;
        this.type = type;
        this.date = date;
        this.time = time;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public int getType(){
        return type;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getName(){
        return name;
    }
}
