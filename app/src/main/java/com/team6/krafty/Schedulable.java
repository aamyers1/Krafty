package com.team6.krafty;

public interface Schedulable {
    String getDate();
    String getTime();
    String getTitle();
    //1 for event, 2 for task
    int getType();
    int getID();
}
