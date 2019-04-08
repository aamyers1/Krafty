package com.team6.krafty;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

public class Event implements Schedulable {
    private Boolean power, wifi, outdoors, tables, food;
    private String creator, city, street, state, zipcode, startTime, endTime, startDate, endDate, name, imgString = "", description;
    private int id, vendorSpots, takenSpots;
    private double longitude, latitude;
    private HashMap<String, String> krafters;

    Event(){}

    Event(String imgString, String name,String startDate,String endDate,String startTime,String endTime,
          int vendorSpots, String street,String city, String state, String zipcode, Double latitude, Double longitude,
          String description,Boolean outdoors,Boolean power, Boolean food,
          Boolean wifi,Boolean tables){

        this.imgString = imgString;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.vendorSpots = vendorSpots;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.outdoors = outdoors;
        this.power = power;
        this.food = food;
        this.wifi = wifi;
        this.tables = tables;
        this.takenSpots = 0;
    }

    //creates a simple json string. The default json generator does not work properly with our API.
    public String createJson() {
        String start = getStartTime() + " " + getStartDate();
        String end = getEndTime()+ " " + getEndDate();

        String outdoors = Boolean.toString(getOutDoors()).substring(0, 1).toUpperCase() + Boolean.toString(getOutDoors()).substring(1);
        String power = Boolean.toString(getPower()).substring(0, 1).toUpperCase() + Boolean.toString(getPower()).substring(1);
        String food = Boolean.toString(getFood()).substring(0, 1).toUpperCase() + Boolean.toString(getFood()).substring(1);
        String wifi = Boolean.toString(getWifi()).substring(0, 1).toUpperCase() + Boolean.toString(getWifi()).substring(1);
        String tables = Boolean.toString(getTables()).substring(0, 1).toUpperCase() + Boolean.toString(getTables()).substring(1);


        return "id="+getID() + "&creator=" + getCreator()+ "&name=" + getName() + "&start=" + start + "&end=" + end +
                "&street=" + getStreet() + "&city=" + getCity() + "&state=" + getState() + "&zipcode=" + getZipCode() +
                "&description=" +getDescription() + "&vendorspots=" + getVendorSpots()+ "&outdoors=" + outdoors + "&takenspots=" + getTakenSpots() + "&longitude=" +getLongitude()+
                "&Takenspots="+getTakenSpots()+"&latitude=" +getLatitude() +
                "&power=" + power+ "&food=" +food+ "&wifi=" + wifi + "&tables=" +tables +
                "&image=" + imgString;
    }

    public void parseJson(JSONObject json){
        try {
            longitude = json.getDouble("longitude");
            latitude = json.getDouble("latitude");
            name = json.getString("name");
            wifi = json.getBoolean("wifi");
            power = json.getBoolean("power");
            outdoors = json.getBoolean("outdoors");
            tables = json.getBoolean("tables");
            food = json.getBoolean("food");
            id = json.getInt("id");
            vendorSpots = json.getInt("vendorspots");
            takenSpots = json.getInt("takenspots");
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
            if(json.has("image")) {
                imgString = json.getString("image");
            }
        }
        catch(Exception e){
            Log.d("Error event parse", "Event could not be parsed from json");
        }
    }



    public void setID(int toID) {
        id = toID;
    }

    public String getCreator() { return creator; }

    public String getName() { return name; }

    public String getStreet() { return street; }

    public String getState() { return state; }

    public String getCity(){ return city; }

    public String getZipCode() { return zipcode; }

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() { return startTime; }

    public String getEndTime() { return endTime; }

    @Override
    public String getDate() {
        return startDate;
    }

    @Override
    public String getTime() {
        return startTime;
    }

    public String getTitle() {
        return name;
    }

    public int getType() {
        return 1;
    }

    public int getID() {
        return id;
    }

    public String getDescription(){ return description; }

    public String getAddress(){
        String str = street + "\n" + city + ", " + state + ", " + zipcode;
        return  str;
    }

    public int getVendorSpots() { return vendorSpots; }

    public int getTakenSpots() { return takenSpots; }

    public boolean getFood(){ return  food;}

    public boolean getOutDoors(){return outdoors;}

    public Boolean getPower() { return power; }

    public Boolean getTables() { return tables; }

    public Boolean getWifi() { return wifi; }

    public String getImgString() { return imgString; }

    public void setId(int id){
        this.id = id;
    }

    public void setKrafters(HashMap<String, String> theKrafters){ krafters = (HashMap<String, String>) theKrafters.clone(); }

    public  HashMap<String, String> getKrafters(){ return krafters; }

    public boolean usernameIsScheduled(String username) {
        if (getKrafters() == null) {
            Log.d("EVENT", "krafters was null");
            return false;
        } else {
            return getKrafters().containsKey(username);
        }
    }
}