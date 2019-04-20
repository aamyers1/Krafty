package com.team6.krafty;

import android.util.Log;
import java.util.ArrayList;

public class Schedule {
    private static ArrayList<Integer> ids;
    private static ArrayList<Schedulable> schedule;
    private static Schedule instance;

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

    public ArrayList<Integer> getIDS(){
        return ids;
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

    public int getSchedIDPosByTask(Task task){
        for(int i = 0; i < schedule.size(); i++){
            if(schedule.get(i) == task){
                return i;
            }
        }
        return -1;
    }

    public boolean isScheduled(int eventID){
        for(int i =0; i< schedule.size(); i++){
            if(schedule.get(i).getType() == 1){
                if(schedule.get(i).getID() == eventID){
                    return true;
                }
            }
        }
        return false;
    }

    public void sortSched(){
        int minID = 0;
        int minTime;
        int minDate;
        ArrayList<Schedulable> updatedSchedule = new ArrayList<>();
        ArrayList<Integer> updatedIDs = new ArrayList<>();
        while(schedule.size() != 0){
            minTime = convertTime(schedule.get(0));
            minDate = convertDate(schedule.get(0));
            minID = 0;
            for(int j = 0; j < schedule.size(); j ++){
                if(convertDate(schedule.get(j))<minDate){
                    minDate = convertDate(schedule.get(j));
                    minTime = convertTime(schedule.get(j));
                    minID = j;
                }
                else if(convertDate(schedule.get(j)) == minDate){
                    if(convertTime(schedule.get(j)) < minTime){
                        minDate = convertDate(schedule.get(j));
                        minTime = convertTime(schedule.get(j));
                        minID = j;
                    }
                }
            }
            updatedSchedule.add(schedule.get(minID));
            updatedIDs.add((ids.get(minID)));
            schedule.remove(minID);
            ids.remove(minID);
        }
        schedule = updatedSchedule;
        ids = updatedIDs;
    }



    public int convertDate(Schedulable item){
        String day = item.getDate().trim().substring(3,5);
        String month = item.getDate().trim().substring(0, 2);
        String year = item.getDate().trim().substring(6);
        String total = year + month + day;
        int dateInt = Integer.parseInt(total);
        return dateInt;
    }

    public int convertTime(Schedulable item){
        String time = item.getTime();
        String converted;
        if(time.contains("AM")){
            converted = time.substring(0,2) + time.substring(3, 5);
            int convInt = Integer.parseInt(converted);
            if(convInt >= 1200){
                convInt += 1200;
            }
            return convInt;
        }
        else{
            converted = time.substring(0,2) + time.substring(3, 5);
            int convInt = Integer.parseInt(converted);
            if(!(convInt>=1200)){
                convInt += 1200;
            }
            return convInt;
        }
    }

}
