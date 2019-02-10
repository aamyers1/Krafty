package com.team6.krafty;

import android.util.Log;

import org.json.JSONObject;

public class Event implements Schedulable {
    private Boolean power, wifi, outdoors,tables,food;
    private String creator, city, street, state, zipcode,startTime, endTime, startDate, endDate, name, imgString, description;
    private int id, vendorSpots, takenSpots;
    private double longitude, latitude;


    public void parseJson(JSONObject json){
        try {
            wifi = json.getBoolean("wifi");
            power = json.getBoolean("power");
            outdoors = json.getBoolean("outdoors");
            tables = json.getBoolean("tables");
            food = json.getBoolean("food");
            creator = json.getString("creator");
            city = json.getString("city");
            street = json.getString("street");
            state = json.getString("state");
            zipcode = json.getString("zipcode");
            name = json.getString("name");
            String temp = json.getString("start");
            startTime = temp.substring(0, temp.indexOf(" "));
            startDate = temp.substring(temp.indexOf(" ") + 1);
            longitude = json.getDouble("longitude");
            latitude = json.getDouble("latitude");
        }
        catch(Exception e){
            Log.d("Error event parse", "Event could not be parsed from json");
        }
    }


    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    @Override
    public String getDate() {
        return startDate;
    }

    @Override
    public String getTime() {
        return startTime;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public int getID() {
        return id;
    }
}
