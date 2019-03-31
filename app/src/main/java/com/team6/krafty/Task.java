package com.team6.krafty;

public class Task implements Schedulable {

    String date;
    String time;
    int id;
    int prodId;
    int qty;

    public Task(int id, int prodId, int qty, String date, String time){
        this.date = date;
        this.time = time;
        this.id = id;
        this.prodId = prodId;
        this.qty = qty;
    }
    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getTitle(){
        return "TEST PRODUCT: " + qty;
    }

    public int getType(){
        return 2;
    }

    public int getID(){
        return id;
    }
}
