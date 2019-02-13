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
            longitude = json.getDouble("longitude");
            latitude = json.getDouble("latitude");
            id = json.getInt("id");
            name = json.getString("name");
            wifi = json.getBoolean("wifi");
            vendorSpots = json.getInt("vendorspots");
            takenSpots = json.getInt("takenspots");
            power = json.getBoolean("power");
            outdoors = json.getBoolean("outdoors");
            tables = json.getBoolean("tables");
            food = json.getBoolean("food");
            creator = json.getString("creator");
            city = json.getString("city");
            street = json.getString("street");
            state = json.getString("state");
            zipcode = json.getString("zipcode");
            String temp = json.getString("start");
            startTime = temp.substring(0, temp.indexOf(" "));
            String temp2 = json.getString("end");
            endTime = temp2.substring(0,temp2.indexOf(" "));
            startDate = temp.substring(temp.indexOf(" ") + 1);
            temp = json.getString("end");
            endTime = temp.substring(0, temp.indexOf(" "));
            endDate = temp.substring(temp.indexOf(" "));
            description = json.getString("description");
            imgString = json.getString("image");

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

    public String getDescription(){return description;}

    public String getEndTime() { return  endTime;}

    public boolean getFood(){ return  food;}

    public boolean getOutDoors(){return outdoors;}

    public Boolean getPower() { return power; }

    public Boolean getTables() { return tables; }

    public Boolean getWifi() { return wifi; }
}
